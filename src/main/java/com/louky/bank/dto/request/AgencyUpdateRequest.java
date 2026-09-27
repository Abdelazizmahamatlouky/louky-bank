package com.louky.bank.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgencyUpdateRequest {

    @Size(max = 120)
    private String name;

    @Size(max = 80)
    private String city;

    @Size(max = 255)
    private String address;

    @Size(max = 30)
    private String phone;

    private Boolean active;
}