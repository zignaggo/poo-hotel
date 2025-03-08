package poo.domain.entities;

import java.util.ArrayList;

public class Room extends BaseEntity {
  private int number;
  private int capacity;
  private double pricePerNight;
  private ArrayList<Integer> beds;

  public Room(int id, int number, int capacity, double pricePerNight) {
    super(id);
    this.number = number;
    this.capacity = capacity;
    this.pricePerNight = pricePerNight;
    this.beds = new ArrayList<Integer>();
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

  @Override
  public String toString() {
    return "Room {" +
        "id=" + this.getId() +
        ", number=" + this.number +
        ", capacity=" + this.capacity +
        ", pricePerNight=" + this.pricePerNight +
        '}';
  }
}