package com.louky.bank.mapper;

import com.louky.bank.domain.entity.CreditRequest;
import com.louky.bank.dto.response.CreditRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CreditAssessmentMapper.class})
public interface CreditRequestMapper {

    @Mapping(target = "agencyId", source = "agency.id")
    @Mapping(target = "agencyName", source = "agency.name")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", expression = "java(request.getCreatedBy().getFirstName() + \" \" + request.getCreatedBy().getLastName())")
    CreditRequestResponse toResponse(CreditRequest request);
}