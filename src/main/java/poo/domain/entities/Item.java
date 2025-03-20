package poo.domain.entities;

public class Item extends BaseEntity {
  private String type;
  private int availableQuantity;
  private double price;
  private String name;
  private String description;

  public Item(int id, String name, double price) {
    super(id);
    this.price = price;
    this.name = name;
  }
  public Item(String type, String name, String description, int availableQuantity, double price) {
    this.type = type;
    this.availableQuantity = availableQuantity;
    this.price = price;
    this.name = name;
    this.description = description;
  }

  public Item(int id, String type, String name, String description, int availableQuantity, double price) {
    super(id);
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
    return String.format(
        """
            |----- Item #%d -----|
            | Name: %s
            | Type: %s
            | Price: $%.2f
            | Available Quantity: %d
            | Description: %s
            |--------------------|
                    """,
        this.getId(),
        this.name,
        this.type,
        this.price,
        this.availableQuantity,
        this.description);
  }
}
