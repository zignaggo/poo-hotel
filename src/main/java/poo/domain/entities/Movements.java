package poo.domain.entities;

import java.util.Date;

public class Movements extends BaseEntity{ 
  private int reservationId;
  private MovementsEnum type;
  private Date date;
  private Double amount;

  public Movements(int id, int reservationId, MovementsEnum type, Date date, Double amount) {
    super(id);
    this.reservationId = reservationId;
    this.type = type;
    this.date = date;
    this.amount = amount;
  }

  public int getReservationId() {
    return this.reservationId;
  }

  public void setReservationId(int reservationId) {
    this.reservationId = reservationId;
  }

  public MovementsEnum getType() {
    return this.type;
  }

  public void setType(MovementsEnum type) {
    this.type = type;
  }

  public Date getDate() {
    return this.date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Double getAmount() {
    return this.amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "Movements {" +
        "reservationId=" + this.reservationId +
        ", type=" + this.type +
        ", date=" + this.date +
        ", amount=" + this.amount +
        '}';
  }

}
