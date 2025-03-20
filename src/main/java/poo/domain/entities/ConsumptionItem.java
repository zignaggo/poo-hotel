package poo.domain.entities;

public class ConsumptionItem {
  private int consumptionId;
  private int itemId;
  private int quantity;
  private double price;
  private Item item;

  public ConsumptionItem(int consumptionId, int itemId, int quantity, double price) {
    this.consumptionId = consumptionId;
    this.itemId = itemId;
    this.quantity = quantity;
    this.price = price;
  }
  public ConsumptionItem(int consumptionId, int itemId, int quantity, double price, Item item) {
    this.consumptionId = consumptionId;
    this.itemId = itemId;
    this.quantity = quantity;
    this.price = price;
    this.item = item;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getConsumptionId() {
    return consumptionId;
  }

  public void setConsumptionId(int consumptionId) {
    this.consumptionId = consumptionId;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  @Override
  public String toString() {
    return String.format("\n       Item ID: %d | Quantity: %d | Price: %.2f | ItemName: %s", this.itemId, this.quantity, this.price, this.item.getName());
  }
}
