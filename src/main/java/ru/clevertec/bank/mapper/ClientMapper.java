package ru.clevertec.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.bank.domain.ClientDto;
import ru.clevertec.bank.entity.Client;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "address", target = "address")
    @Mapping(source = "mobilePhone", target = "mobilePhone")
    @Mapping(source = "income", target = "income")
    ClientDto toDomain(Client client);

    @Mapping(source = "address", target = "address")
    @Mapping(source = "mobilePhone", target = "mobilePhone")
    @Mapping(source = "income", target = "income")
    List<ClientDto> toDomains(List<Client> clients);
}
