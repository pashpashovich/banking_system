package by.bsuir.bank.repository;


import by.bsuir.bank.entity.Account;
import by.bsuir.bank.entity.Transaction;
import by.bsuir.bank.entity.enumeration.TransactionType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findBySenderAccountOrRecipientAccount(Account account1, Account account2);

  @Query("SELECT t FROM Transaction t " +
      "WHERE t.transactionTime BETWEEN :transactionTimeStart AND :transactionTimeEnd " +
      "AND (t.recipientAccount = :account OR t.senderAccount = :account)")
  List<Transaction> findTransactionsByTimeRangeAndRecipientOrSender(
      @Param("transactionTimeStart") LocalDateTime transactionTimeStart,
      @Param("transactionTimeEnd") LocalDateTime transactionTimeEnd,
      @Param("account") Account account
  );

  @Query("SELECT t FROM Transaction t " +
      "WHERE t.recipientAccount = :account " +
      "AND (t.transactionType = :transactionType1 OR t.transactionType = :transactionType2) " +
      "AND t.transactionTime < :endDate " +
      "AND t.transactionTime > :startDate")
  List<Transaction> findByRecipientAccountAndTransactionTypeOrTransactionTypeAndTransactionTimeBeforeAndTransactionTimeAfter(
      @Param("account") Account account,
      @Param("transactionType1") TransactionType transactionType1,
      @Param("transactionType2") TransactionType transactionType2,
      @Param("endDate") LocalDateTime endDate,
      @Param("startDate") LocalDateTime startDate
  );

  @Query("SELECT t FROM Transaction t " +
      "WHERE t.senderAccount = :account " +
      "AND (t.transactionType = :transactionType1 OR t.transactionType = :transactionType2) " +
      "AND t.transactionTime < :endDate " +
      "AND t.transactionTime > :startDate")
  List<Transaction> findTransactionsBySenderAccountAndTypesWithinDateRange(
      @Param("account") Account account,
      @Param("transactionType1") TransactionType transactionType1,
      @Param("transactionType2") TransactionType transactionType2,
      @Param("endDate") LocalDateTime endDate,
      @Param("startDate") LocalDateTime startDate
  );

  List<Transaction> findByTransactionTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

  List<Transaction> findByTransactionTimeBetweenAndTransactionType(LocalDateTime transactionTimeStart,
      LocalDateTime transactionTimeEnd, TransactionType transactionType);

  List<Transaction> findTransactionsByTransactionTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
}
