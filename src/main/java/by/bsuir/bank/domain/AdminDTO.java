package by.bsuir.bank.domain;

import by.bsuir.bank.entity.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

  private Long id;
  private String email;
  private String firstName;
  private String secondName;
  private String patronymicName;
  private Role role;
  private String mobilePhone;
}
