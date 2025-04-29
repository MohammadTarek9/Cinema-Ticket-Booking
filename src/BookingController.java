//package org.example;

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
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class BookingController {
    @FXML private VBox bookingContainer;
    @FXML private VBox movieDetailsBox;
    @FXML private ComboBox<String> showTimeCombo;
    @FXML private GridPane seatGrid;
    @FXML private Label selectedSeatsLabel;
    @FXML private ComboBox<String> menuItemsCombo;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private VBox menuItemsBox;
    @FXML private Label seatsPriceLabel;
    @FXML private Label menuPriceLabel;
    @FXML private Label totalPriceLabel;
    @FXML private DatePicker datePicker;
    private Map<LocalDate, List<LocalTime>> showTimesByDate = new HashMap<>();
    private List<Integer> selectedMenuItemIds = new ArrayList<>();


    private Movie currentMovie;
    private Map<String, LocalTime> showTimesMap = new HashMap<>();
    private Map<String, MenuItem> menuItemsMap = new HashMap<>();
    private Map<String, Button> seatButtons = new HashMap<>();
    private Set<String> selectedSeats = new HashSet<>();
    private List<OrderItem> orderedItems = new ArrayList<>();
    private double totalPrice = 0.0;
    private int currentShowID = -1;


/******************************** GUI functions ********************************************************/
    //setMovie method
    public void setMovie(Movie movie) {
        this.currentMovie = movie;
        displayMovieDetails();
        setupDatePicker();
        loadAvailableDates();
        loadMenuItems();

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

    @FXML
    private void addMenuItem() {
        String selected = menuItemsCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a menu item first");
            return;
        }

        int quantity = quantitySpinner.getValue();
        MenuItem item = menuItemsMap.get(selected);

        selectedMenuItemIds.add(item.getItemID());

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
            selectedMenuItemIds.remove(Integer.valueOf(item.getItemID()));
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

    private double calculateMenuPrice() {
        return orderedItems.stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
    }

    @FXML
    private void confirmBooking() {
        if (currentShowID == -1) {
            showAlert("Selection Error", "Please select a show time first");
            return;
        }

        if (selectedSeats.isEmpty()) {
            showAlert("Selection Error", "Please select at least one seat");
            return;
        }

        // In a real application, you would:
        // 1. Collect customer information
        // 2. Create payment record
        // 3. Create tickets
        // 4. Add menu items to order
        // 5. Show confirmation

        showAlert("Booking Confirmation",
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
            stage.setScene(new Scene(root,1200, 700));
            stage.setTitle("Cinema Booking System");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to home page: " + e.getMessage());
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


/******************************** End of GUI functions *************************************************/

/**************************** Start of SQL functions ***************************************************/
    private void loadAvailableDates() {
        String sql = "{call sp_GetMovieShowTimes(?)}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, currentMovie.getMovieID());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("show_date").toLocalDate();
                if (!showTimesByDate.containsKey(date)) {
                    showTimesByDate.put(date, new ArrayList<>());
                }
                showTimesByDate.get(date).add(rs.getTime("show_time").toLocalTime());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load available dates");
        }
    }

    // Updated loadSeatMap method
    private void loadSeatMap(LocalDate date, String timeDisplay) {
        seatGrid.getChildren().clear();
        selectedSeats.clear();
        selectedSeatsLabel.setText("None");
        updatePrice();

        try {
            LocalTime time = LocalTime.parse(timeDisplay, DateTimeFormatter.ofPattern("h:mm a"));
            LocalDateTime showDateTime = LocalDateTime.of(date, time);

            String sql = "{ call GetShowAndHallForMovie(?, ?, ?) }";

            try (Connection conn = DatabaseConnector.getConnection();
                 CallableStatement cstmt = conn.prepareCall(sql)) {

                cstmt.setInt(1, currentMovie.getMovieID());
                cstmt.setDate(2, Date.valueOf(date));
                cstmt.setTime(3, Time.valueOf(time));

                ResultSet rs = cstmt.executeQuery();

                if (rs.next()) {
                    currentShowID = rs.getInt("showID");
                    int hallNo = rs.getInt("hall_no");
                    displaySeats(hallNo);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load seat map: " + e.getMessage());
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            showAlert("Error", "Invalid time format");
        }
    }

    private void displaySeats(int hallNo) {
        String sql = "{call sp_GetSeatMapForShow(?)}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, currentShowID);
            ResultSet rs = stmt.executeQuery();

            // Clear existing seats
            seatGrid.getChildren().clear();
            selectedSeats.clear();
            selectedSeatsLabel.setText("None");
            updatePrice();

            // Create seat grid
            int seatsPerRow = 10;
            int row = 0, col = 0;

            while (rs.next()) {
                int seatNo = rs.getInt("seat_no");
                String seatType = rs.getString("seat_type");
                boolean isAvailable = rs.getBoolean("is_available");

                Button seatBtn = new Button(String.valueOf(seatNo));
                seatBtn.getStyleClass().add("seat-button");

                String seatKey = "R" + (row + 1) + "S" + seatNo;

                if (!isAvailable) {
                    seatBtn.getStyleClass().add("seat-booked");
                    seatBtn.setDisable(true);
                } else if ("VIP".equals(seatType)) {
                    seatBtn.getStyleClass().add("seat-vip");
                    seatBtn.setTooltip(new Tooltip("VIP Seat - Higher Price"));
                } else {
                    seatBtn.getStyleClass().add("seat-available");
                }

                seatBtn.setOnAction(e -> toggleSeatSelection(seatBtn, seatNo));
                seatGrid.add(seatBtn, col, row);
                seatButtons.put(seatKey, seatBtn);

                col++;
                if (col >= seatsPerRow) {
                    col = 0;
                    row++;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load seat map");
        }
    }

    private void loadMenuItems() {
        menuItemsCombo.getItems().clear();
        menuItemsMap.clear();

        String sql = "{call sp_GetMenuItems()}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.setItemID(rs.getInt("itemID"));
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getDouble("price"));
                item.setSize(rs.getString("size"));
                item.setCategory(rs.getString("category"));

                String displayText = String.format("%s - $%.2f", item.getItemName(), item.getPrice());
                menuItemsMap.put(displayText, item);
                menuItemsCombo.getItems().add(displayText);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load menu items: " + e.getMessage());
        }
    }

    private double calculateSeatsPrice() {
        if (currentShowID == -1 || selectedSeats.isEmpty()) return 0.0;

        String sql = "{ call GetBasePriceByShowID(?) }";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, currentShowID);

            ResultSet rs = cstmt.executeQuery();

            if (rs.next()) {
                double basePrice = rs.getDouble("price");
                return basePrice * selectedSeats.size();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    @FXML
    private void proceedToPayment() {
        if (currentShowID == -1 || selectedSeats.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Please select showtime and seats first!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Payment.fxml"));
            Parent root = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setBookingData(
                currentMovie,
                currentShowID,
                new ArrayList<>(selectedSeats),
                orderedItems,
                totalPrice,
                selectedMenuItemIds  // Pass the selected item IDs
            );

            Stage stage = (Stage) bookingContainer.getScene().getWindow();
            stage.setMaximized(true);
            stage.setScene(new Scene(root, 1200, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    


/**************************** End of SQL functions *****************************************************/
}