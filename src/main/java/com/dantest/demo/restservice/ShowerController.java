package com.dantest.demo.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Data;

import java.util.List;

@RestController
@RequestMapping("/api/showers")
public class ShowerController {

  @Autowired
  private ShowerService showerService;

  @GetMapping
  public ResponseEntity<List<Shower>> getAllShowers() {
    return ResponseEntity.ok(showerService.getAllShowers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getShowerById(@PathVariable int id) {
    Shower shower = showerService.getShowerById(id);
    if (shower == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(shower);
  }

  @GetMapping("/{id}/occupied")
  public ResponseEntity<?> isShowerOccupied(@PathVariable int id) {
    boolean isOccupied = showerService.isShowerOccupied(id);
    long remainingTime = showerService.getShowerById(id).getRemainingTime();
    return ResponseEntity.ok(isOccupied ? "Occupied, remaining time: " + remainingTime + " minutes" : "Available");
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<?> updateShowerStatus(@PathVariable int id, @RequestBody ShowerStatusUpdateRequest request) {
    Shower shower = showerService.getShowerById(id);
    if (shower == null) {
      return ResponseEntity.notFound().build();
    }

    showerService.setShowerStatus(id, request.isOnline(), request.isUnderMaintenance());
    return ResponseEntity.ok(showerService.getShowerById(id));
  }

  @Data
  private static class ShowerStatusUpdateRequest {
    private boolean online;
    private boolean underMaintenance;
  }
}
