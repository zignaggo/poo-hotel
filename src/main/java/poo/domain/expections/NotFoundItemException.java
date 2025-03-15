package poo.domain.expections;

public class NotFoundItemException extends Exception {
  public NotFoundItemException(String itemName) {
    super("Not found Item: "+ itemName);
  }
}
