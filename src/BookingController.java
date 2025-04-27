

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class BookingController implements AlertHelper {
    @FXML
    private VBox bookingContainer;
    @FXML
    private VBox movieDetailsBox;
    @FXML
    private ComboBox<String> showTimeCombo;
    @FXML
    private GridPane seatGrid;
    @FXML
    private Label selectedSeatsLabel;
    @FXML
    private ComboBox<String> menuItemsCombo;
    @FXML
    private Spinner<Integer> quantitySpinner;
    @FXML
    private VBox menuItemsBox;
    @FXML
    private Label seatsPriceLabel;
    @FXML
    private Label menuPriceLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private DatePicker datePicker;
    private Map<LocalDate, List<LocalTime>> showTimesByDate = new HashMap<>();


    private Movie currentMovie;
    private Map<String, LocalTime> showTimesMap = new HashMap<>();
    private Map<String, MenuItem> menuItemsMap = new HashMap<>();
    private Map<String, Button> seatButtons = new HashMap<>();
    private Set<String> selectedSeats = new HashSet<>();
    private List<OrderItem> orderedItems = new ArrayList<>();
    private double totalPrice = 0.0;
    private int currentShowID = -1;

    // Update initialize or setMovie method
    public void setMovie(Movie movie) {
        this.currentMovie = movie;
        displayMovieDetails();
        setupDatePicker();
        loadAvailableDates();
        loadMenuItems();
        // Update the show time combo box listener
        showTimeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && datePicker.getValue() != null) {
                loadSeatMap(datePicker.getValue(), newVal);
            }
        });
    }

    private void setupDatePicker() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !showTimesByDate.containsKey(date));
            }
        });

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadShowTimesForDate(newVal);
            }
        });
    }

    private void loadAvailableDates() {
        String query = "SELECT DISTINCT CAST(show_date AS DATE) as show_date FROM show " +
                "WHERE movieID = ? AND show_date >= CAST(GETDATE() AS DATE) " +
                "ORDER BY show_date";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentMovie.getMovieID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("show_date").toLocalDate();
                showTimesByDate.put(date, new ArrayList<>());
            }

            // Load times for each date
            for (LocalDate date : showTimesByDate.keySet()) {
                loadTimesForDate(date);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Database Error", "Failed to load available dates");
        }
    }

    private void loadTimesForDate(LocalDate date) {
        String query = "SELECT show_time FROM show " +
                "WHERE movieID = ? AND show_date = ? " +
                "ORDER BY show_time";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentMovie.getMovieID());
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            List<LocalTime> times = new ArrayList<>();
            while (rs.next()) {
                times.add(rs.getTime("show_time").toLocalTime());
            }
            showTimesByDate.put(date, times);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadShowTimesForDate(LocalDate date) {
        showTimeCombo.getItems().clear();
        showTimeCombo.setDisable(false);

        List<LocalTime> times = showTimesByDate.get(date);
        if (times != null) {
            for (LocalTime time : times) {
                showTimeCombo.getItems().add(time.format(DateTimeFormatter.ofPattern("h:mm a")));
            }
        }
    }

    // Updated loadSeatMap method
    private void loadSeatMap(LocalDate date, String timeDisplay) {
        seatGrid.getChildren().clear();
        selectedSeats.clear();
        selectedSeatsLabel.setText("None");
        updatePrice();

        try {
            // Parse the display time back to LocalTime
            LocalTime time = LocalTime.parse(timeDisplay, DateTimeFormatter.ofPattern("h:mm a"));

            // Combine date and time into LocalDateTime
            LocalDateTime showDateTime = LocalDateTime.of(date, time);

            String query = "SELECT s.showID, h.hall_no FROM show s " +
                    "JOIN movie m ON s.movieID = m.movieID " +
                    "JOIN hall h ON s.hall_no = h.hall_no " +
                    "WHERE m.movieID = ? " +
                    "AND s.show_date = ? " +
                    "AND CONVERT(TIME, s.show_time) = CONVERT(TIME, ?)";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, currentMovie.getMovieID());
                pstmt.setDate(2, Date.valueOf(date));
                pstmt.setString(3, time.toString());  // time is your LocalTime object

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    currentShowID = rs.getInt("showID");
                    int hallNo = rs.getInt("hall_no");
                    //System.out.println("here");
                    displaySeats(hallNo);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Database Error", "Failed to load seat map: " + e.getMessage());
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Error", "Invalid time format");
        }
    }

    private void displayMovieDetails() {
        movieDetailsBox.getChildren().clear();

        // Title and rating
        HBox titleBox = new HBox(10);
        Text title = new Text(currentMovie.getTitle());
        title.getStyleClass().add("movie-title");

        Text rating = new Text(String.format("★ %.1f", currentMovie.getRating()));
        rating.getStyleClass().add("rating");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        titleBox.getChildren().addAll(title, spacer, rating);

        // Genres
        Text genres = new Text(String.join(" • ", currentMovie.getGenres()));
        genres.getStyleClass().add("movie-detail");

        // Details grid
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(5);

        addDetailRow(detailsGrid, 0, "Director:", currentMovie.getDirector());
        addDetailRow(detailsGrid, 1, "Starring:", currentMovie.getLeadActor());
        addDetailRow(detailsGrid, 2, "Duration:", formatDuration(currentMovie.getDuration()));
        addDetailRow(detailsGrid, 3, "Language:", currentMovie.getMainLanguage());
        addDetailRow(detailsGrid, 4, "Release Date:",
                String.format("%d/%d/%d", currentMovie.getMovieDay(), currentMovie.getMovieMonth(), currentMovie.getMovieYear()));
        addDetailRow(detailsGrid, 5, "Censorship:", currentMovie.getCensorship());

        // Description
        Text description = new Text(currentMovie.getDescription());
        description.getStyleClass().add("movie-detail");
        description.setWrappingWidth(770);

        movieDetailsBox.getChildren().addAll(
                titleBox,
                genres,
                detailsGrid,
                description
        );
    }

    private void displaySeats(int hallNo) {
        // Get hall layout
        String hallQuery = "SELECT no_of_seats FROM hall WHERE hall_no = ?";
        int totalSeats = 0;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(hallQuery)) {

            pstmt.setInt(1, hallNo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                totalSeats = rs.getInt("no_of_seats");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get booked seats for this show
        Set<String> bookedSeats = new HashSet<>();
        String bookedQuery = "SELECT seat_no FROM ticket WHERE showID = ? AND booking_status = 'CONFIRMED'";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(bookedQuery)) {

            pstmt.setInt(1, currentShowID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bookedSeats.add(String.valueOf(rs.getInt("seat_no")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get seat types and prices
        Map<Integer, String> seatTypes = new HashMap<>();
        String seatQuery = "SELECT seat_no, seat_type FROM seat WHERE hall_no = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(seatQuery)) {

            pstmt.setInt(1, hallNo);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                seatTypes.put(rs.getInt("seat_no"), rs.getString("seat_type"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create seat grid (assuming 10 seats per row)
        int seatsPerRow = 10;
        int rows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < seatsPerRow; col++) {
                int seatNo = row * seatsPerRow + col + 1;
                if (seatNo > totalSeats) break;

                Button seatBtn = new Button(String.valueOf(seatNo));
                seatBtn.getStyleClass().add("seat-button");

                // Set seat style based on availability and type
                String seatKey = "R" + (row + 1) + "S" + seatNo;
                if (bookedSeats.contains(String.valueOf(seatNo))) {
                    seatBtn.getStyleClass().add("seat-booked");
                    seatBtn.setDisable(true);
                } else if ("VIP".equals(seatTypes.get(seatNo))) {
                    seatBtn.getStyleClass().add("seat-vip");
                    seatBtn.setTooltip(new Tooltip("VIP Seat - Higher Price"));
                } else {
                    seatBtn.getStyleClass().add("seat-available");
                }

                seatBtn.setOnAction(e -> toggleSeatSelection(seatBtn, seatNo));
                seatGrid.add(seatBtn, col, row);
                seatButtons.put(seatKey, seatBtn);
            }
        }
    }

    private void toggleSeatSelection(Button seatBtn, int seatNo) {
        String seatKey = "R" + (GridPane.getRowIndex(seatBtn) + 1) + "S" + seatNo;

        if (selectedSeats.contains(seatKey)) {
            selectedSeats.remove(seatKey);
            seatBtn.getStyleClass().remove("seat-selected");
            seatBtn.getStyleClass().add("seat-available");
        } else {
            selectedSeats.add(seatKey);
            seatBtn.getStyleClass().remove("seat-available");
            seatBtn.getStyleClass().add("seat-selected");
        }

        updateSelectedSeatsLabel();
        updatePrice();
    }

    private void updateSelectedSeatsLabel() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsLabel.setText("None");
        } else {
            selectedSeatsLabel.setText(String.join(", ", selectedSeats));
        }
    }

    private void loadMenuItems() {
        menuItemsCombo.getItems().clear();
        menuItemsMap.clear();

        String query = "SELECT itemID, item_name, price FROM menu_item";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.setItemID(rs.getInt("itemID"));
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getDouble("price"));

                String displayText = String.format("%s - $%.2f", item.getItemName(), item.getPrice());
                menuItemsMap.put(displayText, item);
                menuItemsCombo.getItems().add(displayText);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Database Error", "Failed to load menu items: " + e.getMessage());
        }
    }

    @FXML
    private void addMenuItem() {
        String selected = menuItemsCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Selection Error", "Please select a menu item first");
            return;
        }

        int quantity = quantitySpinner.getValue();
        MenuItem item = menuItemsMap.get(selected);

        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItem(item);
        orderItem.setQuantity(quantity);
        orderedItems.add(orderItem);

        // Add to display
        HBox itemBox = new HBox(10);
        itemBox.getStyleClass().add("menu-item-box");

        Label itemLabel = new Label(String.format("%s x%d - $%.2f",
                item.getItemName(), quantity, item.getPrice() * quantity));
        itemLabel.getStyleClass().add("selected-items-label");

        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("remove-button");
        removeBtn.setOnAction(e -> {
            menuItemsBox.getChildren().remove(itemBox);
            orderedItems.remove(orderItem);
            updatePrice();
        });

        itemBox.getChildren().addAll(itemLabel, removeBtn);
        menuItemsBox.getChildren().add(itemBox);

        updatePrice();
    }

    private void updatePrice() {
        double seatsPrice = calculateSeatsPrice();
        double menuPrice = calculateMenuPrice();
        totalPrice = seatsPrice + menuPrice;

        seatsPriceLabel.setText(String.format("$%.2f", seatsPrice));
        menuPriceLabel.setText(String.format("$%.2f", menuPrice));
        totalPriceLabel.setText(String.format("$%.2f", totalPrice));
    }

    private double calculateSeatsPrice() {
        if (currentShowID == -1 || selectedSeats.isEmpty()) return 0.0;

        // Get base price from screen type
        String priceQuery = "SELECT sc.price FROM hall h " +
                "JOIN screen sc ON h.screen_type = sc.screen_type " +
                "JOIN show s ON h.hall_no = s.hall_no " +
                "WHERE s.showID = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(priceQuery)) {

            pstmt.setInt(1, currentShowID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double basePrice = rs.getDouble("price");
                return basePrice * selectedSeats.size();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    private double calculateMenuPrice() {
        return orderedItems.stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
    }

    @FXML
    private void confirmBooking() {
        if (currentShowID == -1) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Selection Error", "Please select a show time first");
            return;
        }

        if (selectedSeats.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Selection Error", "Please select at least one seat");
            return;
        }

        // In a real application, you would:
        // 1. Collect customer information
        // 2. Create payment record
        // 3. Create tickets
        // 4. Add menu items to order
        // 5. Show confirmation

        AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Booking Confirmation",
                String.format("Booking confirmed for %s!\nSeats: %s\nTotal: $%.2f",
                        currentMovie.getTitle(),
                        String.join(", ", selectedSeats),
                        totalPrice));
    }

    @FXML
    private void goBackToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
            Stage stage = (Stage) bookingContainer.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Error", "Failed to return to home page: " + e.getMessage());
        }
    }

    // Helper methods
    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%dh %02dm", hours, mins);
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Text labelText = new Text(label);
        labelText.getStyleClass().add("movie-detail-label");

        Text valueText = new Text(value);
        valueText.getStyleClass().add("movie-detail");

        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }
}