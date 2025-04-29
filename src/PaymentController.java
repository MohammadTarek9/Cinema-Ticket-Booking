import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaymentController {
    
    @FXML private Label movieTitleLabel;
    @FXML private Label showtimeLabel;
    @FXML private Label seatsLabel;
    @FXML private Label itemsLabel;
    @FXML private Label totalLabel; 
    @FXML private TextField firstNameField;
    @FXML private TextField middleInitialField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField ageField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private VBox paymentDetailsBox;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryDateField;
    @FXML private TextField cvvField;
    
    private Movie currentMovie;
    private int showID;
    private List<String> selectedSeats = new ArrayList<>();
    private List<OrderItem> orderedItems = new ArrayList<>();
    private double totalPrice;
    private List<Integer> selectedMenuItemIds = new ArrayList<>();
    
    @FXML
public void initialize() {
    setupInputValidation();
    setupPaymentMethodListener();
    setupComboBox();
}

private void setupInputValidation() {
    // Allow only numbers for age
    ageField.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.matches("\\d*")) {
            ageField.setText(newVal.replaceAll("[^\\d]", ""));
        }
    });

    // Allow only letters for first, middle, and last name
    firstNameField.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.matches("[a-zA-Z]*")) {
            firstNameField.setText(newVal.replaceAll("[^a-zA-Z]", ""));
        }
    });

    Tooltip tooltip = new Tooltip("Enter a single letter (A-Z)");
    middleInitialField.setTooltip(tooltip);

    middleInitialField.textProperty().addListener((obs, oldVal, newVal) -> {
    // Limit to one character
    if (newVal.length() > 1) {
        middleInitialField.setText(newVal.substring(0, 1));
    }
    
    // Only allow letters
    if (!newVal.matches("[a-zA-Z]?")) {
        middleInitialField.setText(newVal.replaceAll("[^a-zA-Z]", ""));
    }
    
    // Visual feedback
    if (newVal.length() == 1 && newVal.matches("[a-zA-Z]")) {
        middleInitialField.setStyle("-fx-border-color: #4CAF50;"); // Green for valid
    } else if (!newVal.isEmpty()) {
        middleInitialField.setStyle("-fx-border-color: #F44336;"); // Red for invalid
    } else {
        middleInitialField.setStyle(""); // Default
    }
});

    lastNameField.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.matches("[a-zA-Z]*")) {
            lastNameField.setText(newVal.replaceAll("[^a-zA-Z]", ""));
        }
    });
}

private void setupPaymentMethodListener() {
    paymentMethodCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        boolean showDetails = newVal != null &&
                (newVal.contains("Visa") || newVal.contains("Master Card"));
        paymentDetailsBox.setVisible(showDetails);
    });
}

private void setupComboBox() {
    paymentMethodCombo.getItems().addAll(
        "Visa Credit Card",
        "Visa Debit Card",
        "Master Card"     
    );
}
public void setBookingData(Movie movie, int showID, List<String> seats, 
                         List<OrderItem> items, double total,
                         List<Integer> selectedMenuItemIds) {
    this.currentMovie = movie;
    this.showID = showID;
    this.selectedSeats = seats != null ? new ArrayList<>(seats) : new ArrayList<>();
    this.orderedItems = items != null ? new ArrayList<>(items) : new ArrayList<>();
    this.totalPrice = total;
    this.selectedMenuItemIds = selectedMenuItemIds != null ? new ArrayList<>(selectedMenuItemIds) : new ArrayList<>();
    
    updateUI();
    
}

private void updateUI() {
    /// Movie title
    movieTitleLabel.setText(String.format("Movie: %s", 
    currentMovie != null ? currentMovie.getTitle() : "No movie selected"));

    // Showtime info
    showtimeLabel.setText(getFormattedShowtime());

    // Seats display
    String seatsText = selectedSeats != null && !selectedSeats.isEmpty()
        ? "Seat(s): " + String.join(", ", selectedSeats)
        : "Seat(s): No seats selected";
    seatsLabel.setText(seatsText);

    // Ordered Items (concessions/snacks)
    String itemsText;
    if (orderedItems == null || orderedItems.isEmpty()) {
        itemsText = "Menu Item(s): None";
    } else {
        itemsText = "Menu Item(s):\n" + orderedItems.stream()
            .filter(Objects::nonNull)
            .map(item -> item.getMenuItem() != null
                ? String.format("%s (x%d)", item.getMenuItem().getItemName(), item.getQuantity())
                : "Unknown Item")
            .collect(Collectors.joining("\n"));
    }
    itemsLabel.setText(itemsText);

    // Total price
    totalLabel.setText(String.format("Total: $%.2f", totalPrice));
}


private String getFormattedShowtime() {
    return showID > 0 ? "Showtime ID: " + showID : "No showtime selected";
}

