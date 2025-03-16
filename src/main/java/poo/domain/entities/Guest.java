package poo.domain.entities;

import java.util.Date;

public class Guest extends BaseEntity {
  private String cpf;
  private String fullName;
  private String email;
  private String phone;
  private String address;
  private Date birthDate;
  private int numberOfReservations = 0;

  public Guest(String cpf, String fullName, String email, String phone, String address, Date birthDate) {
    this.cpf = cpf;
    this.fullName = fullName;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.birthDate = birthDate;
  }

  public Guest(int id, String cpf, String fullName, String email, String phone, String address, Date birthDate) {
    super(id);
    this.cpf = cpf;
    this.fullName = fullName;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.birthDate = birthDate;
  }

  public Guest(int id, String cpf, String fullName, String email, String phone, String address, Date birthDate,
      int numberOfReservations) {
    super(id);
    this.cpf = cpf;
    this.fullName = fullName;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.birthDate = birthDate;
    this.numberOfReservations = numberOfReservations;
  }

  public String getCpf() {
    return this.cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Date getBirthDate() {
    return this.birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public int getNumberOfReservations() {
    return this.numberOfReservations;
  }

  public void setNumberOfReservations(int numberOfReservations) {
    this.numberOfReservations = numberOfReservations;
  }

  @Override
  public String toString() {
    return String.format("""
        |-------- Guest #%d --------|
        | CPF: %s
        | Full Name: %s
        | Email: %s
        | Phone: %s
        | Address: %s
        | Birth Date: %s
        | Number of Reservations: %d
        |---------------------------|
                """,
        this.getId(),
        this.cpf,
        this.fullName,
        this.email,
        this.phone,
        this.address,
        this.birthDate,
        this.numberOfReservations);
  }
}
