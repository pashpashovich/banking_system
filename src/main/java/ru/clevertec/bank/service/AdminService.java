package ru.clevertec.bank.service;

import org.springframework.stereotype.Service;
import ru.clevertec.bank.domain.AdminDTO;
import ru.clevertec.bank.entity.Admin;
import ru.clevertec.bank.mapper.AdminMapper;
import ru.clevertec.bank.repository.AdminRepository;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    public AdminService(AdminRepository adminRepository, AdminMapper adminMapper) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
    }

    public AdminDTO getAdminById(Long id) {
        Optional<Admin> admin = adminRepository.findById(id);
        return admin.map(adminMapper::toDomain).orElse(null);
    }

}
