package poo.domain.entities;

import java.util.ArrayList;
import java.util.Date;

public class Reservation extends BaseEntity {
  private String guestCpf;
  private ArrayList<Room> rooms;
  private Date checkIn;
  private Date checkOut;
  private ReservationEnum status;
  private int numberOfGuests;
  private Double amount;
  private String paymentMethod;
  private Guest guest;

  public Reservation(int id, String guestCpf, Date checkIn, Date checkOut, ReservationEnum status, int numberOfGuests, Double amount, String paymentMethod, Guest guest) {
    super(id);
    this.guestCpf = guestCpf;
    this.rooms = new ArrayList<Room>();
    this.checkIn = checkIn;
    this.checkOut = checkOut;
    this.status = status;
    this.numberOfGuests = numberOfGuests;
    this.amount = amount;
    this.paymentMethod = paymentMethod;
    this.guest = guest;
  }

  public Reservation(String guestCpf, Date checkIn, Date checkOut, ReservationEnum status, int numberOfGuests, Double amount, String paymentMethod) {
    this.guestCpf = guestCpf;
    this.rooms = new ArrayList<Room>();
    this.checkIn = checkIn;
    this.checkOut = checkOut;
    this.status = status;
    this.numberOfGuests = numberOfGuests;
    this.amount = amount;
    this.paymentMethod = paymentMethod;
  }

  public Reservation(int id, String guestCpf, Date checkIn, Date checkOut, ReservationEnum status, int numberOfGuests, Double amount, String paymentMethod) {
    super(id);
    this.guestCpf = guestCpf;
    this.rooms = new ArrayList<Room>();
    this.checkIn = checkIn;
    this.checkOut = checkOut;
    this.status = status;
    this.numberOfGuests = numberOfGuests;
    this.amount = amount;
    this.paymentMethod = paymentMethod;
  }

  public String getGuestCpf() {
    return this.guestCpf;
  }

  public void setGuestCpf(String guestCpf) {
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

  public int getNumberOfGuests() {
    return this.numberOfGuests;
  }

  public void setNumberOfGuests(int numberOfGuests) {
    this.numberOfGuests = numberOfGuests;
  }

  public String getPaymentMethod() {
    return this.paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public Guest getGuest() {
    return this.guest;
  }

  public void setGuest(Guest guest) {
    this.guest = guest;
  }

  @Override
  public String toString() {
    return String.format(
        """
|----- Reservation #%d -----|
| Guest: %s
| CPF: %s
| Check-in: %s
| Check-out: %s
| Status: %s
| Number of Guests: %d
| Amount: $%.2f
| Payment Method: %s
|---------------------------|
        """,
        this.getId(),
        this.guest != null ? this.guest.getFullName() : "N/A",
        this.guestCpf,
        this.checkIn,
        this.checkOut,
        this.status,
        this.numberOfGuests,
        this.amount,
        this.paymentMethod);
  }
}
