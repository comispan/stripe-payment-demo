package com.example.payment_demo.service;

import com.example.payment_demo.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ReservationCleanupService {

    private final ReservationRepository reservationRepository;

    public ReservationCleanupService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Runs every 60 seconds (60000 milliseconds)
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredStock() {
        int updatedCount = reservationRepository.deactivateExpiredReservations(LocalDateTime.now());

        if (updatedCount > 0) {
            log.info("Cleaned up {} expired reservations.", updatedCount);
        }
    }
}