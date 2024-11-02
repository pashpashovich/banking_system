package ru.clevertec.bank.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.bank.domain.ClientDto;
import ru.clevertec.bank.entity.Client;
import ru.clevertec.bank.mapper.ClientMapper;
import ru.clevertec.bank.repository.ClientRepository;

import java.util.Optional;

@Service
public class ClientService {

    private ClientRepository clientRepository;
    private ClientMapper clientMapper;

    @Autowired
    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public void save(Client client) {
        clientRepository.save(client);
    }

    @Transactional
    public ClientDto getClientById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        return client.map(value -> clientMapper.toDomain(value)).orElse(null);
    }

    public void uploadAvatar(Long clientId, String base64Image) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        client.setAvatar(base64Image);
        clientRepository.save(client);
    }

    public String getAvatar(Long clientId) {
        return clientRepository.findById(clientId)
                .map(Client::getAvatar)
                .orElse(null);
    }

}
