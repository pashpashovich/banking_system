package by.bsuir.bank.mapper;

import by.bsuir.bank.domain.LoanRequestDTO;
import by.bsuir.bank.entity.LoanRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanRequestMapper {


  @Mapping(source = "status.displayName", target = "status")
  @Mapping(source = "client.id", target = "clientId")
  LoanRequestDTO toDto(LoanRequest request);

  List<LoanRequestDTO> toDtoList(List<LoanRequest> list);

}

