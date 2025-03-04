package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import poo.domain.entities.Guest;

public class GuestDao extends BaseDao<Guest> {

  public GuestDao(Connection connection) {
    super(connection);
  }

  public void create(Guest guest) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
        "INSERT INTO guests (full_name, cpf, email, phone, address, birth_date) VALUES (?, ?, ?, ?, ?, ?)");
    stmt.setString(1, guest.getFullName());
    stmt.setString(2, guest.getCpf());
    stmt.setString(3, guest.getEmail());
    stmt.setString(4, guest.getPhone());
    stmt.setString(5, guest.getAddress());
    stmt.setDate(6, new java.sql.Date(guest.getBirthDate().getTime()));
    stmt.execute();
    stmt.close();
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
  }

  public Guest findById(Integer id) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM guests WHERE id = ?");
    stmt.setInt(1, id);
    ResultSet rs = stmt.executeQuery();
    if (!rs.next())
      return null;
    Guest guest = new Guest(
        rs.getInt("id"),
        rs.getString("cpf"),
        rs.getString("full_name"),
        rs.getString("email"),
        rs.getString("phone"),
        rs.getString("address"),
        rs.getDate("birth_date"));
    stmt.close();
    return guest;
  }

  public ArrayList<Guest> findAll() throws SQLException {
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
              rs.getDate("birth_date")));
    stmt.close();
    return guests;
  }
}
