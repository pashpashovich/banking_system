package by.bsuir.bank.mapper;

import by.bsuir.bank.domain.DirectorDto;
import by.bsuir.bank.entity.Director;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface DirectorMapper {

  @Mapping(source = "mobilePhone", target = "mobilePhone")
  DirectorDto toDomain(Director admin);
}
