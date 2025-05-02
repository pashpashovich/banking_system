package by.bsuir.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import by.bsuir.bank.domain.DirectorDto;
import by.bsuir.bank.entity.Director;
import by.bsuir.bank.entity.enumeration.Role;
import by.bsuir.bank.mapper.DirectorMapper;
import by.bsuir.bank.repository.DirectorRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DirectorServiceTest {

  @Mock
  private DirectorRepository directorRepository;

  @Mock
  private DirectorMapper directorMapper;

  @InjectMocks
  private DirectorService directorService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldReturnDirectorDtoWhenDirectorExists() {
    // Given
    Director director = new Director();
    director.setEmail("director@example.com");
    director.setFirstName("Anna");
    director.setSecondName("Ivanova");
    director.setPatronymicName("Petrovna");
    director.setMobilePhone("123456789");
    director.setRole(Role.DIRECTOR);

    DirectorDto expectedDto = new DirectorDto()
        .setEmail("director@example.com")
        .setFirstName("Anna")
        .setSecondName("Ivanova")
        .setPatronymicName("Petrovna")
        .setMobilePhone("123456789")
        .setRole(Role.DIRECTOR);

    when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
    when(directorMapper.toDomain(director)).thenReturn(expectedDto);

    // When
    DirectorDto result = directorService.getDirectorById(1L);

    // Then
    assertNotNull(result);
    assertEquals("director@example.com", result.getEmail());
    assertEquals(Role.DIRECTOR, result.getRole());
    verify(directorRepository).findById(1L);
    verify(directorMapper).toDomain(director);
  }

  @Test
  void shouldReturnNullWhenDirectorNotFound() {
    // Given
    when(directorRepository.findById(999L)).thenReturn(Optional.empty());

    // When
    DirectorDto result = directorService.getDirectorById(999L);

    // Then
    assertNull(result);
    verify(directorRepository).findById(999L);
    verifyNoInteractions(directorMapper);
  }
}
