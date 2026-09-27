package com.louky.bank.dto.request;

import com.louky.bank.domain.enums.CreditType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreditRequestUpdateRequest {

    @Size(max = 80)
    private String applicantFirstName;

    @Size(max = 80)
    private String applicantLastName;

    @Size(max = 150)
    private String applicantEmail;

    @Size(max = 30)
    private String applicantPhone;

    @DecimalMin(value = "10000.00")
    private BigDecimal amount;

    @Min(1)
    private Integer durationMonths;

    @DecimalMin("0.00")
    private BigDecimal monthlyIncome;

    @DecimalMin("0.00")
    private BigDecimal monthlyCharges;

    private CreditType creditType;

    @Size(max = 500)
    private String purpose;
}