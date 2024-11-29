package ru.clevertec.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.bank.entity.Director;

public interface DirectorRepository extends JpaRepository<Director, Long> {
}
