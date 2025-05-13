package by.bsuir.bank.repository;

import by.bsuir.bank.entity.LoanRequest;
import by.bsuir.bank.entity.enumeration.LoanStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
  List<LoanRequest> findByClientId(Long clientId);
  boolean existsByClientIdAndStatus(Long clientId, LoanStatus status);
}

