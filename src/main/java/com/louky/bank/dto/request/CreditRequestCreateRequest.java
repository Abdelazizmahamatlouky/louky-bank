package com.louky.bank.dto.request;

import com.louky.bank.domain.enums.CreditType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreditRequestCreateRequest {

    @NotBlank
    @Size(max = 80)
    private String applicantFirstName;

    @NotBlank
    @Size(max = 80)
    private String applicantLastName;

    @Size(max = 150)
    private String applicantEmail;

    @Size(max = 30)
    private String applicantPhone;

    @NotNull
    @DecimalMin(value = "10000.00", message = "Le montant minimum est de 10 000 FCFA")
    private BigDecimal amount;

    @NotNull
    @Min(value = 1, message = "La duree minimale est de 1 mois")
    private Integer durationMonths;

    @NotNull
    @DecimalMin(value = "0.00", message = "Le revenu ne peut pas etre negatif")
    private BigDecimal monthlyIncome;

    @NotNull
    @DecimalMin(value = "0.00", message = "Les charges ne peuvent pas etre negatives")
    private BigDecimal monthlyCharges;

    @NotNull
    private CreditType creditType;

    @NotBlank
    @Size(max = 500)
    private String purpose;
}