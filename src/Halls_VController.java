//package org.example;
import java.sql.*;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

public class Halls_VController extends CRUD implements AlertHelper {
    @FXML
    private AnchorPane Pane;

    @FXML
    public void initialize() {
        loadHalls();
        setupHyperlinkActions("Halls");
    }

    //sql
    private void loadHalls() {
        ObservableList<Hall> allHalls = FXCollections.observableArrayList();
        String sql = "{ call GetAllHalls() }";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                Hall hall = createHallFromResultSet(rs);
                allHalls.add(hall);
            }

            setupHallTable(allHalls);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load halls: " + e.getMessage());
        }
    }

    private Hall createHallFromResultSet(ResultSet rs) throws SQLException {
        Hall hall = new Hall();
        hall.setHall_no(rs.getInt("hall_no"));
        hall.setSound_sys(rs.getString("sound_sys"));
        hall.setScreen_type(rs.getString("screen_type"));
        hall.setNo_of_seats(rs.getInt("no_of_seats"));
        hall.setCinema(Cinema.getCinemaByID(rs.getInt("cinemaID")));
        return hall;
    }

    private void setupHallTable(ObservableList<Hall> allHalls) {
        TableView<Hall> hallTable = new TableView<>();

        // Create columns
        TableColumn<Hall, Integer> hallNoCol = new TableColumn<>("Hall No");
        hallNoCol.setCellValueFactory(new PropertyValueFactory<>("hall_no"));

        TableColumn<Hall, String> soundSysCol = new TableColumn<>("Sound System");
        soundSysCol.setCellValueFactory(new PropertyValueFactory<>("sound_sys"));

        TableColumn<Hall, String> screenTypeCol = new TableColumn<>("Screen Type");
        screenTypeCol.setCellValueFactory(new PropertyValueFactory<>("screen_type"));

        TableColumn<Hall, Integer> noOfSeatsCol = new TableColumn<>("No. of Seats");
        noOfSeatsCol.setCellValueFactory(new PropertyValueFactory<>("no_of_seats"));

        TableColumn<Hall, String> cinemaCol = new TableColumn<>("Cinema");
        cinemaCol.setCellValueFactory(cellData -> {
            Cinema cinema = cellData.getValue().getCinema();
            return new javafx.beans.property.SimpleStringProperty(
            cinema != null ? cinema.getCinemaName() : "Unknown");
        });

        TableColumn<Hall, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(new Callback<TableColumn<Hall, Void>, TableCell<Hall, Void>>() {
            @Override
            public TableCell<Hall, Void> call(final TableColumn<Hall, Void> param) {
                return new TableCell<Hall, Void>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");

                    {
                        // Style buttons (optional)
                        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                        // Set button actions
                        editBtn.setOnAction(event -> {
                            Hall hall = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("Halls_E.fxml"));
                                Parent root = loader.load();

                                // Get controller and pass data
                                Halls_EController editController = loader.getController();
                                editController.setHallToEdit(hall);

                                Scene scene = Pane.getScene();
                                scene.setRoot(root);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        // Delete functionality
                        deleteBtn.setOnAction(event -> {
                            Hall hall = getTableView().getItems().get(getIndex());
                            if (AlertHelper.showConfirm("Alert!", "Delete Hall No " + hall.getHall_no() + "?")) {
                                String res = Hall.deleteHall(hall.getHall_no());
                                if(res.equals("Success")){
                                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Hall Deleted.");
                                    loadHalls();
                                } else {
                                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Failed", res);
                                }

                            }
                            else {
                                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Deletion cancelled.");
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

        // Add columns to table
        hallTable.getColumns().addAll(hallNoCol, soundSysCol, screenTypeCol, noOfSeatsCol, cinemaCol, actionCol);

        // Add table to center pane
        AnchorPane.setTopAnchor(hallTable, 0.0);
        AnchorPane.setBottomAnchor(hallTable, 0.0);
        AnchorPane.setLeftAnchor(hallTable, 0.0);
        AnchorPane.setRightAnchor(hallTable, 0.0);
        Pane.getChildren().add(hallTable);
        hallTable.setItems(allHalls);
    }
}
