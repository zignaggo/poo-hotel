package poo.domain.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Consumption extends BaseEntity {
  private int reservationId;
  private Date date;
  private double total = 0.0;
  private List<ConsumptionItem> items = new ArrayList<>();

  public Consumption(int reservationId, Date date) {
    this.reservationId = reservationId;
    this.date = date;
  }

  public Consumption(int id, int reservationId, Date date) {
    super(id);
    this.reservationId = reservationId;
    this.date = date;
  }

  public Consumption(int id, int reservationId, Date date, double total) {
    super(id);
    this.reservationId = reservationId;
    this.date = date;
    this.total = total;
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

  public double getTotal() {
    return this.total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public List<ConsumptionItem> getItems() {
    return this.items;
  }

  public void setItems(List<ConsumptionItem> items) {
    this.items = items;
  }

  public void addItem(ConsumptionItem item) {
    this.items.add(item);
  }

  @Override
  public String toString() {
    return String.format("""
        |-------------------------------- Consumption #%d --------------------------------|
        | Reservation ID: %d
        | Date: %s
        | Total: %.2f
        | Items: %s
        |---------------------------------------------------------------------------------|
    """, this.getId(), this.reservationId, this.date, this.total, this.items.stream().map(item -> item.toString()).collect(Collectors.joining("")));
  }
}
