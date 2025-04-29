//package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Cinema {
    private int cinemaID;
    private String cinemaName;
    private String contact_no;
    private String opening_hours;
    private String closing_hours;
    private String street;
    private String city;
    private String district;
    
    public Cinema(int cinemaID, String cinemaName, String contact_no, String opening_hours, String closing_hours, String street, String city, String district) {
        this.cinemaID = cinemaID;
        this.cinemaName = cinemaName;
        this.contact_no = contact_no;
        this.opening_hours = opening_hours;
        this.closing_hours = closing_hours;
        this.street = street;
        this.city = city;
        this.district = district;
    }

    public Cinema(String cinemaName, String contact_no, String opening_hours, String closing_hours, String street, String city, String district) {
        this.cinemaName = cinemaName;
        this.contact_no = contact_no;
        this.opening_hours = opening_hours;
        this.closing_hours = closing_hours;
        this.street = street;
        this.city = city;
        this.district = district;
    }

    public Cinema() {
    }

    public int getCinemaID() {
        return cinemaID;
    }

    public void setCinemaID(int cinemaID) {
        this.cinemaID = cinemaID;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(String opening_hours) {
        this.opening_hours = opening_hours;
    }

    public String getClosing_hours() {
        return closing_hours;
    }

    public void setClosing_hours(String closing_hours) {
        this.closing_hours = closing_hours;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }   

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public String toString() {
        return "Cinema{" +
                "cinemaID=" + cinemaID +
                ", cinemaName='" + cinemaName + '\'' +
                ", contact_no='" + contact_no + '\'' +
                ", opening_hours='" + opening_hours + '\'' +
                ", closing_hours='" + closing_hours + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                '}';
    }

    //sql
    public boolean addCinema() {
        String sql = "{CALL AddCinema(?, ?, ?, ?, ?, ?, ?)}"; // Calling AddCinema stored procedure

        try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, cinemaName);
            pstmt.setString(2, contact_no);
            pstmt.setString(3, opening_hours);
            pstmt.setString(4, closing_hours);
            pstmt.setString(5, street);
            pstmt.setString(6, city);
            pstmt.setString(7, district);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("new cinema added successfully");
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error Adding cinema: " + e.getMessage());
            return false;
        }
    }

    //sql
    public static void deleteCinema(int cinemaID) {
        String query = "{CALL DeleteCinema(?)}"; // Calling DeleteCinema stored procedure

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, cinemaID);
            pstmt.executeUpdate();
            System.out.println("Cinema with ID " + cinemaID + " deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete cinema: " + e.getMessage());
        }
    }

    //sql
    public boolean updateCinema(int cinemaID) {
        String query = "{CALL UpdateCinema(?, ?, ?, ?, ?, ?, ?,?)}";;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cinemaName);
            pstmt.setString(2, contact_no);
            pstmt.setString(3, opening_hours);
            pstmt.setString(4, closing_hours);
            pstmt.setString(5, street);
            pstmt.setString(6, city);
            pstmt.setString(7, district);
            pstmt.setInt(8, cinemaID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //sql
    public static Cinema getCinemaByID(int cinemaID) {
        String query = "{CALL GetCinemaByID(?)}";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, cinemaID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Cinema(
                        rs.getInt("cinemaID"),
                        rs.getString("cinema_name"),
                        rs.getString("contact_no"),
                        rs.getString("opening_hours"),
                        rs.getString("closing_hours"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("district")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
