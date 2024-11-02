package ru.clevertec.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.clevertec.bank.entity.enumeration.Role;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private String email;
    private String firstName;
    private String secondName;
    private String patronymicName;
    private Role role;
    private String address;
    private String mobilePhone;
    private double income;
}
