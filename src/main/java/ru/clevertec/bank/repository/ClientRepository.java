package ru.clevertec.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.bank.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {

    Client findClientById(Long id);
}
