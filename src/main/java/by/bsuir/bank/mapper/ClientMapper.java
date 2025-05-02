package by.bsuir.bank.mapper;

import by.bsuir.bank.domain.ClientDto;
import by.bsuir.bank.entity.Client;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
