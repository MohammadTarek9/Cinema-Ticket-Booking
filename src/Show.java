package org.asu;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

public class Show {
    private int showID;

    public int getShowID() {
        return showID;
    }

    public void setShowID(int showID) {
        this.showID = showID;
    }

    public int getHall_no() {
        return hall_no;
    }

    public void setHall_no(int hall_no) {
        this.hall_no = hall_no;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public LocalDate getShow_date() {
        return show_date;
    }

    public void setShow_date(LocalDate show_date) {
        this.show_date = show_date;
    }

    public LocalTime getShow_time() {
        return show_time;
    }

    public void setShow_time(LocalTime show_time) {
        this.show_time = show_time;
    }

    private int hall_no;
    private int movieID;
    private LocalDate show_date;
    private LocalTime show_time;

    public Show(int showID, int hall_no, int movieID, LocalDate show_date, LocalTime show_time) {
        this(hall_no, movieID, show_date, show_time);
        this.showID = showID;
    }

    public Show(int hall_no, int movieID, LocalDate show_date, LocalTime show_time) {
        this.hall_no = hall_no;
        this.movieID = movieID;
        this.show_date = show_date;
        this.show_time = show_time;
    }

    public Show() {

    }

    public boolean addShow() {
        String sql = "INSERT INTO show VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, hall_no);
            pstmt.setInt(2, movieID);
            pstmt.setString(3, show_date.toString());
            pstmt.setString(4, show_time.toString());

            int rowsAffected = pstmt.executeUpdate();
            setLastShowID(pstmt);

            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error Adding show: " + e.getMessage());
            return false;
        }
    }

    private boolean setLastShowID(PreparedStatement stmt) {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                showID = generatedKeys.getInt(1);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean updateShow(int showID) {
        this.showID = showID;
        String sql = "UPDATE show SET " + "hall_no = ?, " + "movieID = ?, " + "show_date = ?, " + "show_time = ? " + "WHERE showID = ?";

        try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, hall_no);
            pstmt.setInt(2, movieID);
            pstmt.setString(3, show_date.toString());
            pstmt.setString(4, show_time.toString());
            pstmt.setInt(5, showID);

            int rowsAffected = pstmt.executeUpdate();
            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating show: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteShow(int showID) {
        String sql = "DELETE FROM show WHERE showID = ?";

        try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, showID);

            int rowsAffected = pstmt.executeUpdate();

            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting show: " + e.getMessage());
            return false;
        }
    }
}