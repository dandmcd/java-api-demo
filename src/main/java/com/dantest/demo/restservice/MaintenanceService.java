package com.dantest.demo.restservice;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaintenanceService {

    private List<MaintenanceRequest> maintenanceRequests = new ArrayList<>();

    public void flagShowerForMaintenance(int showerId) {
        maintenanceRequests.add(new MaintenanceRequest(showerId));
    }

    public void completeMaintenance(int showerId) {
        maintenanceRequests.removeIf(request -> request.getShowerId() == showerId);
    }

    public List<MaintenanceRequest> getAllMaintenanceRequests() {
        return maintenanceRequests;
    }
}

