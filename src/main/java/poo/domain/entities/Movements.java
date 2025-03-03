package poo.domain.entities;

import java.util.Date;

public class Movements {
  private int id;
  private int reservationId;
  private MovementsEnum type;
  private Date date;
  private Double amount;

  public Movements(int reservationId, MovementsEnum type, Date date, Double amount) {
    this.reservationId = reservationId;
    this.type = type;
    this.date = date;
    this.amount = amount;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getReservationId() {
    return reservationId;
  }

  public void setReservationId(int reservationId) {
    this.reservationId = reservationId;
  }

  public MovementsEnum getType() {
    return type;
  }

  public void setType(MovementsEnum type) {
    this.type = type;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

}
