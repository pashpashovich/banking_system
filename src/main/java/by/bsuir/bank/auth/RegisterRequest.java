package by.bsuir.bank.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String login;
  private String email;
  private String password;
  private String firstName;
  private String secondName;
  private String patronymicName;
  private String mobilePhone;
  private String address;
  private double income;
}
