package ru.clevertec.bank.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.bank.domain.ClientDto;
import ru.clevertec.bank.entity.Client;
import ru.clevertec.bank.mapper.ClientMapper;
import ru.clevertec.bank.repository.ClientRepository;

import java.util.List;
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


    public List<ClientDto> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return clientMapper.toDomains(clients);
    }

    public void updateClient(Long id, ClientDto clientDto) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Клиент не найден"));
        existingClient.setFirstName(clientDto.getFirstName());
        existingClient.setSecondName(clientDto.getSecondName());
        existingClient.setPatronymicName(clientDto.getPatronymicName());
        existingClient.setIncome(clientDto.getIncome());
        existingClient.setMobilePhone(clientDto.getMobilePhone());
        existingClient.setAddress(clientDto.getAddress());
        clientRepository.save(existingClient);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public ClientDto findById(Long id) {
        boolean present = clientRepository.findById(id).isPresent();
        if (present) return clientMapper.toDomain(clientRepository.findById(id).get());
        else throw new IllegalArgumentException("Нет клиента с таким ID");
    }
}
