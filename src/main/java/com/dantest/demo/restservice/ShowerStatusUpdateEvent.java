package com.dantest.demo.restservice;

import org.springframework.context.ApplicationEvent;
import java.util.List;

public class ShowerStatusUpdateEvent extends ApplicationEvent {
    private final List<Shower> showers;

    public ShowerStatusUpdateEvent(Object source, List<Shower> showers) {
        super(source);
        this.showers = showers;
    }

    public List<Shower> getShowers() {
        return showers;
    }
}