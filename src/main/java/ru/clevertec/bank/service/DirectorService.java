package ru.clevertec.bank.service;

import org.springframework.stereotype.Service;
import ru.clevertec.bank.domain.DirectorDto;
import ru.clevertec.bank.entity.Director;
import ru.clevertec.bank.mapper.DirectorMapper;
import ru.clevertec.bank.repository.DirectorRepository;

import java.util.Optional;

@Service
public class DirectorService {
    private final DirectorRepository directorRepository;
    private final DirectorMapper directorMapper;

    public DirectorService(DirectorRepository directorRepository, DirectorMapper directorMapper) {
        this.directorRepository = directorRepository;
        this.directorMapper = directorMapper;
    }

    public DirectorDto getDirectorById(Long id) {
        Optional<Director> director = directorRepository.findById(id);
        return director.map(directorMapper::toDomain).orElse(null);
    }
}
