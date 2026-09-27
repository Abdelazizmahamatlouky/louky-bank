package com.louky.bank.repository;

import com.louky.bank.domain.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {

    Optional<Agency> findByCode(String code);

    boolean existsByCode(String code);
}