@FXML
private void confirmPayment() {
    if (!validateInputs()) {
        return;
    }
    
    // Validate payment details if card payment
    String paymentMethod = paymentMethodCombo.getValue();
    if (paymentMethod != null && (paymentMethod.contains("Card") || paymentMethod.contains("Credit"))) {
        if (!validateCardDetails()) {
            return;
        }
    }
    
    // Process payment
    Connection conn = null;
    try {
        conn = DatabaseConnector.getConnection();
        //conn.setAutoCommit(false);
        
        // 1. Create customer record
        String customerPhone = insertCustomer(conn);
        System.out.println("Customer phone in confirm: " + customerPhone);
        
        // 2. Create payment record
        int paymentID = insertPayment(conn, customerPhone);
        System.out.println("Payment ID in confirm: " + paymentID);
        
        // 3. Get hall number for this show
        int hallNo = getHallNumber(conn, showID);
        System.out.println("Hall number in confirm: " + hallNo);
        
        // 4. Create tickets and update seat status
        createTickets(conn, paymentID, showID, hallNo);
        updateSeatStatus(conn, hallNo, selectedSeats);
        
        // 5. Add ordered items if any
        if (!selectedMenuItemIds.isEmpty()) {
            addOrderItems(conn, paymentID, selectedMenuItemIds);
        }
        
        //conn.commit();
        
        // Show success alert with booking details
        showSuccessAlert(paymentID);
        
        // Return to home screen after successful payment
        returnToHome();
        
    } catch (SQLException e) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        e.printStackTrace();
        AlertHelper.showAlert(Alert.AlertType.ERROR, 
            "Payment Failed", "Transaction could not be completed: " + e.getMessage());
    } finally {
        try {
            if (conn != null) {
                //conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

private void showSuccessAlert(int paymentID) {
    StringBuilder message = new StringBuilder();
    message.append("Payment Successful!\n\n");
    message.append("Booking Reference: ").append(paymentID).append("\n");
    message.append("Movie: ").append(currentMovie.getTitle()).append("\n");
    message.append("Showtime: ").append(getFormattedShowtime()).append("\n");
    message.append("Seats: ").append(String.join(", ", selectedSeats)).append("\n");
    
    if (!orderedItems.isEmpty()) {
        message.append("\nItems:\n");
        orderedItems.forEach(item -> 
            message.append("- ")
                  .append(item.getMenuItem().getItemName())
                  .append(" (x")
                  .append(item.getQuantity())
                  .append(")\n"));
    }
    
    message.append("\nTotal: $").append(String.format("%.2f", totalPrice));
    
    AlertHelper.showAlert(Alert.AlertType.INFORMATION, 
        "Booking Confirmed", 
        message.toString());
}

private boolean validateCardDetails() {
    // Validate card number has exactly 16 digits
    String cardNumber = cardNumberField.getText().replaceAll("\\s+", "");
    if (cardNumber.length() != 16 || !cardNumber.matches("\\d{16}")) {
        AlertHelper.showAlert(Alert.AlertType.ERROR,
            "Invalid Card",
            "Please enter a 16-digit card number");
        cardNumberField.requestFocus();
        return false;
    }
    
    // Validate expiry date (MM/YY format and not expired)
if (!expiryDateField.getText().matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
    AlertHelper.showAlert(Alert.AlertType.ERROR,
        "Invalid Date Format",
        "Please enter expiry date in MM/YY format");
    expiryDateField.requestFocus();
    return false;
} else if (isCardExpired(expiryDateField.getText())) {
    AlertHelper.showAlert(Alert.AlertType.ERROR,
        "Card Expired",
        "This card has already expired");
    expiryDateField.requestFocus();
    return false;
}
    
    // Validate CVV has exactly 3 digits
    if (!cvvField.getText().matches("\\d{3}")) {
        AlertHelper.showAlert(Alert.AlertType.ERROR,
            "Invalid CVV",
            "Please enter a 3-digit security code");
        cvvField.requestFocus();
        return false;
    }
    
    return true;
}

private boolean isCardExpired(String mmYY) {
    try {
        String[] parts = mmYY.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]); // Convert YY to YYYY
        
        // Get current year and month
        YearMonth current = YearMonth.now();
        YearMonth expiry = YearMonth.of(year, month);
        
        return expiry.isBefore(current);
    } catch (Exception e) {
        return true; // If any parsing error occurs, consider it expired
    }
}

    private boolean validateInputs() {
        // Validate customer info
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
            phoneField.getText().isEmpty() || ageField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, 
                "Validation Error", "Please fill all required fields");
            return false;
        }
        
        // Validate payment method
        if (paymentMethodCombo.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR,
                "Validation Error", "Please select a payment method");
            return false;
        }
        
        // Validate card details if needed
        String method = paymentMethodCombo.getValue();
        if ((method.contains("Visa") || method.contains("Master Card")) &&
            (cardNumberField.getText().isEmpty() || 
             expiryDateField.getText().isEmpty() || 
             cvvField.getText().isEmpty())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR,
                "Validation Error", "Please enter all payment details");
            return false;
        }
        
        return true;
    }

    private String insertCustomer(Connection conn) {
    String phoneNum = phoneField.getText();
    String sql = "{call sp_InsertCustomer(?, ?, ?, ?, ?, ?)}";

    try (CallableStatement stmt = conn.prepareCall(sql)) {
        stmt.setString(1, phoneNum);
        stmt.setString(2, firstNameField.getText());
        stmt.setString(3, middleInitialField.getText());
        stmt.setString(4, lastNameField.getText());
        stmt.setInt(5, Integer.parseInt(ageField.getText()));
        stmt.setString(6, emailField.getText());

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String returnedPhone = rs.getString("phoneNum");
                System.out.println("Customer processed: " + returnedPhone);
                return returnedPhone;
            } else {
                throw new SQLException("No phone number returned by stored procedure.");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        AlertHelper.showAlert(Alert.AlertType.ERROR, 
            "Database Error", "Could not insert or retrieve customer: " + e.getMessage());
        return null;
    }
}


