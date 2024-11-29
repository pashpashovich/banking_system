package ru.clevertec.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String secondName;
    private String patronymicName;
    private String role;
    private boolean isActive;
}
