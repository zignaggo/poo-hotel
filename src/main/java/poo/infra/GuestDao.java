package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import poo.domain.entities.Guest;

public class GuestDao extends BaseDao<Guest> {

  public GuestDao(Connection connection) {
    super(connection);
  }

  public Guest create(Guest guest) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
        "INSERT INTO guests (full_name, cpf, email, phone, address, birth_date) VALUES (?, ?, ?, ?, ?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS);
    stmt.setString(1, guest.getFullName());
    stmt.setString(2, guest.getCpf());
    stmt.setString(3, guest.getEmail());
    stmt.setString(4, guest.getPhone());
    stmt.setString(5, guest.getAddress());
    stmt.setTimestamp(6, new java.sql.Timestamp(guest.getBirthDate().getTime()));
    stmt.execute();
    
    ResultSet rs = stmt.getGeneratedKeys();
    if (!rs.next())
      throw new SQLException("Failed to create guest");
    guest.setId(rs.getInt(1));
    stmt.close();
    return guest;
  }

  public void update(Guest guest) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
        "UPDATE guests SET full_name = ?, cpf = ?, email = ?, phone = ?, address = ? WHERE id = ?");
    stmt.setString(1, guest.getFullName());
    stmt.setString(2, guest.getCpf());
    stmt.setString(3, guest.getEmail());
    stmt.setString(4, guest.getPhone());
    stmt.setString(5, guest.getAddress());
    stmt.execute();
    stmt.close();
  }

  public void delete(Integer id) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM guests WHERE id = ?");
    stmt.setInt(1, id);
    stmt.execute();
    stmt.close();
  }

  public Optional<Guest> find(Integer id) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM guests WHERE id = ?");
    stmt.setInt(1, id);
    ResultSet rs = stmt.executeQuery();
    if (!rs.next())
      return Optional.empty();
    Guest guest = new Guest(
        rs.getInt("id"),
        rs.getString("cpf"),
        rs.getString("full_name"),
        rs.getString("email"),
        rs.getString("phone"),
        rs.getString("address"),
        rs.getTimestamp("birth_date"));
    stmt.close();
    return Optional.of(guest);
  }

  public Optional<Guest> find(String cpf) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM guests WHERE cpf = ?");
    stmt.setString(1, cpf);
    ResultSet rs = stmt.executeQuery();
    if (!rs.next())
      return Optional.empty();
    Guest guest = new Guest(
        rs.getInt("id"),
        rs.getString("cpf"),
        rs.getString("full_name"),
        rs.getString("email"),
        rs.getString("phone"),
        rs.getString("address"),
        rs.getTimestamp("birth_date"));
    stmt.close();
    return Optional.of(guest);
  }

  public ArrayList<Guest> find() throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM guests");
    ResultSet rs = stmt.executeQuery();
    ArrayList<Guest> guests = new ArrayList<>();
    while (rs.next())
      guests.add(
          new Guest(
              rs.getInt("id"),
              rs.getString("cpf"),
              rs.getString("full_name"),
              rs.getString("email"),
              rs.getString("phone"),
              rs.getString("address"),
              rs.getTimestamp("birth_date")));
    stmt.close();
    return guests;
  }

  public ArrayList<Guest> find(Boolean hasReservations) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
      """
      SELECT count(r.id) reservation_count, g.full_name, g.id, g.cpf, g.email, g.address, g.phone, g.birth_date
      FROM guests g
      LEFT JOIN reservations r ON g.cpf = r.guest_cpf
      group by g.full_name, g.id, g.cpf, g.email, g.address, g.phone, g.birth_date
      order by g.id
      """
    );
    ResultSet rs = stmt.executeQuery();
    ArrayList<Guest> guests = new ArrayList<>();
    while (rs.next())
      guests.add(
          new Guest(
              rs.getInt("id"),
              rs.getString("cpf"),
              rs.getString("full_name"),
              rs.getString("email"),
              rs.getString("phone"),
              rs.getString("address"),
              rs.getTimestamp("birth_date"),
              rs.getInt("reservation_count")));
    stmt.close();
    return guests;
  }
}
