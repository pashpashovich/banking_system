package by.bsuir.bank.repository;

import by.bsuir.bank.entity.Account;
import by.bsuir.bank.entity.Client;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  List<Account> findAccountsByClient(Client client);

  Account findAccountByAccountNum(Long accountNum);

  @Query("SELECT c.income, COUNT(a) FROM Account a JOIN a.client c GROUP BY c.income")
  List<Object[]> findClientsIncomeAndAccountCount();

  List<Account> findByClientId(Long clientId);
}
