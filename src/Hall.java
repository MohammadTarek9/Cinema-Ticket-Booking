//package org.example;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.PreparedStatement;

public class Hall {
    private int hall_no;
    private String sound_sys;
    private String screen_type;
    private int no_of_seats;
    private Cinema cinema;

    
    public Hall(int hall_no, String sound_sys, String screen_type, int no_of_seats, Cinema cinema) {
        this.hall_no = hall_no;
        this.sound_sys = sound_sys;
        this.screen_type = screen_type;
        this.no_of_seats = no_of_seats;
        this.cinema = cinema;
    }

    public Hall(String sound_sys, String screen_type, int no_of_seats, Cinema cinema) {
        this.sound_sys = sound_sys;
        this.screen_type = screen_type;
        this.no_of_seats = no_of_seats;
        this.cinema = cinema;
    }

    public Hall() {
    }

    public int getHall_no() {
        return hall_no;
    }

    public void setHall_no(int hall_no) {
        this.hall_no = hall_no;
    }

    public String getSound_sys() {
        return sound_sys;
    }

    public void setSound_sys(String sound_sys) {
        this.sound_sys = sound_sys;
    }

    public String getScreen_type() {
        return screen_type;
    }

    public void setScreen_type(String screen_type) {
        this.screen_type = screen_type;
    }

    public int getNo_of_seats() {
        return no_of_seats;
    }

    public void setNo_of_seats(int no_of_seats) {
        this.no_of_seats = no_of_seats;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    //sql
    public boolean addHall() {
    int hall_no;
    String sql = "{CALL AddHall(?, ?, ?, ?,?)}";
    try (CallableStatement pstmt = DatabaseConnector.getConnection().prepareCall(sql)) {
        pstmt.setString(1, sound_sys);
        pstmt.setString(2, screen_type);
        pstmt.setInt(3, no_of_seats);
        pstmt.setInt(4, cinema.getCinemaID());
        pstmt.registerOutParameter(5, Types.INTEGER); 

        pstmt.execute();
        hall_no = pstmt.getInt(5); // Get the generated hall number
        this.hall_no = hall_no; // Set the hall number in the object
        System.out.println("Hall added successfully with hall number: " + hall_no);
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    }

    //sql
    public static String deleteHall(int hall_no) {
        String query = "{CALL DeleteHall(?)}";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, hall_no);
            pstmt.executeUpdate();
            System.out.println("Hall with ID " + hall_no + " deleted successfully.");
            return "Success";
        } catch (SQLException e) {
           // e.printStackTrace();
            return "Couldn't Delete Hall, It has valid tickets.";
        }
    }

    //sql
    public boolean updateHall(int hall_no){
        String query = "{CALL UpdateHall(?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, sound_sys);
            pstmt.setString(2, screen_type);
            pstmt.setInt(3, no_of_seats);
            pstmt.setInt(4, cinema.getCinemaID());
            pstmt.setInt(5, hall_no);
            int rowsAffected = pstmt.executeUpdate();
            this.hall_no = hall_no; 
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addSeats(int hall_no){
        String query = "{CALL add_seats(?)}";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, hall_no);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteAllSeats(int hall_no){
        String query = "DELETE FROM seat WHERE hall_no = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, hall_no);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }
}
