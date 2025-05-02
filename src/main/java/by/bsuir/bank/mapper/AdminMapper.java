package by.bsuir.bank.mapper;

import by.bsuir.bank.domain.AdminDTO;
import by.bsuir.bank.entity.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

  @Mapping(source = "mobilePhone", target = "mobilePhone")
  AdminDTO toDomain(Admin admin);
}
