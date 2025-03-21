package poo.domain.exceptions;

public class NotFoundItemException extends Exception {
  public NotFoundItemException() {
    super("Not found Item");
  }
}
