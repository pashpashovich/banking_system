package ru.clevertec.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.bank.domain.DirectorDto;
import ru.clevertec.bank.entity.Director;


@Mapper(componentModel = "spring")
public interface DirectorMapper {
    @Mapping(source = "mobilePhone", target = "mobilePhone")
    DirectorDto toDomain(Director admin);
}
