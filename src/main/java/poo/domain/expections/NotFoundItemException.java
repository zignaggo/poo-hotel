package poo.domain.expections;

public class NotFoundItemException extends Exception {
  public NotFoundItemException() {
    super("Not found Item");
  }
}
