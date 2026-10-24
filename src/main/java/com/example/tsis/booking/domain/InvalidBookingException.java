package com.example.tsis.booking.domain;

public class InvalidBookingException extends RuntimeException {
    public InvalidBookingException(String message) { super(message); }
}