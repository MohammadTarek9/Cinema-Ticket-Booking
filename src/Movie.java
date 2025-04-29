
//package org.example;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Set;

public class Movie {
    private int movieID;
    private String title;
    private String description;
    private String mainLanguage;
    private int duration;
    private int movieDay;
    private int movieMonth;
    private int movieYear;
    private boolean nowShowing;

    @Override
    public String toString() {
        return "Movie{" + "movieID=" + movieID + ", title='" + title + '\'' + ", description='" + description + '\'' + ", mainLanguage='" + mainLanguage + '\'' + ", duration=" + duration + ", movieDay=" + movieDay + ", movieMonth=" + movieMonth + ", movieYear=" + movieYear + ", nowShowing=" + nowShowing + ", censorship='" + censorship + '\'' + ", rating=" + rating + ", leadActor='" + leadActor + '\'' + ", director='" + director + '\'' + ", genres=" + genres + '}';
    }

    private String censorship;
    private double rating;
    private String leadActor;
    private String director;
    private Set<String> genres;

    public Movie(String title, String director, String leadActor, int duration, String language, double rating, String description, LocalDate releaseDate, String censorRating, boolean isAvailable, Set<String> genres) {
        this.title = title;
        this.director = director;
        this.leadActor = leadActor;
        this.duration = duration;
        this.mainLanguage = language;
        this.rating = rating;
        this.description = description;
        this.setReleaseDate(releaseDate.getDayOfMonth(), releaseDate.getMonthValue(), releaseDate.getYear());
        this.censorship = censorRating;
        this.nowShowing = isAvailable;
        this.genres = genres;
    }

    public Movie() {

    }


    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    // Getters and Setters
    public int getMovieID() {
        return movieID;
    }

    public void setLastMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainLanguage() {
        return mainLanguage;
    }

    public void setMainLanguage(String mainLanguage) {
        this.mainLanguage = mainLanguage;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMovieDay() {
        return movieDay;
    }

    public int getMovieMonth() {
        return movieMonth;
    }

    public int getMovieYear() {
        return movieYear;
    }

    public void setReleaseDate(int day, int month, int year) {
        this.movieDay = day;
        this.movieMonth = month;
        this.movieYear = year;
    }

    public boolean isNowShowing() {
        return nowShowing;
    }

    public void setNowShowing(boolean nowShowing) {
        this.nowShowing = nowShowing;
    }

    public void setNowShowing(int nowShowing) {
        this.nowShowing = nowShowing == 1;
    }

    public String getCensorship() {
        return censorship;
    }

    public void setCensorship(String censorship) {
        this.censorship = censorship;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLeadActor() {
        return leadActor;
    }

    public void setLeadActor(String leadActor) {
        this.leadActor = leadActor;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    //sql
    public boolean addMovie() {
        String sql = "{CALL AddMovie(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"; // Calling AddMovie stored procedure

        try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, mainLanguage);
            pstmt.setInt(4, duration);
            pstmt.setInt(5, movieDay);
            pstmt.setInt(6, movieMonth);
            pstmt.setInt(7, movieYear);
            pstmt.setBoolean(8, nowShowing);
            pstmt.setString(9, censorship);
            pstmt.setDouble(10, rating);
            pstmt.setString(11, leadActor);
            pstmt.setString(12, director);


            setLastMovieID(pstmt);
            int rowsAffected = pstmt.executeUpdate();
            addCategory();
            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error Adding movie: " + e.getMessage());
            return false;
        }
    }

    //sql
    public void addCategory() {
        String sql = "{CALL AddCategory(?,?)}";
        try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql)) {
            for (String genre : genres) {
                pstmt.setString(1, genre);
                pstmt.setInt(2, movieID);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error adding categories: " + e.getMessage());
        }
    }

    //sql
    private boolean setLastMovieID(PreparedStatement stmt) {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                movieID = generatedKeys.getInt(1);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    //sql
    public boolean updateMovie(int movieID) {
        this.movieID = movieID;
        String sql ="{CALL UpdateMovie(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"; // Calling UpdateMovie stored procedure

        try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, mainLanguage);
            pstmt.setInt(4, duration);
            pstmt.setInt(5, movieDay);
            pstmt.setInt(6, movieMonth);
            pstmt.setInt(7, movieYear);
            pstmt.setBoolean(8, nowShowing);
            pstmt.setString(9, censorship);
            pstmt.setDouble(10, rating);
            pstmt.setString(11, leadActor);
            pstmt.setString(12, director);
            pstmt.setInt(13, movieID);

            int rowsAffected = pstmt.executeUpdate();
            dropCategory();
            addCategory();
            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating movie: " + e.getMessage());
            return false;
        }
    }

    //sql
    public void dropCategory() {
        String sql = "{CALL DropCategory(?)}";
        try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, movieID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting categories: " + e.getMessage());
        }
    }

    //sql
    public static boolean deleteMovie(int movieID) {
        String sql = "{CALL DeleteMovie(?)}";

        try (PreparedStatement pstmt = DatabaseConnector.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, movieID);

            int rowsAffected = pstmt.executeUpdate();

            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting movie: " + e.getMessage());
            return false;
        }
    }
}