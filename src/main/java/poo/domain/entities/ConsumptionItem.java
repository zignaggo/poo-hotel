package poo.domain.entities;

public class ConsumptionItem {
  private int consumptionId;
  private int itemId;
  private int quantity;

  public ConsumptionItem(int consumptionId, int itemId, int quantity) {
    this.consumptionId = consumptionId;
    this.itemId = itemId;
    this.quantity = quantity;
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

}
