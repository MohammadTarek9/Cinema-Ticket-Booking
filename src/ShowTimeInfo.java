//package org.example;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ShowTimeInfo {
    private LocalDate date;
    private LocalTime time;
    //private String cinemaName;

    // Constructor, getters, and setters
    public ShowTimeInfo(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
       // this.cinemaName = cinemaName;
    }

    // Getters and setters...
    @Override
    public String toString() {
        return String.format("%s",time.format(DateTimeFormatter.ofPattern("h:mm a")));
    }
}