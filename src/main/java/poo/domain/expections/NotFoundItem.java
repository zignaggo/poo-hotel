package poo.domain.expections;

public class NotFoundItem extends Exception {
  public NotFoundItem(String itemName) {
    super("Not found Item: "+ itemName);
  }
}
