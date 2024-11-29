package ru.clevertec.bank.domain;

import lombok.Data;

@Data
public class AdminUpdateRequest {
    private String firstName;
    private String secondName;
    private String patronymicName;
    private String mobilePhone;
}
