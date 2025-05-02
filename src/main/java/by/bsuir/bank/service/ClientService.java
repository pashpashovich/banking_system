package by.bsuir.bank.service;

import by.bsuir.bank.domain.ClientDto;
import by.bsuir.bank.entity.Client;
import by.bsuir.bank.mapper.ClientMapper;
import by.bsuir.bank.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;

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
}
