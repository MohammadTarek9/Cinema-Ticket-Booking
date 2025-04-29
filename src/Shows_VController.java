//package org.asu.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
//import org.asu.DatabaseConnector;
//import org.asu.Show;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Shows_VController extends CRUD {

    @FXML
    private AnchorPane Pane;


    @FXML
    public void initialize() {
        loadShows();
        setupHyperlinkActions("Shows");
    }

    private void loadShows() {
        ObservableList<Show> allShows = FXCollections.observableArrayList();
        String query = "SELECT * FROM show";

        try (Connection conn = DatabaseConnector.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Show show = new Show(rs.getInt("showID"), rs.getInt("hall_no"), rs.getInt("movieID"), rs.getDate("show_date").toLocalDate(), rs.getTime("show_time").toLocalTime());
                ;
                allShows.add(show);
            }

            setupShowTable(allShows);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load shows: " + e.getMessage());
        }
    }

    private void setupShowTable(ObservableList<Show> allShows) {
        TableView<Show> showTable = new TableView<>();

        // Create columns
        TableColumn<Show, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("showID"));

        TableColumn<Show, Integer> hallCol = new TableColumn<>("Hall Number");
        hallCol.setCellValueFactory(new PropertyValueFactory<>("hall_no"));

        TableColumn<Show, Integer> movieCol = new TableColumn<>("Movie ID");
        movieCol.setCellValueFactory(new PropertyValueFactory<>("movieID"));

        TableColumn<Show, LocalDate> dateCol = new TableColumn<>("Show Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("show_date"));

        TableColumn<Show, LocalTime> timeCol = new TableColumn<>("Show Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("show_time"));

        TableColumn<Show, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(new Callback<TableColumn<Show, Void>, TableCell<Show, Void>>() {
            @Override
            public TableCell<Show, Void> call(final TableColumn<Show, Void> param) {
                return new TableCell<Show, Void>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");

                    {
                        // Style buttons (optional)
                        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                        // Set button actions
                        editBtn.setOnAction(event -> {
                            Show show = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/asu/Shows_E.fxml"));
                                Parent root = loader.load();

                                // Get controller and pass data
                                Shows_EContoller editController = loader.getController();
                                editController.setShowToEdit(show);

                                Scene scene = Pane.getScene();
                                scene.setRoot(root);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        // Delete functionality
                        deleteBtn.setOnAction(event -> {

                            Show show = getTableView().getItems().get(getIndex());
                            if (AlertHelper.showConfirm("Alert!", "Delete show?")) {
                                Show.deleteShow(show.getShowID());
                                loadShows();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
                        }
                    }
                };
            }
        });

        movieCol.setPrefWidth(100); // Preferred width
        // Add columns to table
        showTable.getColumns().addAll(idCol, hallCol, movieCol, dateCol, timeCol, actionCol);

        // Add table to center pane
        AnchorPane.setTopAnchor(showTable, 0.0);
        AnchorPane.setBottomAnchor(showTable, 0.0);
        AnchorPane.setLeftAnchor(showTable, 0.0);
        AnchorPane.setRightAnchor(showTable, 0.0);
        Pane.getChildren().add(showTable);
        showTable.setItems(allShows);
    }
}
