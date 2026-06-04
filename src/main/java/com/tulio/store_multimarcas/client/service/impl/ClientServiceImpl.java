package com.tulio.store_multimarcas.client.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuprojeto.common.consumer.TopicEnum;
import com.seuprojeto.common.consumer.dto.MessageWrapper;
import com.seuprojeto.common.reply.dto.CompanyResponseDTO;
import com.tulio.store_multimarcas.client.dto.*;
import com.tulio.store_multimarcas.client.mapper.ClientMapper;
import com.tulio.store_multimarcas.client.repository.ClientRepository;
import com.tulio.store_multimarcas.client.service.ClientService;
import com.tulio.store_multimarcas.configuration.MessageProducer;
import com.tulio.store_multimarcas.configuration.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final MessageProducer messageProducer;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;


    @Override
    public CreateClientReponseDTO createClient(CreateClientRequestDTO request) {
        log.info("Received request: {}", request);
        Optional<ClientEntity> client = clientRepository.findByEmail(request.getEmail());
        if (client.isPresent()) {
            throw new RuntimeException(String.format("A client with this email address %s has already been registered", client.get().getEmail()));
        }
        validateDocument(request);
        ClientEntity clientEntity = clientMapper.toClientEntity(request);
        log.info("Clienty Entity: {}", clientEntity);
        ClientEntity saveClient = clientRepository.save(clientEntity);
        log.info("Clienty entity saved: {}", saveClient);
        return clientMapper.toCreateClientResponse(saveClient);
    }

    private void validateDocument(CreateClientRequestDTO request) {
        MessageWrapper<Object> wrapper = MessageWrapper.builder().payload(request.getDocument()).build();
        messageProducer.send("VALIDATE_CNPJ_RECEITA_FEDERAL", wrapper);
    }

    @Override
    public RetrieveClientDTO retrieveClientById(String id) {
        Optional<ClientEntity> clientFound = clientRepository.findByClientId(UUID.fromString(id));
        ClientEntity clientEntity = clientFound.orElseThrow(() -> new RuntimeException("Client with Id " + id + " not found!"));
        ConsumerRecord<String, Object> response = kafkaProducer.sendAndReply(TopicEnum.GET_VALIDATE_CNPJ.name(), clientFound.get().getDocumentCpf());
        try {
            CompanyResponseDTO message =
                    objectMapper.readValue(
                            response.value().toString(),
                            CompanyResponseDTO.class
                    );
            return clientMapper.toRetrieveClient(clientEntity, message);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar");
        }
    }

    @Override
    public void deleteClientById(Long id) {
        Optional<ClientEntity> clientFound = clientRepository.findById(id);
        if (clientFound.isPresent()) {
            clientRepository.deleteById(id);
        } else {
            throw new RuntimeException("Client with Id " + id + " not found!");
        }
    }

    @Override
    public UpdateClientResponseDTO updateClient(Long id, UpdateClientResquestDTO request) {
        Optional<ClientEntity> clientFound = clientRepository.findById(id);
        if (clientFound.isPresent() && clientFound.get().getDocumentCpf().equalsIgnoreCase(request.getDocumentCpf())) {
            ClientEntity clientEntity = updateValidation(clientFound.get(), request);
            ClientEntity client = clientRepository.save(clientEntity);
            return clientMapper.toUpdateClientResponse(client);
        }
        return null;
    }

    private ClientEntity updateValidation(ClientEntity clientEntity, UpdateClientResquestDTO resquest) {
        if (resquest != null) {
            clientEntity.setName(StringUtils.isBlank(resquest.getName()) ? clientEntity.getName() : resquest.getName());
            clientEntity.setEmail(StringUtils.isBlank(resquest.getEmail()) ? clientEntity.getEmail() : resquest.getEmail());
            clientEntity.setPhone(StringUtils.isBlank(resquest.getPhone()) ? clientEntity.getPhone() : resquest.getPhone());
            verifyAddress(clientEntity, resquest);
        }
        return clientEntity;
    }

    private void verifyAddress(ClientEntity clientEntity, UpdateClientResquestDTO resquest) {
        if (resquest.getAddress() != null) {
            clientEntity.setState(StringUtils.isBlank(resquest.getAddress().street()) ? clientEntity.getStreet() : resquest.getAddress().street());
            clientEntity.setState(StringUtils.isBlank(resquest.getAddress().number()) ? clientEntity.getNumber() : resquest.getAddress().number());
            clientEntity.setState(StringUtils.isBlank(resquest.getAddress().district()) ? clientEntity.getDistrict() : resquest.getAddress().district());
            clientEntity.setState(StringUtils.isBlank(resquest.getAddress().city()) ? clientEntity.getCity() : resquest.getAddress().city());
            clientEntity.setState(StringUtils.isBlank(resquest.getAddress().state()) ? clientEntity.getState() : resquest.getAddress().state());
            clientEntity.setState(StringUtils.isBlank(resquest.getAddress().country()) ? clientEntity.getCountry() : resquest.getAddress().country());
            clientEntity.setState(StringUtils.isBlank(resquest.getAddress().complement()) ? clientEntity.getComplement() : resquest.getAddress().complement());
        }
    }
}
