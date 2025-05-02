package by.bsuir.bank.service;

import by.bsuir.bank.domain.AdminUpdateRequest;
import by.bsuir.bank.domain.ClientUpdateRequest;
import by.bsuir.bank.domain.UserDto;
import by.bsuir.bank.domain.UserResponse;
import by.bsuir.bank.entity.Admin;
import by.bsuir.bank.entity.Client;
import by.bsuir.bank.entity.User;
import by.bsuir.bank.entity.enumeration.Role;
import by.bsuir.bank.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordResetService passwordResetService;

  public boolean isLoginAvailable(String login) {
    return !userRepository.existsUserByLogin(login);

  }

  public boolean isEmailAvailable(String email) {
    return !userRepository.existsUserByEmail(email);
  }


  public void uploadAvatar(Long id, String base64Image) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
    user.setAvatar(base64Image);
    userRepository.save(user);
  }

  public String getAvatar(Long id) {
    return userRepository.findById(id)
        .map(User::getAvatar)
        .orElse(null);
  }

  public void save(User user) {
    userRepository.save(user);
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
        .filter(user -> !user.getRole().equals(Role.DIRECTOR))
        .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getRole().toString()))
        .collect(Collectors.toList());
  }

  public List<UserDto> getAllUsersForDir() {
    return userRepository.findAll().stream()
        .filter(user -> !user.getRole().equals(Role.DIRECTOR))
        .map(user -> new UserDto(user.getId(), user.getEmail(), user.getFirstName(), user.getSecondName(),
            user.getPatronymicName(), user.getRole().toString(), user.isActive()))
        .collect(Collectors.toList());
  }

  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException("Пользователь не найден");
    }
    Optional<User> user = userRepository.findById(id);
    user.ifPresent(value -> passwordResetService.deleteByUserId(value));
    System.out.println(id);
    userRepository.deleteById(id);
  }

  public void updateUserStatus(Long id, boolean isActive) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    user.setActive(isActive);
    userRepository.save(user);
  }

  @Transactional
  public void updateClientDetails(Long id, ClientUpdateRequest request) {
    Client user = (Client) userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    user.setFirstName(request.getFirstName());
    user.setSecondName(request.getSecondName());
    user.setPatronymicName(request.getPatronymicName());
    user.setAddress(request.getAddress());
    user.setMobilePhone(request.getMobilePhone());
    user.setIncome(request.getIncome());
    user.setRole(Role.CLIENT);
    userRepository.save(user);
  }

  @Transactional
  public void updateAdminDetails(Long id, AdminUpdateRequest request) {
    Admin user = (Admin) userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    user.setFirstName(request.getFirstName());
    user.setSecondName(request.getSecondName());
    user.setPatronymicName(request.getPatronymicName());
    user.setMobilePhone(request.getMobilePhone());
    user.setRole(Role.ADMIN);
    userRepository.save(user);
  }

  @Transactional
  public void changeRoleToClient(Long id, ClientUpdateRequest request) {
    User oldUser = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    userRepository.delete(oldUser);
    passwordResetService.deleteByUserId(oldUser);
    userRepository.flush();
    Client user = new Client();
    user.setId(oldUser.getId());
    user.setLogin(oldUser.getLogin());
    user.setEmail(oldUser.getEmail());
    user.setPassword(oldUser.getPassword());
    user.setFirstName(request.getFirstName());
    user.setSecondName(request.getSecondName());
    user.setPatronymicName(request.getPatronymicName());
    user.setAddress(request.getAddress());
    user.setMobilePhone(request.getMobilePhone());
    user.setIncome(request.getIncome());
    user.setRole(Role.CLIENT);
    userRepository.save(user);
  }


  @Transactional
  public void changeRoleToAdmin(Long id, AdminUpdateRequest request) {
    User oldUser = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    userRepository.delete(oldUser);
    passwordResetService.deleteByUserId(oldUser);
    userRepository.flush();
    Admin user = new Admin();
    user.setId(oldUser.getId());
    user.setLogin(oldUser.getLogin());
    user.setEmail(oldUser.getEmail());
    user.setPassword(oldUser.getPassword());
    user.setFirstName(request.getFirstName());
    user.setSecondName(request.getSecondName());
    user.setPatronymicName(request.getPatronymicName());
    user.setMobilePhone(request.getMobilePhone());
    user.setRole(Role.ADMIN);
    userRepository.save(user);
  }


}
