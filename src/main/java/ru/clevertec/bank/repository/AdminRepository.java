package ru.clevertec.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.bank.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}
