package by.bsuir.bank.service;

import by.bsuir.bank.domain.DirectorDto;
import by.bsuir.bank.entity.Director;
import by.bsuir.bank.mapper.DirectorMapper;
import by.bsuir.bank.repository.DirectorRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DirectorService {

  private final DirectorRepository directorRepository;
  private final DirectorMapper directorMapper;

  public DirectorDto getDirectorById(Long id) {
    Optional<Director> director = directorRepository.findById(id);
    return director.map(directorMapper::toDomain).orElse(null);
  }
}
