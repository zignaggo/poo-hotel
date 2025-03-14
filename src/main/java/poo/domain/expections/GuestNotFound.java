package poo.domain.expections;

public class GuestNotFound extends Exception {
  public GuestNotFound(String itemName) {
    super("Not found Guest: "+ itemName);
  }
}
