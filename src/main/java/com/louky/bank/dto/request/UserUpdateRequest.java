package com.louky.bank.dto.request;

import com.louky.bank.domain.enums.Role;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @Size(max = 80)
    private String firstName;

    @Size(max = 80)
    private String lastName;

    private Role role;

    private Boolean active;

    private Long agencyId;
}