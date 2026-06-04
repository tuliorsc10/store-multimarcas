package com.tulio.store_multimarcas.client.controller;

import com.tulio.store_multimarcas.client.dto.*;
import com.tulio.store_multimarcas.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/client")
public class ClientController {

    private final ClientService clientService;

    @PostMapping()
    public ResponseEntity<CreateClientReponseDTO> createClient(@RequestBody @Valid CreateClientRequestDTO request) {
        return ResponseEntity.ok(clientService.createClient(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetrieveClientDTO> retrieveClientById(@PathVariable String id) {
        return ResponseEntity.ok(clientService.retrieveClientById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientById(@PathVariable Long id) {
        clientService.deleteClientById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateClientResponseDTO> updateClient(@PathVariable Long id, @RequestBody @Valid UpdateClientResquestDTO request) {
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }
}

