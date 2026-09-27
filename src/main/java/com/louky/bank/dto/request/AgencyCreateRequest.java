package com.louky.bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgencyCreateRequest {

    @NotBlank
    @Size(max = 20)
    private String code;

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Size(max = 80)
    private String city;

    @Size(max = 255)
    private String address;

    @Size(max = 30)
    private String phone;
}