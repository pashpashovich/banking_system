package by.bsuir.bank.domain;

import lombok.Data;

@Data
public class ClientUpdateRequest {

  private String firstName;
  private String secondName;
  private String patronymicName;
  private String mobilePhone;
  private String address;
  private double income;
}
