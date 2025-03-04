package poo.domain.entities;

import java.util.Date;

public class Consumption extends BaseEntity {
  private int itemId;
  private int reservationId;
  private Date date;

  public Consumption(int id, int itemId, int reservationId, Date date) {
    super(id);
    this.itemId = itemId;
    this.reservationId = reservationId;
    this.date = date;
  }

  public int getItemId() {
    return this.itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getReservationId() {
    return this.reservationId;
  }

  public void setReservationId(int reservationId) {
    this.reservationId = reservationId;
  }

  public Date getDate() {
    return this.date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return "Consumption [id=" + this.getId() + ", itemId=" + this.itemId + ", reservationId=" + this.reservationId + ", date=" + this.date + "]";
  }
}
