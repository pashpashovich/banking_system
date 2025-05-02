package by.bsuir.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import by.bsuir.bank.domain.ClientDto;
import by.bsuir.bank.entity.Client;
import by.bsuir.bank.mapper.ClientMapper;
import by.bsuir.bank.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ClientServiceTest {

  @Mock
  private ClientRepository clientRepository;

  @Mock
  private ClientMapper clientMapper;

  @InjectMocks
  private ClientService clientService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldSaveClient() {
    // Given
    Client client = new Client();

    // When
    clientService.save(client);

    // Then
    verify(clientRepository).save(client);
  }

  @Test
  void shouldReturnClientDtoWhenClientExists() {
    // Given
    Client client = new Client();
    client.setId(1L);
    ClientDto expectedDto = new ClientDto().setId(1L);

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(clientMapper.toDomain(client)).thenReturn(expectedDto);

    // When
    ClientDto result = clientService.getClientById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(clientRepository).findById(1L);
    verify(clientMapper).toDomain(client);
  }

  @Test
  void shouldReturnNullWhenClientDoesNotExist() {
    // Given
    when(clientRepository.findById(999L)).thenReturn(Optional.empty());

    // When
    ClientDto result = clientService.getClientById(999L);

    // Then
    assertNull(result);
    verify(clientRepository).findById(999L);
    verifyNoInteractions(clientMapper);
  }

  @Test
  void shouldReturnAllClients() {
    // Given
    List<Client> clients = List.of(new Client());
    List<ClientDto> expectedDtos = List.of(new ClientDto());

    when(clientRepository.findAll()).thenReturn(clients);
    when(clientMapper.toDomains(clients)).thenReturn(expectedDtos);

    // When
    List<ClientDto> result = clientService.getAllClients();

    // Then
    assertEquals(1, result.size());
    verify(clientRepository).findAll();
    verify(clientMapper).toDomains(clients);
  }

  @Test
  void shouldUpdateClientIfExists() {
    // Given
    Client existingClient = new Client();
    existingClient.setId(1L);

    ClientDto updatedDto = new ClientDto()
        .setFirstName("Ivan")
        .setSecondName("Ivanov")
        .setPatronymicName("Ivanovich")
        .setIncome(1234.56)
        .setAddress("Minsk")
        .setMobilePhone("123456789");

    when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));

    // When
    clientService.updateClient(1L, updatedDto);

    // Then
    assertEquals("Ivan", existingClient.getFirstName());
    assertEquals("Minsk", existingClient.getAddress());
    verify(clientRepository).save(existingClient);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistingClient() {
    // Given
    ClientDto updatedDto = new ClientDto();
    when(clientRepository.findById(999L)).thenReturn(Optional.empty());

    // When / Then
    assertThrows(EntityNotFoundException.class, () -> clientService.updateClient(999L, updatedDto));
    verify(clientRepository).findById(999L);
  }

  @Test
  void shouldDeleteClientById() {
    // Given
    Long id = 1L;

    // When
    clientService.deleteClient(id);

    // Then
    verify(clientRepository).deleteById(id);
  }
}
