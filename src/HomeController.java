

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class HomeController implements AlertHelper {
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> genreFilter;
    @FXML
    private ComboBox<String> durationFilter;
    @FXML
    private ComboBox<String> timeFilter;
    @FXML
    private ComboBox<String> directorFilter;
    @FXML
    private VBox moviesContainer;

    private List<Movie> allMovies;
    private List<Movie> nowShowingMovies;
    private Map<Integer, List<LocalTime>> movieShowTimes = new HashMap<>();

    @FXML
    public void initialize() {
        loadMovies();
        loadShowTimes();
        setupFilters();
    }

    private void loadMovies() {
        allMovies = new ArrayList<>();
        nowShowingMovies = new ArrayList<>();
        String query = "SELECT * FROM movie";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Movie movie = createMovieFromResultSet(rs);
                allMovies.add(movie);

                if (movie.isNowShowing()) {
                    nowShowingMovies.add(movie);
                }
            }

            displayMovies(nowShowingMovies);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load movies: " + e.getMessage());
        }
    }

    private void loadShowTimes() {
        movieShowTimes = new HashMap<>();
        String query = "SELECT s.movieID, s.show_time FROM show s JOIN movie m ON s.movieID = m.movieID WHERE m.now_showing = 1";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int movieID = rs.getInt("movieID");
                Time showTime = rs.getTime("show_time");
                LocalTime time = showTime.toLocalTime();

                movieShowTimes.computeIfAbsent(movieID, k -> new ArrayList<>()).add(time);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Movie createMovieFromResultSet(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setLastMovieID(rs.getInt("movieID"));
        movie.setTitle(rs.getString("title"));
        movie.setDescription(rs.getString("description"));
        movie.setMainLanguage(rs.getString("main_language"));
        movie.setDuration(rs.getInt("duration"));
        movie.setReleaseDate(
                rs.getInt("movie_day"),
                rs.getInt("movie_month"),
                rs.getInt("movie_year")
        );
        movie.setNowShowing(rs.getBoolean("now_showing"));
        movie.setCensorship(rs.getString("censorship"));
        movie.setRating(rs.getDouble("rating"));
        movie.setLeadActor(rs.getString("lead_actor"));
        movie.setDirector(rs.getString("director"));

        Set<String> genres = getGenresForMovie(movie.getMovieID());
        movie.setGenres(genres);

        return movie;
    }

    private Set<String> getGenresForMovie(int movieID) throws SQLException {
        Set<String> genres = new HashSet<>();
        String query = "SELECT movie_genre FROM genre WHERE movieID = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, movieID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                genres.add(rs.getString("movie_genre"));
            }
        }
        return genres;
    }

    private void setupFilters() {
        // Set initial prompt texts
        genreFilter.setPromptText("Filter by Genre");
        durationFilter.setPromptText("Filter by Duration");
        timeFilter.setPromptText("Filter by Show Time");
        directorFilter.setPromptText("Filter by Director");

        // Genre filter
        Set<String> allGenres = new HashSet<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT movie_genre FROM genre")) {

            while (rs.next()) {
                allGenres.add(rs.getString("movie_genre"));
            }
            genreFilter.getItems().addAll(allGenres);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Duration filter
        durationFilter.getItems().addAll(
                "Under 90 mins",
                "90-120 mins",
                "Over 120 mins"
        );

        // Time filter
        timeFilter.getItems().addAll(
                "Morning (Before 12PM)",
                "Afternoon (12PM-5PM)",
                "Evening (5PM-10PM)",
                "Night (After 10PM)"
        );

        // Director filter
        Set<String> directors = new HashSet<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT director FROM movie")) {

            while (rs.next()) {
                directors.add(rs.getString("director"));
            }
            directorFilter.getItems().addAll(directors);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add listeners to filters
        genreFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterMovies());
        durationFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterMovies());
        timeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterMovies());
        directorFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterMovies());
    }

    private void filterMovies() {
        List<Movie> filtered = new ArrayList<>(nowShowingMovies);

        // Apply genre filter
        if (genreFilter.getValue() != null) {
            filtered.removeIf(movie -> !movie.getGenres().contains(genreFilter.getValue()));
        }

        // Apply duration filter
        if (durationFilter.getValue() != null) {
            switch (durationFilter.getValue()) {
                case "Under 90 mins":
                    filtered.removeIf(movie -> movie.getDuration() >= 90);
                    break;
                case "90-120 mins":
                    filtered.removeIf(movie -> movie.getDuration() < 90 || movie.getDuration() > 120);
                    break;
                case "Over 120 mins":
                    filtered.removeIf(movie -> movie.getDuration() <= 120);
                    break;
            }
        }

        // Apply time filter
        if (timeFilter.getValue() != null && !movieShowTimes.isEmpty()) {
            filtered = filtered.stream()
                    .filter(movie -> {
                        List<LocalTime> times = movieShowTimes.get(movie.getMovieID());
                        if (times == null) return false;

                        switch (timeFilter.getValue()) {
                            case "Morning (Before 12PM)":
                                return times.stream().anyMatch(t -> t.isBefore(LocalTime.NOON));
                            case "Afternoon (12PM-5PM)":
                                return times.stream().anyMatch(t ->
                                        !t.isBefore(LocalTime.NOON) && t.isBefore(LocalTime.of(17, 0)));
                            case "Evening (5PM-10PM)":
                                return times.stream().anyMatch(t ->
                                        !t.isBefore(LocalTime.of(17, 0)) && t.isBefore(LocalTime.of(22, 0)));
                            case "Night (After 10PM)":
                                return times.stream().anyMatch(t -> !t.isBefore(LocalTime.of(22, 0)));
                            default:
                                return true;
                        }
                    })
                    .collect(Collectors.toList());
        }

        // Apply director filter
        if (directorFilter.getValue() != null) {
            filtered.removeIf(movie -> !movie.getDirector().equals(directorFilter.getValue()));
        }

        displayMovies(filtered);
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            clearFilters();
            return;
        }

        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (movie.getTitle().toLowerCase().contains(searchTerm) ||
                    movie.getDescription().toLowerCase().contains(searchTerm) ||
                    movie.getLeadActor().toLowerCase().contains(searchTerm) ||
                    movie.getDirector().toLowerCase().contains(searchTerm)) {
                filtered.add(movie);
            }
        }

        displayMovies(filtered, true); // true indicates search mode
    }

    @FXML
    private void clearFilters() {
        searchField.clear();

        // Reset each combo box
        resetComboBox(genreFilter, "Filter by Genre");
        resetComboBox(durationFilter, "Filter by Duration");
        resetComboBox(timeFilter, "Filter by Show Time");
        resetComboBox(directorFilter, "Filter by Director");

        displayMovies(nowShowingMovies);
    }

    private void resetComboBox(ComboBox<String> comboBox, String promptText) {
        comboBox.getSelectionModel().clearSelection();
        comboBox.setPromptText(promptText);

        // This ensures the prompt text is visible
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(promptText);
                    setStyle("-fx-text-fill: #aaa;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: white;");
                }
            }
        });
    }

    private void displayMovies(List<Movie> movies) {
        displayMovies(movies, false);
    }

    private void displayMovies(List<Movie> movies, boolean isSearchMode) {
        moviesContainer.getChildren().clear();

        if (movies.isEmpty()) {
            Text noResults = new Text("No movies found matching your criteria.");
            noResults.getStyleClass().add("movie-detail");
            moviesContainer.getChildren().add(noResults);
            return;
        }

        for (Movie movie : movies) {
            VBox movieCard = new VBox(10);
            movieCard.getStyleClass().add("movie-card");
            movieCard.setPrefWidth(800);

            // Title and rating
            HBox titleBox = new HBox(10);
            Text title = new Text(movie.getTitle());
            title.getStyleClass().add("movie-title");

            Text rating = new Text(String.format("★ %.1f", movie.getRating()));
            rating.getStyleClass().add("rating");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Text nowShowing = new Text(movie.isNowShowing() ? "NOW SHOWING" : "COMING SOON");
            nowShowing.getStyleClass().add(movie.isNowShowing() ? "now-showing" : "coming-soon");

            titleBox.getChildren().addAll(title, spacer, rating, nowShowing);

            // Genres
            Text genres = new Text(String.join(" • ", movie.getGenres()));
            genres.getStyleClass().add("movie-detail");

            // Details grid
            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(10);
            detailsGrid.setVgap(5);

            addDetailRow(detailsGrid, 0, "Director:", movie.getDirector());
            addDetailRow(detailsGrid, 1, "Starring:", movie.getLeadActor());
            addDetailRow(detailsGrid, 2, "Duration:", formatDuration(movie.getDuration()));
            addDetailRow(detailsGrid, 3, "Language:", movie.getMainLanguage());
            addDetailRow(detailsGrid, 4, "Release Date:",
                    String.format("%d/%d/%d", movie.getMovieDay(), movie.getMovieMonth(), movie.getMovieYear()));
            addDetailRow(detailsGrid, 5, "Censorship:", movie.getCensorship());

            // Show times (only for now showing movies)
            if (movie.isNowShowing() && movieShowTimes.containsKey(movie.getMovieID())) {
                String showTimes = movieShowTimes.get(movie.getMovieID()).stream()
                        .sorted()
                        .map(t -> t.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a")))
                        .collect(Collectors.joining(", "));
                addDetailRow(detailsGrid, 6, "Show Times:", showTimes);
            }

            // Description
            Text description = new Text(movie.getDescription());
            description.getStyleClass().add("movie-detail");
            description.setWrappingWidth(770);

            // Book button
            Button bookButton = new Button("Book Tickets");
            bookButton.getStyleClass().add("book-button");

            if (movie.isNowShowing()) {
                bookButton.setOnAction(e -> openBookingPage(movie));
            } else {
                bookButton.setDisable(true);
                bookButton.setTooltip(new Tooltip("This movie is not currently showing"));
                bookButton.getStyleClass().add("disabled-button");
            }

            movieCard.getChildren().addAll(
                    titleBox,
                    genres,
                    detailsGrid,
                    description,
                    bookButton
            );

            moviesContainer.getChildren().add(movieCard);
        }
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Text labelText = new Text(label);
        labelText.getStyleClass().add("movie-detail-label");

        Text valueText = new Text(value);
        valueText.getStyleClass().add("movie-detail");

        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }

    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%dh %02dm", hours, mins);
    }

    private void openBookingPage(Movie movie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookingPage.fxml"));
            Parent root = loader.load();

            BookingController controller = loader.getController();
            controller.setMovie(movie);

            Stage stage = (Stage) moviesContainer.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Book Tickets for " + movie.getTitle());

        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to load booking page: " + e.getMessage());
        }
    }
}