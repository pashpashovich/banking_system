package ru.clevertec.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.clevertec.bank.domain.TransactionDTO;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.Transaction;

import java.util.List;

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
