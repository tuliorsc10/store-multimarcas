package com.tulio.store_multimarcas.client.mapper;

import com.seuprojeto.common.reply.dto.CompanyResponseDTO;
import com.tulio.store_multimarcas.client.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(target = "documentCpf", source = "request.document")
    @Mapping(target = "street", source = "request.address.street")
    @Mapping(target = "number", source = "request.address.number")
    @Mapping(target = "district", source = "request.address.district")
    @Mapping(target = "city", source = "request.address.city")
    @Mapping(target = "country", source = "request.address.country")
    @Mapping(target = "state", source = "request.address.state")
    @Mapping(target = "complement", source = "request.address.complement")
    ClientEntity toClientEntity(CreateClientRequestDTO request);

    @Mapping(source = "street", target = "address.street")
    @Mapping(source = "number", target = "address.number")
    @Mapping(source = "district", target = "address.district")
    @Mapping(source = "city", target = "address.city")
    @Mapping(source = "country", target = "address.country")
    @Mapping(source = "state", target = "address.state")
    @Mapping(source = "complement", target = "address.complement")
    CreateClientReponseDTO toCreateClientResponse(ClientEntity entity);

    @Mapping(target = "email", source = "entity.email")
    RetrieveClientDTO toRetrieveClient(ClientEntity entity, CompanyResponseDTO companyResponse);

    UpdateClientResponseDTO toUpdateClientResponse(ClientEntity entity);
}
