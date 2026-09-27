package com.louky.bank.repository;

import com.louky.bank.domain.entity.CreditRequest;
import com.louky.bank.domain.enums.CreditStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public interface CreditRequestRepository extends JpaRepository<CreditRequest, Long> {

    Optional<CreditRequest> findByReference(String reference);

    boolean existsByReference(String reference);

    /**
     * Recherche paginee avec filtres optionnels.
     *
     * Note : les CAST explicites sont necessaires pour PostgreSQL
     * qui ne peut pas inferer le type des parametres utilises
     * dans un test "(:param IS NULL OR ...)". Sans CAST, PostgreSQL
     * renvoie l'erreur SQLState 42P18 :
     * "could not determine data type of parameter $N".
     */
    @Query("""
            SELECT c FROM CreditRequest c
            WHERE (CAST(:status AS string) IS NULL OR c.status = :status)
              AND (CAST(:agencyId AS long) IS NULL OR c.agency.id = :agencyId)
              AND (CAST(:minAmount AS bigdecimal) IS NULL OR c.amount >= :minAmount)
              AND (CAST(:maxAmount AS bigdecimal) IS NULL OR c.amount <= :maxAmount)
              AND (CAST(:from AS timestamp) IS NULL OR c.createdAt >= :from)
              AND (CAST(:to AS timestamp) IS NULL OR c.createdAt <= :to)
            """)
    Page<CreditRequest> search(@Param("status") CreditStatus status,
                               @Param("agencyId") Long agencyId,
                               @Param("minAmount") BigDecimal minAmount,
                               @Param("maxAmount") BigDecimal maxAmount,
                               @Param("from") Instant from,
                               @Param("to") Instant to,
                               Pageable pageable);

    long countByAgencyId(Long agencyId);
}