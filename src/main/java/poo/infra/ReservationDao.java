package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import poo.domain.entities.Guest;
import poo.domain.entities.Reservation;
import poo.domain.entities.ReservationEnum;

public class ReservationDao extends BaseDao<Reservation> {
  public ReservationDao(Connection connection) {
    super(connection);
  }

  public Reservation create(Reservation reservation) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
        "INSERT INTO reservations (guest_cpf, status, amount, number_of_guests, payment_method, check_in_date, check_out_date) VALUES (?, ?::reservation_status, ?, ?, ?, ?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS);
    stmt.setString(1, reservation.getGuestCpf());
    stmt.setString(2, reservation.getStatus().name());
    stmt.setDouble(3, reservation.getAmount());
    stmt.setInt(4, reservation.getNumberOfGuests());
    stmt.setString(5, reservation.getPaymentMethod());
    stmt.setDate(6, new java.sql.Date(reservation.getCheckIn().getTime()));
    stmt.setDate(7, new java.sql.Date(reservation.getCheckOut().getTime()));
    stmt.execute();
    ResultSet rs = stmt.getGeneratedKeys();
    if (!rs.next())
      throw new SQLException("Failed to create reservation");
    reservation.setId(rs.getInt(1));
    stmt.close();
    return reservation;
  }

  public void update(Reservation reservation) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
        "UPDATE reservations SET guest_cpf = ?, status = ?::reservation_status, amount = ?, number_of_guests = ?, payment_method = ?, check_in_date = ?, check_out_date = ? WHERE id = ?");
    stmt.setString(1, reservation.getGuestCpf());
    stmt.setString(2, reservation.getStatus().name());
    stmt.setDouble(3, reservation.getAmount());
    stmt.setInt(4, reservation.getNumberOfGuests());
    stmt.setString(5, reservation.getPaymentMethod());
    stmt.setDate(6, new java.sql.Date(reservation.getCheckIn().getTime()));
    stmt.setDate(7, new java.sql.Date(reservation.getCheckOut().getTime()));
    stmt.setInt(8, reservation.getId());
    stmt.execute();
    stmt.close();
  }

  public void delete(Integer id) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM reservations WHERE id = ?");
    stmt.setInt(1, id);
    stmt.execute();
    stmt.close();
  }

  public Optional<Reservation> find(Integer id) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM reservations WHERE id = ?");
    stmt.setInt(1, id);
    ResultSet rs = stmt.executeQuery();
    if (!rs.next())
      return Optional.empty();
    Reservation reservation = new Reservation(
        rs.getInt("id"),
        rs.getString("guest_cpf"),
        rs.getDate("check_in_date"),
        rs.getDate("check_out_date"),
        ReservationEnum.valueOf(rs.getString("status")),
        rs.getInt("number_of_guests"),
        rs.getDouble("amount"),
        rs.getString("payment_method"));
    stmt.close();
    return Optional.of(reservation);
  }

  public ArrayList<Reservation> find() throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM reservations");
    ResultSet rs = stmt.executeQuery();
    ArrayList<Reservation> reservations = new ArrayList<>();
    while (rs.next())
      reservations.add(
          new Reservation(
              rs.getInt("id"),
              rs.getString("guest_cpf"),
              rs.getDate("check_in_date"),
              rs.getDate("check_out_date"),
              ReservationEnum.valueOf(rs.getString("status")),
              rs.getInt("number_of_guests"),
              rs.getDouble("amount"),
              rs.getString("payment_method")));
    stmt.close();
    return reservations;
  }

  public ArrayList<Reservation> find(Boolean hasGuests) throws SQLException {
    PreparedStatement stmt = this.getConnection()
        .prepareStatement("""
              SELECT r.id,
                r.status,
                r.amount,
                r.check_in_date,
                r.check_out_date,
                r.guest_cpf,
                r.lunch,
                r.number_of_guests,
                r.payment_method,
                g.id guest_id,
                g.full_name guest_fullname,
                g.email guest_email,
                g.phone guest_phone,
                g.address guest_address,
                g.birth_date guest_birth_date
              FROM reservations r
              LEFT JOIN guests g ON g.cpf = r.guest_cpf
            """);
    ResultSet rs = stmt.executeQuery();
    ArrayList<Reservation> reservations = new ArrayList<>();
    while (rs.next())
      reservations.add(
          new Reservation(
              rs.getInt("id"),
              rs.getString("guest_cpf"),
              rs.getDate("check_in_date"),
              rs.getDate("check_out_date"),
              ReservationEnum.valueOf(rs.getString("status")),
              rs.getInt("number_of_guests"),
              rs.getDouble("amount"),
              rs.getString("payment_method"),
              new Guest(
                  rs.getInt("guest_id"),
                  rs.getString("guest_cpf"),
                  rs.getString("guest_fullname"),
                  rs.getString("guest_email"),
                  rs.getString("guest_phone"),
                  rs.getString("guest_address"),
                  rs.getDate("guest_birth_date"))));
    stmt.close();
    return reservations;
  }

  public ArrayList<Reservation> find(String guestCpf) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM reservations WHERE guest_cpf = ?");
    stmt.setString(1, guestCpf);
    ResultSet rs = stmt.executeQuery();
    ArrayList<Reservation> reservations = new ArrayList<>();
    while (rs.next())
      reservations.add(
          new Reservation(
              rs.getInt("id"),
              rs.getString("guest_cpf"),
              rs.getDate("check_in_date"),
              rs.getDate("check_out_date"),
              ReservationEnum.valueOf(rs.getString("status")),
              rs.getInt("number_of_guests"),
              rs.getDouble("amount"),
              rs.getString("payment_method")));
    stmt.close();
    return reservations;
  }
}
