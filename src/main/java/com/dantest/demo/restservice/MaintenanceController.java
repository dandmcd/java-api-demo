package com.dantest.demo.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Data;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

  @Autowired
  private MaintenanceService maintenanceService;

  @Autowired
  private ShowerService showerService;

  @PostMapping("/flag")
  public ResponseEntity<?> flagShowerForMaintenance(@RequestBody MaintenanceRequest request) {
    if (showerService.getShowerById(request.getShowerId()) == null) {
      return ResponseEntity.badRequest().body("Invalid Shower ID.");
    }

    maintenanceService.flagShowerForMaintenance(request.getShowerId());
    showerService.setShowerStatus(request.getShowerId(), false, true);
    return ResponseEntity.ok("Shower " + request.getShowerId() + " flagged for maintenance.");
  }

  @PostMapping("/complete")
  public ResponseEntity<?> completeMaintenance(@RequestBody MaintenanceRequest request) {
    if (showerService.getShowerById(request.getShowerId()) == null) {
      return ResponseEntity.badRequest().body("Invalid Shower ID.");
    }

    maintenanceService.completeMaintenance(request.getShowerId());
    showerService.setShowerStatus(request.getShowerId(), true, false); // Set online and not under maintenance
    return ResponseEntity.ok("Maintenance completed for Shower " + request.getShowerId() + ".");
  }

  @GetMapping
  public ResponseEntity<List<?>> getAllMaintenanceRequests() {
    return ResponseEntity.ok(maintenanceService.getAllMaintenanceRequests());
  }

  @Data
  private static class MaintenanceRequest {
    private int showerId;
  }
}
