package by.bsuir.bank.mapper;

import by.bsuir.bank.domain.TransactionDTO;
import by.bsuir.bank.entity.Account;
import by.bsuir.bank.entity.Transaction;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(target = "senderAccount", ignore = true)
  @Mapping(target = "recipientAccount", ignore = true)
  Transaction toEntity(TransactionDTO transactionDTO);

  @Mapping(source = "senderAccount", target = "senderAccountId", qualifiedByName = "accountNumToLong")
  @Mapping(source = "recipientAccount", target = "recipientAccountId", qualifiedByName = "accountNumToLong")
  TransactionDTO toDto(Transaction transaction);


  List<TransactionDTO> toDto(List<Transaction> transactions);

  @Named("accountNumToLong")
  default Long accountNumToLong(Account account) {
    if (account == null || account.getAccountNum() == null) {
      return null;
    }
    try {
      return account.getAccountNum();
    } catch (NumberFormatException e) {
      return null;
    }
  }

}
