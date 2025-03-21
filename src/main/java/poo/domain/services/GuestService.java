package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import poo.domain.entities.Guest;
import poo.domain.exceptions.GuestException;
import poo.infra.GuestDao;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class GuestService {
  private GuestDao guestDao;

  public GuestService(GuestDao guestDao) {
    this.guestDao = guestDao;
  }

  public GuestService(Connection connection) {
    this.guestDao = new GuestDao(connection);
  }
  public GuestService(Connection connection, GuestDao guestDao) {
    this.guestDao = guestDao;
  }

  public ArrayList<Guest> getAllGuests() throws GuestException {
    try {
      return guestDao.find(true);
    } catch (SQLException e) {
      throw new GuestException("Failed to retrieve guests: " + e.getMessage());
    }
  }

  private boolean isGuest18YearsOld(Date birthDate) {
    LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate now = LocalDate.now();
    int age = Period.between(birthLocalDate, now).getYears();
    return age >= 18;
  }

  public Guest create(String name, String cpf, String email, String phone, String address,
      Date birthDate) throws GuestException {
    try {
      Optional<Guest> existingGuest = guestDao.find(cpf);
      if (!isGuest18YearsOld(birthDate)) {
        throw new GuestException("Guest must be at least 18 years old");
      }

      if (existingGuest.isPresent()) {
        Guest guest = existingGuest.get();
        throw new GuestException("Guest already exists: " + guest.getFullName() + " (" + guest.getCpf() + ")");
      }
      return guestDao.create(new Guest(cpf, name, email, phone, address, birthDate));
    } catch (SQLException e) {
      throw new GuestException("Failed to create guest: " + e.getMessage());
    }
  }
}
