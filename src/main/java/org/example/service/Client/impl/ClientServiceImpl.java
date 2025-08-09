package org.example.service.Client.impl;

import org.example.dto.request.ClientRequestDTO;
import org.example.dto.response.ClientResponseDTO;
import org.example.model.Client;
import org.example.repository.ClientRepository;
import org.example.service.Client.ClientService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<ClientResponseDTO> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(this::toClientResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponseDTO findById(Integer id) {
        return clientRepository.findById(id)
                .map(this::toClientResponseDTO)
                .orElse(null);
    }

    @Override
    public ClientResponseDTO save(ClientRequestDTO clientRequestDTO) {
        Client client = new Client();
        client.setClientName(clientRequestDTO.clientName());
        client.setContactEmail(clientRequestDTO.contactEmail());
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());

        Client savedClient = clientRepository.save(client);
        return toClientResponseDTO(savedClient);
    }

    @Override
    public ClientResponseDTO update(Integer id, ClientRequestDTO clientRequestDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));

        existingClient.setClientName(clientRequestDTO.clientName());
        existingClient.setContactEmail(clientRequestDTO.contactEmail());
        existingClient.setUpdatedAt(LocalDateTime.now());

        Client updatedClient = clientRepository.save(existingClient);
        return toClientResponseDTO(updatedClient);
    }

    @Override
    public void deleteById(Integer id) {
        clientRepository.deleteById(id);
    }

    private ClientResponseDTO toClientResponseDTO(Client client) {
        return new ClientResponseDTO(
                client.getClientId(),
                client.getClientName(),
                client.getContactEmail(),
                client.getContactPhone(),
                client.getAddress()
        );
    }
}
