package com.louky.bank.mapper;

import com.louky.bank.domain.entity.Agency;
import com.louky.bank.dto.response.AgencyResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgencyMapper {

    AgencyResponse toResponse(Agency agency);
}