package by.bsuir.bank.service;

import by.bsuir.bank.domain.AdminDTO;
import by.bsuir.bank.entity.Admin;
import by.bsuir.bank.mapper.AdminMapper;
import by.bsuir.bank.repository.AdminRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final AdminRepository adminRepository;
  private final AdminMapper adminMapper;

  public AdminDTO getAdminById(Long id) {
    Optional<Admin> admin = adminRepository.findById(id);
    return admin.map(adminMapper::toDomain).orElse(null);
  }

}
