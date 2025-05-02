package by.bsuir.bank.controller;

import by.bsuir.bank.domain.AdminUpdateRequest;
import by.bsuir.bank.domain.ClientUpdateRequest;
import by.bsuir.bank.domain.UserDto;
import by.bsuir.bank.service.UserService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserManagementController {

  private final UserService userService;

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/status")
  public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> statusRequest) {
    boolean isActive = "unblock".equals(statusRequest.get("action"));
    userService.updateUserStatus(id, isActive);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/client/{id}")
  public ResponseEntity<Void> updateUserDetailsClient(@PathVariable Long id, @RequestBody ClientUpdateRequest request) {
    userService.updateClientDetails(id, request);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/adm/{id}")
  public ResponseEntity<Void> updateUserDetailsAdmin(@PathVariable Long id, @RequestBody AdminUpdateRequest request) {
    userService.updateAdminDetails(id, request);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/toAdmin/{id}")
  public ResponseEntity<Void> changeRoleToAdmin(@PathVariable Long id, @RequestBody AdminUpdateRequest request) {
    userService.changeRoleToAdmin(id, request);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/toClient/{id}")
  public ResponseEntity<Void> changeRoleToClient(@PathVariable Long id, @RequestBody ClientUpdateRequest request) {
    userService.changeRoleToClient(id, request);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> getAllUsersForDir() {
    List<UserDto> users = userService.getAllUsersForDir();
    return ResponseEntity.ok(users);
  }

}

