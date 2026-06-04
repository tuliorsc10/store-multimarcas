package com.tulio.store_multimarcas.client.service;

import com.tulio.store_multimarcas.client.dto.*;
import jakarta.validation.Valid;

public interface ClientService {

    CreateClientReponseDTO createClient(CreateClientRequestDTO request);

    RetrieveClientDTO retrieveClientById(String id);

    void deleteClientById(Long id);

    UpdateClientResponseDTO updateClient(Long id, @Valid UpdateClientResquestDTO request);
}
