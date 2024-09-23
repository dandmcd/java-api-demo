package com.dantest.demo.restservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    private String email;
    private String phoneNumber;
    private LocalDateTime reservationTime;
    private int pinCode;
    private int showerId;
    private int durationInMinutes;
}