package poo.domain.entities;

public class ReservationRoom {
  private int reservationId;
  private int roomId;
  private double price;


  public ReservationRoom(int reservationId, int roomId, double price) {
    this.reservationId = reservationId;
    this.roomId = roomId;
    this.price = price;
  }

  public ReservationRoom(int reservationId, int roomId) {
    this.reservationId = reservationId;
    this.roomId = roomId;
  }

  public int getReservationId() {
    return reservationId;
  }

  public void setReservationId(int reservationId) {
    this.reservationId = reservationId;
  }

  public int getRoomId() {
    return roomId;
  }

  public void setRoomId(int roomId) {
    this.roomId = roomId;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  @Override
  public String toString() {
    return "ReservationRoom {" +
        "reservationId=" + this.reservationId +
        ", roomId=" + this.roomId +
        '}';
  }
}
