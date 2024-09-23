package com.dantest.demo.restservice;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShowerService {
  private List<Shower> showers = new ArrayList<>();
  private static final int SHOWER_COUNT = 10;

  private final ReservationService reservationService;
  private final ApplicationEventPublisher eventPublisher;
  private final ShowerStatusWebSocketHandler webSocketHandler;

  public ShowerService(ReservationService reservationService,
      ApplicationEventPublisher eventPublisher,
      @Lazy ShowerStatusWebSocketHandler webSocketHandler) {
    this.reservationService = reservationService;
    this.eventPublisher = eventPublisher;
    this.webSocketHandler = webSocketHandler;
  }

  @PostConstruct
  public void initShowers() {
    for (int i = 1; i <= SHOWER_COUNT; i++) {
      showers.add(new Shower(i, true, false, false, 0)); // Initialize with remainingTime as 0
    }
  }

  @Scheduled(fixedRate = 60000) // Run every minute
  public void updateShowerTimes() {
    for (Shower shower : showers) {
      if (shower.isOccupied() && shower.getRemainingTime() > 0) {
        shower.setRemainingTime(shower.getRemainingTime() - 1);
        if (shower.getRemainingTime() == 0) {
          shower.setOccupied(false);
        }
      }
    }
    broadcastShowerStatus();
  }

  public List<Shower> getAllShowers() {
    updateShowerOccupancyStatus();
    return showers;
  }

  public Shower getShowerById(int id) {
    updateShowerOccupancyStatus();
    return showers.stream()
        .filter(shower -> shower.getId() == id)
        .findFirst()
        .orElse(null);
  }

  public void setShowerStatus(int id, boolean isOnline, boolean isUnderMaintenance) {
    Shower shower = getShowerById(id);
    if (shower != null) {
      shower.setOnline(isOnline);
      shower.setUnderMaintenance(isUnderMaintenance);
      broadcastShowerStatus(); // Broadcast status after the update
    }
  }

  public boolean isShowerOccupied(int showerId) {
    return reservationService.getAllReservations().stream()
        .anyMatch(reservation -> reservation.getShowerId() == showerId
            && reservation.getReservationTime().isBefore(LocalDateTime.now())
            && reservation.getReservationTime().plusMinutes(reservation.getDurationInMinutes())
                .isAfter(LocalDateTime.now()));
  }

  private void updateShowerOccupancyStatus() {
    for (Shower shower : showers) {
      boolean isOccupied = isShowerOccupied(shower.getId());
      shower.setOccupied(isOccupied);
      if (isOccupied) {
        long remainingTime = reservationService.getAllReservations().stream()
            .filter(reservation -> reservation.getShowerId() == shower.getId())
            .mapToLong(reservation -> {
              LocalDateTime endTime = reservation.getReservationTime().plusMinutes(reservation.getDurationInMinutes());
              return java.time.Duration.between(LocalDateTime.now(), endTime).toMinutes();
            })
            .findFirst()
            .orElse(0L);
        shower.setRemainingTime(remainingTime);
      } else {
        shower.setRemainingTime(0);
      }
    }
  }

  public void updateShowerStatus(int id, boolean isOnline, boolean isUnderMaintenance) throws IOException {
    setShowerStatus(id, isOnline, isUnderMaintenance);
    eventPublisher.publishEvent(new ShowerStatusUpdateEvent(this, showers));
    broadcastShowerStatus();
  }

  public void broadcastShowerStatus() {
    try {
      webSocketHandler.broadcastShowersStatus(showers);
    } catch (IOException e) {
      // Handle the exception
    }
  }
}
