package com.louky.bank.domain.entity;

import com.louky.bank.domain.enums.DecisionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_decisions")
public class CreditDecision extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_request_id", nullable = false)
    private CreditRequest creditRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_maker_id", nullable = false)
    private User decisionMaker;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 20)
    private DecisionType decision;

    @Column(name = "comment", length = 1000)
    private String comment;
}