package service;

import dto.request.PersonRequestDTO;
import dto.response.PersonResponseDTO;

import java.util.List;

public interface PersonService {
    void save(PersonRequestDTO person);
    List<PersonResponseDTO> findAll();
}
