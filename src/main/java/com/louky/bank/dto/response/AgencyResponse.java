package com.louky.bank.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class AgencyResponse {

    private final Long id;
    private final String code;
    private final String name;
    private final String city;
    private final String address;
    private final String phone;
    private final boolean active;
    private final Instant createdAt;
}