package by.bsuir.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.bank.domain.AdminUpdateRequest;
import by.bsuir.bank.domain.ClientUpdateRequest;
import by.bsuir.bank.domain.UserDto;
import by.bsuir.bank.domain.UserResponse;
import by.bsuir.bank.entity.Admin;
import by.bsuir.bank.entity.Client;
import by.bsuir.bank.entity.User;
import by.bsuir.bank.entity.enumeration.Role;
import by.bsuir.bank.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordResetService passwordResetService;
  @InjectMocks
  private UserService userService;

  private User user;

  @BeforeEach
  void setup() {
    user = User.builder()
        .id(1L)
        .email("test@example.com")
        .login("testuser")
        .password("pass")
        .firstName("Test")
        .secondName("User")
        .patronymicName("Testovich")
        .role(Role.CLIENT)
        .isActive(true)
        .build();
  }

  @Test
  void shouldCheckLoginAvailability() {
    when(userRepository.existsUserByLogin("freeLogin")).thenReturn(false);
    assertThat(userService.isLoginAvailable("freeLogin")).isTrue();
  }

  @Test
  void shouldCheckEmailAvailability() {
    when(userRepository.existsUserByEmail("email@example.com")).thenReturn(false);
    assertThat(userService.isEmailAvailable("email@example.com")).isTrue();
  }

  @Test
  void shouldUploadAvatar() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    userService.uploadAvatar(1L, "base64img");
    verify(userRepository).save(user);
    assertThat(user.getAvatar()).isEqualTo("base64img");
  }

  @Test
  void shouldGetAvatar() {
    user.setAvatar("imgdata");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    assertThat(userService.getAvatar(1L)).isEqualTo("imgdata");
  }

  @Test
  void shouldSaveUser() {
    userService.save(user);
    verify(userRepository).save(user);
  }

  @Test
  void shouldGetAllUsers() {
    User admin = new User();
    admin.setId(2L);
    admin.setEmail("admin@mail.com");
    admin.setRole(Role.ADMIN);

    when(userRepository.findAll()).thenReturn(List.of(user, admin));
    List<UserResponse> result = userService.getAllUsers();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void shouldGetAllUsersForDir() {
    when(userRepository.findAll()).thenReturn(List.of(user));
    List<UserDto> result = userService.getAllUsersForDir();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void shouldDeleteUser() {
    when(userRepository.existsById(1L)).thenReturn(true);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    userService.deleteUser(1L);
    verify(passwordResetService).deleteByUserId(user);
    verify(userRepository).deleteById(1L);
  }

  @Test
  void shouldUpdateUserStatus() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    userService.updateUserStatus(1L, false);
    verify(userRepository).save(user);
    assertThat(user.isActive()).isFalse();
  }

  @Test
  void shouldUpdateClientDetails() {
    Client client = new Client();
    client.setId(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(client));

    ClientUpdateRequest request = new ClientUpdateRequest();
    request.setFirstName("New");
    request.setSecondName("Name");
    request.setPatronymicName("Patro");
    request.setAddress("Street 1");
    request.setMobilePhone("123");
    request.setIncome(1234.0);

    userService.updateClientDetails(1L, request);
    verify(userRepository).save(client);
    assertThat(client.getFirstName()).isEqualTo("New");
  }

  @Test
  void shouldUpdateAdminDetails() {
    Admin admin = new Admin();
    admin.setId(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

    AdminUpdateRequest request = new AdminUpdateRequest();
    request.setFirstName("A");
    request.setSecondName("B");
    request.setPatronymicName("C");
    request.setMobilePhone("987");

    userService.updateAdminDetails(1L, request);
    verify(userRepository).save(admin);
    assertThat(admin.getFirstName()).isEqualTo("A");
  }

  @Test
  void shouldChangeRoleToClient() {
    User old = user;
    when(userRepository.findById(1L)).thenReturn(Optional.of(old));

    ClientUpdateRequest request = new ClientUpdateRequest();
    request.setFirstName("New");
    request.setSecondName("Name");
    request.setPatronymicName("Patro");
    request.setAddress("Street");
    request.setMobilePhone("123");
    request.setIncome(1234.0);

    userService.changeRoleToClient(1L, request);

    verify(userRepository).delete(old);
    verify(passwordResetService).deleteByUserId(old);
    verify(userRepository).flush();
    verify(userRepository).save(any(Client.class));
  }

  @Test
  void shouldChangeRoleToAdmin() {
    User old = user;
    when(userRepository.findById(1L)).thenReturn(Optional.of(old));

    AdminUpdateRequest request = new AdminUpdateRequest();
    request.setFirstName("A");
    request.setSecondName("B");
    request.setPatronymicName("C");
    request.setMobilePhone("987");

    userService.changeRoleToAdmin(1L, request);

    verify(userRepository).delete(old);
    verify(passwordResetService).deleteByUserId(old);
    verify(userRepository).flush();
    verify(userRepository).save(any(Admin.class));
  }
}
