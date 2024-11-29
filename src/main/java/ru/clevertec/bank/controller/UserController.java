package ru.clevertec.bank.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.bank.domain.AdminDTO;
import ru.clevertec.bank.domain.ClientDto;
import ru.clevertec.bank.domain.DirectorDto;
import ru.clevertec.bank.domain.RoleUpdateRequest;
import ru.clevertec.bank.domain.UserResponse;
import ru.clevertec.bank.entity.Director;
import ru.clevertec.bank.service.AdminService;
import ru.clevertec.bank.service.ClientService;
import ru.clevertec.bank.service.DirectorService;
import ru.clevertec.bank.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    private ClientService clientService;
    private AdminService adminService;
    private DirectorService directorService;


    public UserController(UserService userService, ClientService clientService, AdminService adminService, DirectorService directorService) {
        this.userService = userService;
        this.clientService = clientService;
        this.adminService = adminService;
        this.directorService=directorService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<ClientDto> findById(@PathVariable("id") Long id) {
        ClientDto clientDTO = clientService.getClientById(id);
        if (clientDTO != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(clientDTO);
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("/client/{id}")
    public ResponseEntity<Void> updateClient(@PathVariable("id") Long id, @RequestBody ClientDto clientDto) {
        clientService.updateClient(id, clientDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/client/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable("id") Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email-check")
    public ResponseEntity<Boolean> findById(@RequestParam("email") String email) {
        boolean emailAvailable = userService.isEmailAvailable(email);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(emailAvailable);
    }

    @GetMapping("/client")
    public ResponseEntity<List<ClientDto>> findClients() {
        List<ClientDto> clientDTOs = clientService.getAllClients();
        if (clientDTOs != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(clientDTOs);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<AdminDTO> findAdminById(@PathVariable("id") Long id) {
        AdminDTO adminDTO = adminService.getAdminById(id);
        if (adminDTO != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(adminDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/director/{id}")
    public ResponseEntity<DirectorDto> findDirectorById(@PathVariable("id") Long id) {
        DirectorDto directorDto = directorService.getDirectorById(id);
        if (directorDto != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(directorDto);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<Void> uploadAvatar(@PathVariable Long id, @RequestBody String base64Image) {
        userService.uploadAvatar(id, base64Image);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<String> getAvatar(@PathVariable Long id) {
        String avatar = userService.getAvatar(id);
        return ResponseEntity.ok(avatar);
    }
}

