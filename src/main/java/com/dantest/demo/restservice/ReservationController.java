package com.dantest.demo.restservice;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private ShowerService showerService;

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @PostMapping("/reserve")
  public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
    // Validate duration
    if (request.getDurationInMinutes() != 15 && request.getDurationInMinutes() != 30) {
      return ResponseEntity.badRequest().body("Duration must be 15 or 30 minutes.");
    }

    // Find an available shower
    Optional<Shower> availableShower = showerService.getAllShowers().stream()
        .filter(Shower::isOnline)
        .filter(shower -> !showerService.isShowerOccupied(shower.getId()))
        .findFirst();

    if (!availableShower.isPresent()) {
      return ResponseEntity.status(503).body("No showers available at the moment. Please try again later.");
    }

    Shower shower = availableShower.get();

    // Create reservation
    Reservation reservation = reservationService.createReservation(
        request.getEmail(),
        request.getPhoneNumber(),
        shower.getId(),
        request.getDurationInMinutes());

    // Publish and broadcast shower status update event
    eventPublisher.publishEvent(new ShowerStatusUpdateEvent(this, showerService.getAllShowers()));

    // Broadcast updated shower status
    showerService.broadcastShowerStatus();

    return ResponseEntity.ok(new ReservationResponse(reservation.getPinCode(), shower.getId()));
  }

  @Data
  private static class ReservationRequest {
    private String email;
    private String phoneNumber;
    private int durationInMinutes;
  }

  @Data
  private static class ReservationResponse {
    private int pinCode;
    private int showerId;

    public ReservationResponse(int pinCode, int showerId) {
      this.pinCode = pinCode;
      this.showerId = showerId;
    }
  }
}
