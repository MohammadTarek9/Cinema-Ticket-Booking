import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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

    public boolean addHall() {
    String sql = "INSERT INTO hall (sound_sys, screen_type, no_of_seats, cinemaID) VALUES (?, ?, ?, ?)";
    try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        pstmt.setString(1, sound_sys);
        pstmt.setString(2, screen_type);
        pstmt.setInt(3, no_of_seats);
        pstmt.setInt(4, cinema.getCinemaID());

        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
        try (var generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
            this.hall_no = generatedKeys.getInt(1);
            }
        }
        }

        return rowsAffected > 0;

    } catch (SQLException e) {
        System.err.println("Error Adding hall: " + e.getMessage());
        return false;
    }
    }

    public static void deleteHall(int hall_no) {
        String query = "DELETE FROM hall WHERE hall_no = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, hall_no);
            pstmt.executeUpdate();
            System.out.println("Hall with ID " + hall_no + " deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateHall(int hall_no){
        String query = "UPDATE hall SET sound_sys = ?, screen_type = ?, no_of_seats = ?, cinemaID = ? WHERE hall_no = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, sound_sys);
            pstmt.setString(2, screen_type);
            pstmt.setInt(3, no_of_seats);
            pstmt.setInt(4, cinema.getCinemaID());
            pstmt.setInt(5, hall_no);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
