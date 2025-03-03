package poo.domain.entities;

import java.util.ArrayList;
import java.util.Date;

public class Reservation {
  private int id;
  private Guest guest;
  private ArrayList<Room> rooms;
  private Date checkIn;
  private Date checkOut;
  private ReservationEnum status;
  private Double amount;

  public Reservation(Guest guest, ArrayList<Room> rooms, Date checkIn, Date checkOut) {
    this.guest = guest;
    this.rooms = rooms;
    this.checkIn = checkIn;
    this.checkOut = checkOut;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Guest getGuest() {
    return guest;
  }

  public void setGuest(Guest guest) {
    this.guest = guest;
  }

  public ArrayList<Room> getRooms() {
    return rooms;
  }

  public void setRooms(ArrayList<Room> rooms) {
    this.rooms = rooms;
  }

  public Date getCheckIn() {
    return checkIn;
  }

  public void setCheckIn(Date checkIn) {
    this.checkIn = checkIn;
  }

  public Date getCheckOut() {
    return checkOut;
  }

  public void setCheckOut(Date checkOut) {
    this.checkOut = checkOut;
  }

  public ReservationEnum getStatus() {
    return status;
  }

  public void setStatus(ReservationEnum status) {
    this.status = status;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

}
