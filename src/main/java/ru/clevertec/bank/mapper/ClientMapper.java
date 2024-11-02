package ru.clevertec.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.bank.domain.ClientDto;
import ru.clevertec.bank.entity.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "secondName", target = "secondName")
    @Mapping(source = "patronymicName", target = "patronymicName")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "mobilePhone", target = "mobilePhone")
    @Mapping(source = "income", target = "income")
    ClientDto toDomain(Client client);
}