private int insertPayment(Connection conn, String customerPhone) throws SQLException {
    String sql = "{CALL sp_InsertPayment(?, ?, ?, ?)}"; 

    try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        pstmt.setDouble(1, totalPrice); 
        pstmt.setString(2, paymentMethodCombo.getValue()); 
        pstmt.setString(3, customerPhone);
        pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected == 0) {
            throw new SQLException("Inserting payment failed, no rows affected.");
        }

        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1); 
            } else {
                throw new SQLException("Inserting payment failed, no ID obtained.");
            }
        }
    }
}


private int getHallNumber(Connection conn, int showID) throws SQLException {
    String sql = "{call sp_GetHallNumberByShowID(?, ?)}";

    try (CallableStatement stmt = conn.prepareCall(sql)) {
        stmt.setInt(1, showID);
        stmt.registerOutParameter(2, Types.INTEGER);

        stmt.execute();

        int hallNumber = stmt.getInt(2);
        System.out.println("Hall number: " + hallNumber);
        return hallNumber;
    }
}


private void createTickets(Connection conn, int paymentID, int showID, int hallNo) throws SQLException {
    String sql = "{call sp_CreateTicket(?, ?, ?, ?)}";

    try (CallableStatement stmt = conn.prepareCall(sql)) {
        for (String seat : selectedSeats) {
            int seatNo = Integer.parseInt(seat.substring(seat.indexOf("S") + 1));
            System.out.println("Inserting ticket for seat: " + seatNo);

            stmt.setInt(1, showID);
            stmt.setInt(2, hallNo);
            stmt.setInt(3, seatNo);
            stmt.setInt(4, paymentID);

            stmt.execute();
        }
    }
}


private void updateSeatStatus(Connection conn, int hallNo, List<String> selectedSeats) throws SQLException {
    String sql = "{call sp_UpdateSeatStatus(?, ?)}";

    try (CallableStatement stmt = conn.prepareCall(sql)) {
        for (String seat : selectedSeats) {
            int seatNo = Integer.parseInt(seat.substring(seat.indexOf("S") + 1));

            stmt.setInt(1, hallNo);
            stmt.setInt(2, seatNo);

            stmt.execute();
        }
    }
}


private void addOrderItems(Connection conn, int paymentID, List<Integer> itemIds) throws SQLException {
    String sql = "{call sp_AddOrderItem(?, ?)}";

    try (CallableStatement stmt = conn.prepareCall(sql)) {
        List<Integer> remainingItems = new ArrayList<>(itemIds);

        while (!remainingItems.isEmpty()) {
            Integer itemId = remainingItems.remove(0);
            try {
                stmt.setInt(1, paymentID);
                stmt.setInt(2, itemId);
                stmt.execute();

                itemIds.remove(itemId); // optional if itemIds needs to reflect only uninserted

            } catch (SQLException e) {
                System.err.println("Failed to insert item " + itemId + ": " + e.getMessage());
            }
        }
    }
}


    private void showConfirmation() {
        AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", 
            "Payment confirmed!\n\n" +
            "Movie: " + currentMovie.getTitle() + "\n" +
            "Seats: " + String.join(", ", selectedSeats) + "\n" +
            "Total: $" + String.format("%.2f", totalPrice));
        
        // Return to home screen
        try {
            Parent root = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBackToBooking() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BookingPage.fxml"));
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void returnToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR,
                "Navigation Error",
                "Could not return to home screen: " + e.getMessage());
        }
    }
}