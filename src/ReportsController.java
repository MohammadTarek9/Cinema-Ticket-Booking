//package org.example;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

public class ReportsController {
    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private ComboBox<String> monthCombo;
    @FXML private ComboBox<String> yearCombo;
    @FXML private ComboBox<String> movieCombo;
    @FXML private ComboBox<String> cinemaCombo;
    @FXML private Button generateReportBtn;
    @FXML private VBox reportContainer;
    @FXML private Hyperlink returnLink;

    private Connection connection;

    public void initialize() {
        try {
            connection = DatabaseConnector.getConnection();
            // Setup years (current year and previous 5 years)
            int currentYear = Year.now().getValue();
            for (int i = currentYear; i >= currentYear - 5; i--) {
                yearCombo.getItems().add(String.valueOf(i));
            }

            // Load movies and cinemas for combo boxes
            loadMovies();
            loadCinemas();

            // Set up report type listener
            reportTypeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                updateInputVisibility(newVal);
            });

            generateReportBtn.setOnAction(e -> generateReport());

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not connect to database: " + e.getMessage());
        }
    }

    private void updateInputVisibility(String reportType) {
        // Reset visibility
        monthCombo.setVisible(false);
        yearCombo.setVisible(false);
        movieCombo.setVisible(false);
        cinemaCombo.setVisible(false);

        // Set visibility based on report type
        if (reportType != null) {
            switch (reportType) {
                case "Monthly Ticket Sales":
                case "Menu Item Sales":
                    monthCombo.setVisible(true);
                    yearCombo.setVisible(true);
                    break;
                case "Movie Performance":
                    movieCombo.setVisible(true);
                    monthCombo.setVisible(true);
                    yearCombo.setVisible(true);
                    break;
                case "Cinema Performance":
                    cinemaCombo.setVisible(true);
                    monthCombo.setVisible(true);
                    yearCombo.setVisible(true);
                    break;
                case "Popular Menu Items":
                case "Movie Comparisons":
                case "Revenue by Screen Type":
                    yearCombo.setVisible(true);
                    break;
            }
        }
    }

    private void loadMovies() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT title FROM movie")) {

            while (rs.next()) {
                movieCombo.getItems().add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCinemas() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT cinema_name FROM cinema")) {

            while (rs.next()) {
                cinemaCombo.getItems().add(rs.getString("cinema_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generateReport() {
        String reportType = reportTypeCombo.getValue();
        if (reportType == null) {
            showAlert("Error", "Please select a report type");
            return;
        }

        reportContainer.getChildren().clear();

        try {
            switch (reportType) {
                case "Monthly Ticket Sales":
                    generateMonthlyTicketSalesReport();
                    break;
                case "Movie Performance":
                    generateMoviePerformanceReport();
                    break;
                case "Cinema Performance":
                    generateCinemaPerformanceReport();
                    break;
                case "Menu Item Sales":
                    generateMenuItemSalesReport();
                    break;
                case "Popular Menu Items":
                    generatePopularMenuItemsReport();
                    break;
                case "Movie Comparisons":
                    generateMovieComparisonsReport();
                    break;
                case "Revenue by Screen Type":
                    generateRevenueByScreenTypeReport();
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Error generating report: " + e.getMessage());
        }
    }

    private void generateMonthlyTicketSalesReport() throws SQLException {
        int year = Integer.parseInt(yearCombo.getValue());
        int month = monthCombo.getSelectionModel().getSelectedIndex() + 1;

        try (CallableStatement cstmt = connection.prepareCall("{call sp_MonthlyTicketSalesReport(?, ?)}")) {
            cstmt.setInt(1, year);
            cstmt.setInt(2, month);

            ResultSet rs = cstmt.executeQuery();
            // Create chart
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
            chart.setTitle("Daily Ticket Sales - " + monthCombo.getValue() + " " + year);

            XYChart.Series<String, Number> ticketSeries = new XYChart.Series<>();
            ticketSeries.setName("Tickets Sold");

            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Revenue ($)");

            int totalTickets = 0;
            double totalRevenue = 0;

            while (rs.next()) {
                int day = rs.getInt("day");
                int tickets = rs.getInt("tickets");
                double revenue = rs.getDouble("revenue");

                ticketSeries.getData().add(new XYChart.Data<>(String.valueOf(day), tickets));
                revenueSeries.getData().add(new XYChart.Data<>(String.valueOf(day), revenue));

                totalTickets += tickets;
                totalRevenue += revenue;
            }

            chart.getData().addAll(ticketSeries, revenueSeries);

            // Create summary card
            VBox summaryCard = new VBox(10);
            summaryCard.getStyleClass().add("report-card");

            Text title = new Text("Summary for " + monthCombo.getValue() + " " + year);
            title.getStyleClass().add("report-title");

            Label ticketsLabel = new Label("Total Tickets Sold: " + totalTickets);
            ticketsLabel.getStyleClass().add("report-stat");

            Label revenueLabel = new Label(String.format("Total Revenue: $%.2f", totalRevenue));
            revenueLabel.getStyleClass().add("report-stat");

            summaryCard.getChildren().addAll(title, ticketsLabel, revenueLabel);

            reportContainer.getChildren().addAll(summaryCard, chart);
        }
    }

    private void generateMoviePerformanceReport() throws SQLException {
        String movieTitle = movieCombo.getValue();
        int year = Integer.parseInt(yearCombo.getValue());
        int month = monthCombo.getSelectionModel().getSelectedIndex() + 1;

        try (CallableStatement cstmt = connection.prepareCall("{call sp_MoviePerformanceReport(?, ?, ?)}")) {
            cstmt.setString(1, movieTitle);
            cstmt.setInt(2, year);
            cstmt.setInt(3, month);

            ResultSet rs = cstmt.executeQuery();

            // Create chart
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle(movieTitle + " Performance - " + monthCombo.getValue() + " " + year);

            XYChart.Series<String, Number> ticketSeries = new XYChart.Series<>();
            ticketSeries.setName("Tickets Sold");

            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Revenue ($)");

            int totalTickets = 0;
            double totalRevenue = 0;

            while (rs.next()) {
                int day = rs.getInt("day");
                int tickets = rs.getInt("tickets");
                double revenue = rs.getDouble("revenue");

                ticketSeries.getData().add(new XYChart.Data<>(String.valueOf(day), tickets));
                revenueSeries.getData().add(new XYChart.Data<>(String.valueOf(day), revenue));

                totalTickets += tickets;
                totalRevenue += revenue;
            }

            chart.getData().addAll(ticketSeries, revenueSeries);

            // Create summary card
            VBox summaryCard = new VBox(10);
            summaryCard.getStyleClass().add("report-card");

            Text title = new Text(movieTitle + " Summary");
            title.getStyleClass().add("report-title");

            Label ticketsLabel = new Label("Total Tickets Sold: " + totalTickets);
            ticketsLabel.getStyleClass().add("report-stat");

            Label revenueLabel = new Label(String.format("Total Revenue: $%.2f", totalRevenue));
            revenueLabel.getStyleClass().add("report-stat");

            double avgRevenuePerTicket = totalTickets > 0 ? totalRevenue / totalTickets : 0;
            Label avgLabel = new Label(String.format("Average Revenue per Ticket: $%.2f", avgRevenuePerTicket));
            avgLabel.getStyleClass().add("report-stat");

            summaryCard.getChildren().addAll(title, ticketsLabel, revenueLabel, avgLabel);

            reportContainer.getChildren().addAll(summaryCard, chart);
        }
    }

    private void generateCinemaPerformanceReport() throws SQLException {
        String cinemaName = cinemaCombo.getValue();
        int year = Integer.parseInt(yearCombo.getValue());
        int month = monthCombo.getSelectionModel().getSelectedIndex() + 1;

        try (CallableStatement cstmt = connection.prepareCall("{call sp_CinemaPerformanceReport(?, ?, ?)}")) {
            cstmt.setString(1, cinemaName);
            cstmt.setInt(2, year);
            cstmt.setInt(3, month);

            ResultSet rs = cstmt.executeQuery();
            // Create chart
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);
            chart.setTitle(cinemaName + " Performance - " + monthCombo.getValue() + " " + year);

            XYChart.Series<String, Number> ticketSeries = new XYChart.Series<>();
            ticketSeries.setName("Tickets Sold");

            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Revenue ($)");

            int totalTickets = 0;
            double totalRevenue = 0;

            while (rs.next()) {
                int day = rs.getInt("day");
                int tickets = rs.getInt("tickets");
                double revenue = rs.getDouble("revenue");

                ticketSeries.getData().add(new XYChart.Data<>(String.valueOf(day), tickets));
                revenueSeries.getData().add(new XYChart.Data<>(String.valueOf(day), revenue));

                totalTickets += tickets;
                totalRevenue += revenue;
            }

            chart.getData().addAll(ticketSeries, revenueSeries);

            // Create summary card
            VBox summaryCard = new VBox(10);
            summaryCard.getStyleClass().add("report-card");

            Text title = new Text(cinemaName + " Summary");
            title.getStyleClass().add("report-title");

            Label ticketsLabel = new Label("Total Tickets Sold: " + totalTickets);
            ticketsLabel.getStyleClass().add("report-stat");

            Label revenueLabel = new Label(String.format("Total Revenue: $%.2f", totalRevenue));
            revenueLabel.getStyleClass().add("report-stat");

            double seatUtilization = getSeatUtilization(cinemaName, year, month);
            Label utilizationLabel = new Label(String.format("Seat Utilization: %.1f%%", seatUtilization * 100));
            utilizationLabel.getStyleClass().add("report-stat");

            summaryCard.getChildren().addAll(title, ticketsLabel, revenueLabel, utilizationLabel);

            reportContainer.getChildren().addAll(summaryCard, chart);
        }
    }

    private double getSeatUtilization(String cinemaName, int year, int month) throws SQLException {
        try (CallableStatement cstmt = connection.prepareCall("{call sp_GetSeatUtilization(?, ?, ?)}")) {
            cstmt.setString(1, cinemaName);
            cstmt.setInt(2, year);
            cstmt.setInt(3, month);

            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                int totalSeats = rs.getInt("total_seats");
                int occupiedSeats = rs.getInt("occupied_seats");
                return totalSeats > 0 ? (double) occupiedSeats / totalSeats : 0;
            }
        }
        return 0;
    }

    private void generateMenuItemSalesReport() throws SQLException {
        int year = Integer.parseInt(yearCombo.getValue());
        int month = monthCombo.getSelectionModel().getSelectedIndex() + 1;

        try (CallableStatement cstmt = connection.prepareCall("{call sp_MenuItemSalesReport(?, ?)}")) {
            cstmt.setInt(1, year);
            cstmt.setInt(2, month);

            ResultSet rs = cstmt.executeQuery();
            // Create chart
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
            chart.setTitle("Menu Item Sales - " + monthCombo.getValue() + " " + year);

            XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
            salesSeries.setName("Revenue ($)");

            double totalRevenue = 0;

            while (rs.next()) {
                String itemName = rs.getString("item_name");
                double revenue = rs.getDouble("revenue");

                salesSeries.getData().add(new XYChart.Data<>(itemName, revenue));
                totalRevenue += revenue;
            }

            chart.getData().add(salesSeries);

            // Create summary card
            VBox summaryCard = new VBox(10);
            summaryCard.getStyleClass().add("report-card");

            Text title = new Text("Menu Item Sales Summary");
            title.getStyleClass().add("report-title");

            Label revenueLabel = new Label(String.format("Total Revenue: $%.2f", totalRevenue));
            revenueLabel.getStyleClass().add("report-stat");

            summaryCard.getChildren().addAll(title, revenueLabel);

            reportContainer.getChildren().addAll(summaryCard, chart);
        }
    }

    private void generatePopularMenuItemsReport() throws SQLException {
        int year = Integer.parseInt(yearCombo.getValue());

        try (CallableStatement cstmt = connection.prepareCall("{call sp_PopularMenuItemsReport(?)}")) {
            cstmt.setInt(1, year);

            ResultSet rs = cstmt.executeQuery();

            // Create chart
            PieChart chart = new PieChart();
            chart.setTitle("Top 5 Popular Menu Items - " + year);

            while (rs.next()) {
                String itemName = rs.getString("item_name");
                int count = rs.getInt("purchase_count");

                chart.getData().add(new PieChart.Data(itemName + " (" + count + ")", count));
            }

            // Create summary card
            VBox summaryCard = new VBox(10);
            summaryCard.getStyleClass().add("report-card");

            Text title = new Text("Popular Items Summary - " + year);
            title.getStyleClass().add("report-title");

            summaryCard.getChildren().add(title);

            reportContainer.getChildren().addAll(summaryCard, chart);
        }
    }

    private void generateMovieComparisonsReport() throws SQLException {
        int year = Integer.parseInt(yearCombo.getValue());

        try (CallableStatement cstmt = connection.prepareCall("{call sp_MovieComparisonsReport(?)}")) {
            cstmt.setInt(1, year);

            ResultSet rs = cstmt.executeQuery();
            // Create chart
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
            chart.setTitle("Top 5 Movies by Revenue - " + year);

            XYChart.Series<String, Number> ticketSeries = new XYChart.Series<>();
            ticketSeries.setName("Tickets Sold");

            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Revenue ($)");

            while (rs.next()) {
                String title = rs.getString("title");
                int tickets = rs.getInt("tickets");
                double revenue = rs.getDouble("revenue");

                ticketSeries.getData().add(new XYChart.Data<>(title, tickets));
                revenueSeries.getData().add(new XYChart.Data<>(title, revenue));
            }

            chart.getData().addAll(ticketSeries, revenueSeries);

            reportContainer.getChildren().add(chart);
        }
    }

    private void generateRevenueByScreenTypeReport() throws SQLException {
        int year = Integer.parseInt(yearCombo.getValue());

        try (CallableStatement cstmt = connection.prepareCall("{call sp_RevenueByScreenTypeReport(?)}")) {
            cstmt.setInt(1, year);

            ResultSet rs = cstmt.executeQuery();
            // Create chart
            PieChart chart = new PieChart();
            chart.setTitle("Revenue by Screen Type - " + year);

            while (rs.next()) {
                String screenType = rs.getString("screen_type");
                double revenue = rs.getDouble("revenue");

                chart.getData().add(new PieChart.Data(screenType + " ($" + String.format("%.2f", revenue) + ")", revenue));
            }

            reportContainer.getChildren().add(chart);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleReturnLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) returnLink.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 700);

            // Set the new scene on the stage
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}