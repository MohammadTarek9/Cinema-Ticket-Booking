//package org.example;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Seat {

    private Hall hall;
    private int seat_no;
    private boolean seatStatus;
    private String seatType;

    public Seat(Hall hall, int seat_no, boolean seatStatus, String seatType) {
        this.hall = hall;
        this.seat_no = seat_no;
        this.seatStatus = seatStatus;
        this.seatType = seatType;
    }

    public Seat(Hall hall, boolean seatStatus, String seatType) {
        this.hall = hall;
        this.seatStatus = seatStatus;
        this.seatType = seatType;
    }

    public Seat() {
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public int getSeat_no() {
        return seat_no;
    }

    public void setSeat_no(int seat_no) {
        this.seat_no = seat_no;
    }

    public boolean isSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(boolean seatStatus) {
        this.seatStatus = seatStatus;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }


    
}
