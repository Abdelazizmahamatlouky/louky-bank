package com.louky.bank.mapper;

import com.louky.bank.domain.entity.User;
import com.louky.bank.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "agencyId", source = "agency.id")
    @Mapping(target = "agencyName", source = "agency.name")
    UserResponse toResponse(User user);
}