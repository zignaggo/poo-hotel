package poo.domain.entities;

import java.util.Date;

public class Guest extends BaseEntity {
  private String cpf;
  private String fullName;
  private String email;
  private String phone;
  private String address;
  private Date birthDate;

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

  @Override
  public String toString() {
    return "Guest {" +
        "cpf='" + this.cpf + '\'' +
        ", id='" + this.getId() + '\'' +
        ", fullName='" + this.fullName + '\'' +
        ", email='" + this.email + '\'' +
        ", phone='" + this.phone + '\'' +
        ", address='" + this.address + '\'' +
        ", birthDate=" + this.birthDate +
        '}';
  }
}
