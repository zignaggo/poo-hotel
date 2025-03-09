package poo.domain.expections;

public class AlreadyExistsGuest extends Exception{
  public AlreadyExistsGuest(String name, String cpf) {
    super("This guest already exists: " + name + " - " + cpf);
  }
}
