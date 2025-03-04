package poo.domain.entities;

import java.util.ArrayList;
import java.util.Date;

public class Reservation extends BaseEntity {
  private String guestCpf;
  private ArrayList<Room> rooms;
  private Date checkIn;
  private Date checkOut;
  private ReservationEnum status;
  private Double amount;

  public Reservation(int id, String guestCpf, Date checkIn, Date checkOut) {
    super(id);
    this.guestCpf = guestCpf;
    this.rooms = new ArrayList<Room>();
    this.checkIn = checkIn;
    this.checkOut = checkOut;
  }

  public String getGuest() {
    return this.guestCpf;
  }

  public void setGuest(String guestCpf) {
    this.guestCpf = guestCpf;
  }

  public ArrayList<Room> getRooms() {
    return this.rooms;
  }

  public void setRooms(ArrayList<Room> rooms) {
    this.rooms = rooms;
  }

  public Date getCheckIn() {
    return this.checkIn;
  }

  public void setCheckIn(Date checkIn) {
    this.checkIn = checkIn;
  }

  public Date getCheckOut() {
    return this.checkOut;
  }

  public void setCheckOut(Date checkOut) {
    this.checkOut = checkOut;
  }

  public ReservationEnum getStatus() {
    return this.status;
  }

  public void setStatus(ReservationEnum status) {
    this.status = status;
  }

  public Double getAmount() {
    return this.amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "Reservation {" +
        "guestCpf=" + this.guestCpf +
        ", rooms=" + this.rooms +
        ", checkIn=" + this.checkIn +
        ", checkOut=" + this.checkOut +
        ", status=" + this.status +
        ", amount=" + this.amount +
        '}';
  }
}
