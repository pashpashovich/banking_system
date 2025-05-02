package by.bsuir.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import by.bsuir.bank.domain.AdminDTO;
import by.bsuir.bank.entity.Admin;
import by.bsuir.bank.entity.enumeration.Role;
import by.bsuir.bank.mapper.AdminMapper;
import by.bsuir.bank.repository.AdminRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AdminServiceTest {

  @Mock
  private AdminRepository adminRepository;

  @Mock
  private AdminMapper adminMapper;

  @InjectMocks
  private AdminService adminService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldReturnAdminDTOWhenAdminExists() {
    // Given
    Admin admin = new Admin();
    admin.setId(1L);
    admin.setEmail("admin@example.com");
    admin.setFirstName("John");
    admin.setSecondName("Doe");
    admin.setPatronymicName("Ivanovich");
    admin.setRole(Role.ADMIN);
    admin.setMobilePhone("123456789");

    AdminDTO expectedDto = new AdminDTO()
        .setId(1L)
        .setEmail("admin@example.com")
        .setFirstName("John")
        .setSecondName("Doe")
        .setPatronymicName("Ivanovich")
        .setRole(Role.ADMIN)
        .setMobilePhone("123456789");

    when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
    when(adminMapper.toDomain(admin)).thenReturn(expectedDto);

    // When
    AdminDTO result = adminService.getAdminById(1L);

    // Then
    assertNotNull(result);
    assertEquals("admin@example.com", result.getEmail());
    assertEquals(Role.ADMIN, result.getRole());
    verify(adminRepository).findById(1L);
    verify(adminMapper).toDomain(admin);
  }

  @Test
  void shouldReturnNullWhenAdminNotFound() {
    // Given
    when(adminRepository.findById(999L)).thenReturn(Optional.empty());

    // When
    AdminDTO result = adminService.getAdminById(999L);

    // Then
    assertNull(result);
    verify(adminRepository).findById(999L);
    verifyNoInteractions(adminMapper);
  }
}
