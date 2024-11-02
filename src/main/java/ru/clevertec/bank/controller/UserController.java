package ru.clevertec.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.bank.domain.ClientDto;
import ru.clevertec.bank.service.ClientService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private ClientService clientService;

    @Autowired
    public UserController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> findById(@PathVariable("id") Long id) {
        ClientDto clientDTO = clientService.getClientById(id);
        if (clientDTO != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(clientDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<Void> uploadAvatar(@PathVariable Long id, @RequestBody String base64Image) {
        clientService.uploadAvatar(id, base64Image);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<String> getAvatar(@PathVariable Long id) {
        String avatar = clientService.getAvatar(id);
        return ResponseEntity.ok(avatar);
    }
}

