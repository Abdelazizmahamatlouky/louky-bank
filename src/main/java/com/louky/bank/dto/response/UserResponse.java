package com.louky.bank.dto.response;

import com.louky.bank.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class UserResponse {

    private final Long id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Role role;
    private final boolean active;
    private final Long agencyId;
    private final String agencyName;
    private final Instant createdAt;
}