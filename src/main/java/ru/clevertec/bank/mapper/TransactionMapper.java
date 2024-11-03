package ru.clevertec.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.bank.domain.TransactionDTO;
import ru.clevertec.bank.entity.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "senderAccount", ignore = true)
    @Mapping(target = "recipientAccount", ignore = true)
    Transaction toEntity(TransactionDTO transactionDTO);

    @Mapping(target = "senderAccountId", ignore = true)
    @Mapping(target = "recipientAccountId", ignore = true)
    TransactionDTO toDto(Transaction transaction);

}
