package com.louky.bank.dto.request;

import com.louky.bank.domain.enums.CreditStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreditRequestFilter {

    private CreditStatus status;

    private Long agencyId;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;
}