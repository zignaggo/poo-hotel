package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import poo.domain.entities.Guest;
import poo.domain.expections.AlreadyExistsGuest;
import poo.infra.GuestDao;
import poo.utils.Getter;

public class GuestService {
  private GuestDao guestDao;

  public GuestService(GuestDao guestDao) {
    this.guestDao = guestDao;
  }

  public void list() throws SQLException{
    ArrayList<Guest> guests = guestDao.find();
    if (guests.isEmpty()) {
      System.out.println("No guests found");
      return;
    }
    guests.forEach(guest -> System.out.println(guest.toString()));
  }

  public void create(Connection connection, Getter getter) throws AlreadyExistsGuest, SQLException {
    System.out.println("Creating guest\n");
    GuestDao guestDao = new GuestDao(connection);
    String name = getter.getString("Name: ");
    String cpf = getter.getString("CPF: ", "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", "Invalid CPF");
    Optional<Guest> existingGuest = guestDao.find(cpf);
    System.out.println(existingGuest.toString());
    if (existingGuest.isPresent()) {
      throw new AlreadyExistsGuest(name, cpf);
    }
    String email = getter.getString("Email: ", "^\\w+@\\w+\\.\\w+$", "Invalid email");
    String phone = getter.getString("Phone: ", "^\\d{2}\\d{4}-\\d{4}$", "Invalid phone");
    String address = getter.getString("Address: ");
    Date birthDate = getter.getDate("Birth date: ");
    Guest guest = new Guest(cpf, name, email, phone, address, birthDate);
    guestDao.create(guest);
    System.out.println("Guest created successfully\n");
    System.out.println(guest.toString());
  }
}
