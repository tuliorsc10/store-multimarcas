package com.tulio.store_multimarcas.client.dto;

import com.seuprojeto.common.reply.dto.CompanyResponseDTO;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Jacksonized
public record RetrieveClientDTO(
        String name,
        String email,
        String phone,
        AddressClientDTO addressClientDTO,
        CompanyResponseDTO companyResponse
) implements Serializable {
}
