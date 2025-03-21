package poo.domain.exceptions;

public class GuestException extends Exception{
  public GuestException(String message) {
    super(message);
  }

  public GuestException(String message, Throwable cause) {
    super(message, cause);
  }
}
