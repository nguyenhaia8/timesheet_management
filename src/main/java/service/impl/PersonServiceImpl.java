package service.impl;

import dto.request.PersonRequestDTO;
import dto.response.PersonResponseDTO;
import service.PersonService;

import java.util.List;

public class PersonServiceImpl implements PersonService {
    @Override
    public void save(PersonRequestDTO person) {

    }

    @Override
    public List<PersonResponseDTO> findAll() {
        return List.of();
    }
}
