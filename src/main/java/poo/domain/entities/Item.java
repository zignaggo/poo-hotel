package poo.domain.entities;

public class Item extends BaseEntity {
  private String type;
  private int availableQuantity;
  private double price;
  private String name;
  private String description;

  public Item(String type, int availableQuantity, double price, String name, String description) {
    this.type = type;
    this.availableQuantity = availableQuantity;
    this.price = price;
    this.name = name;
    this.description = description;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getAvailableQuantity() {
    return this.availableQuantity;
  }

  public void setAvailableQuantity(int availableQuantity) {
    this.availableQuantity = availableQuantity;
  }

  public double getPrice() {
    return this.price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  
  @Override
  public String toString() {
    return "Item {" +
        "type=" + this.type +
        ", availableQuantity=" + this.availableQuantity +
        ", price=" + this.price +
        ", name=" + this.name +
        ", description=" + this.description +
        '}';
  }
}
