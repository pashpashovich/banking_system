package ru.clevertec.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.Client;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAccountsByClient(Client client);
    Account findAccountByAccountNum(Long accountNum);

    @Query("SELECT c.income, COUNT(a) FROM Account a JOIN a.client c GROUP BY c.income")
    List<Object[]> findClientsIncomeAndAccountCount();

    List<Account> findByClientId(Long clientId);
}
