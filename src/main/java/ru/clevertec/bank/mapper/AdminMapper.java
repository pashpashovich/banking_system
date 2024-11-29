package ru.clevertec.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.bank.domain.AdminDTO;
import ru.clevertec.bank.entity.Admin;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(source = "mobilePhone", target = "mobilePhone")
    AdminDTO toDomain(Admin admin);
}
