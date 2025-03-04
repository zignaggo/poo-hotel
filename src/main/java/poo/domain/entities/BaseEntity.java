package poo.domain.entities;

public abstract class BaseEntity {
  private int id;

  public BaseEntity() {
  }

  public BaseEntity(int id) {
    this.id = id;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public abstract String toString();
}