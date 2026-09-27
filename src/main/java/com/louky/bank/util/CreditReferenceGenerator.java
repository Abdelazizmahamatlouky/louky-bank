package com.louky.bank.util;

import com.louky.bank.repository.CreditRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class CreditReferenceGenerator {

    private final CreditRequestRepository creditRequestRepository;
    private final AtomicLong counter = new AtomicLong(0);

    public synchronized String generate() {
        int year = Year.now().getValue();
        long sequence = nextSequence();
        String reference = String.format("LB-%d-%06d", year, sequence);
        while (creditRequestRepository.existsByReference(reference)) {
            sequence = nextSequence();
            reference = String.format("LB-%d-%06d", year, sequence);
        }
        return reference;
    }

    private long nextSequence() {
        return counter.incrementAndGet();
    }
}