package org.example.service.Client;

import org.example.dto.request.ClientRequestDTO;
import org.example.dto.response.ClientResponseDTO;

import java.util.List;

public interface ClientService {
    List<ClientResponseDTO> findAll();
    ClientResponseDTO findById(Integer id);
    ClientResponseDTO save(ClientRequestDTO clientRequestDTO);
    ClientResponseDTO update(Integer id, ClientRequestDTO clientRequestDTO);
    void deleteById(Integer id);
}
