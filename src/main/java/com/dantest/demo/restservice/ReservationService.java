package com.dantest.demo.restservice;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ReservationService {

    private List<Reservation> reservations = new ArrayList<>();
    private final Random random = new Random();

    public Reservation createReservation(String email, String phoneNumber, int showerId, int durationInMinutes) {
        int pinCode = generatePinCode();
        Reservation reservation = new Reservation(email, phoneNumber, LocalDateTime.now(), pinCode, showerId, durationInMinutes);
        reservations.add(reservation);
        return reservation;
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }

  @Scheduled(fixedRate = 60000) // Runs every 1 minute
public void removeExpiredReservations() {
    LocalDateTime now = LocalDateTime.now();
    reservations.removeIf(reservation ->
            reservation.getReservationTime().plusMinutes(reservation.getDurationInMinutes()).isBefore(now));
}

    private int generatePinCode() {
        return 100000 + random.nextInt(900000); // Generate 6-digit PIN
    }
}
