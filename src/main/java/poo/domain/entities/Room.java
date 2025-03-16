package poo.domain.entities;

import java.util.ArrayList;

public class Room extends BaseEntity {
  private int number;
  private int capacity;
  private String roomType;
  private double pricePerNight;
  private ArrayList<Integer> beds;

  public Room(int id, int number, int capacity, double pricePerNight, String roomType) {
    super(id);
    this.number = number;
    this.capacity = capacity;
    this.pricePerNight = pricePerNight;
    this.beds = new ArrayList<Integer>();
    this.roomType = roomType;
  }

  public int getNumber() {
    return this.number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public double getPricePerNight() {
    return this.pricePerNight;
  }

  public void setPricePerNight(int pricePerNight) {
    this.pricePerNight = pricePerNight;
  }

  public int getCapacity() {
    return this.capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public ArrayList<Integer> getBeds() {
    return this.beds;
  }

  public void setBeds(ArrayList<Integer> beds) {
    this.beds = beds;
  }

  public double calculatePrice(int nights) {
    return this.pricePerNight * nights;
  }

  public boolean canAccommodate(int numberOfPeople) {
    return numberOfPeople <= this.capacity;
  }

  public String getRoomType() {
    return this.roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }
  
  @Override
  public String toString() {
    return String.format("""
        |-------------Room %d-------------|
        | Number: %d
        | Capacity: %d
        | Price per night: %.2f
        | Room type: %s
        |---------------------------------|
                """,
        this.getId(),
        this.number,
        this.capacity,
        this.pricePerNight,
        this.roomType);
  }
}