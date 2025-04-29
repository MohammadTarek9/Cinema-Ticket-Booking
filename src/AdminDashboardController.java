//package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboardController {
    @FXML private Text cinemaCountText;
    @FXML private Text hallCountText;
    @FXML private Text nowShowingCountText;
    @FXML private Text ticketsSoldText;
    @FXML private TableView<Map<String, String>> activityTable;
    @FXML private TableColumn<Map<String, String>, String> actionColumn;
    @FXML private TableColumn<Map<String, String>, String> detailsColumn;
    @FXML private TableColumn<Map<String, String>, String> timestampColumn;


    @FXML
    public void initialize() {
        actionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().get("Action"))
        );
        detailsColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().get("Details"))
        );
        timestampColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().get("Timestamp"))
        );

        loadStatistics();
        loadRecentActivity();
    }

    private void loadStatistics() {
        // Cinema Count
        executeStatQuery("sp_GetCinemaCount", "cinema_count", cinemaCountText);

        // Hall Count
        executeStatQuery("sp_GetHallCount", "hall_count", hallCountText);

        // Now Showing Count
        executeStatQuery("sp_GetNowShowingCount", "now_showing_count", nowShowingCountText);

        // Tickets Sold Count
        executeStatQuery("sp_GetTicketsSoldCount", "tickets_sold", ticketsSoldText);
    }

    private void executeStatQuery(String procedureName, String columnName, Text targetText) {
        String sql = "{call " + procedureName + "}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                targetText.setText(String.valueOf(rs.getInt(columnName)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            targetText.setText("Error");
        }
    }

    private void loadRecentActivity() {
        String sql = "{call sp_GetRecentActivity()}";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            activityTable.getItems().clear();

            while (rs.next()) {
                System.out.println("there is data");
                Map<String, String> row = new HashMap<>();
                row.put("Action", rs.getString("action_type"));
                row.put("Details", rs.getString("details"));
                row.put("Timestamp", rs.getTimestamp("timestamp").toString());
                activityTable.getItems().add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openCinemaManagement() {
        loadManagementScreen("Cinemas_V.fxml", "Cinema Management");
    }

    @FXML
    private void openHallManagement() {
        loadManagementScreen("Halls_V.fxml", "Hall Management");
    }

    @FXML
    private void openSeatManagement() {
        loadManagementScreen("Seat_V.fxml", "Seat Management");
    }

    @FXML
    private void openScreenManagement() {
        loadManagementScreen("Screen_V.fxml", "Screen Management");
    }

    @FXML
    private void openMoviesManagement() {
        loadManagementScreen("Movies_V.fxml", "Movies Management");
    }

    private void loadManagementScreen(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) cinemaCountText.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("dark-theme-admin.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load admin page: " + e.getMessage());
        }
    }

    @FXML
    private void goBackToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
            Stage stage = (Stage) cinemaCountText.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Cinema Booking System");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to home page");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}