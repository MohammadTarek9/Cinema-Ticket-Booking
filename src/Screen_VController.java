//package org.example;

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

import java.io.IOException;
import java.sql.*;
public class Screen_VController extends CRUD implements AlertHelper{


    @FXML
    private AnchorPane Pane;


    @FXML
    public void initialize() {
        loadScreens();
        setupHyperlinkActions("Screen");
    }

    //sql
    private void loadScreens() {
        ObservableList<Screen> allScreens = FXCollections.observableArrayList();
        String sql = "{ call GetAllScreens() }";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                Screen screen = createScreenFromResultSet(rs);
                allScreens.add(screen);
            }

            setupScreenTable(allScreens);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load screens: " + e.getMessage());
        }
    }


    private Screen createScreenFromResultSet(ResultSet rs) throws SQLException {
        String screenType = rs.getString("screen_type");
        double price = rs.getDouble("price");
        String resolution = rs.getString("resolution");

        return new Screen(screenType, price, resolution);
    }
    private void setupScreenTable(ObservableList<Screen> allScreens) {
        TableView<Screen> screenTable = new TableView<>();

        TableColumn<Screen, String> typeCol = new TableColumn<>("Screen Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("screenType"));

        TableColumn<Screen, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Screen, String> resolutionCol = new TableColumn<>("Resolution");
        resolutionCol.setCellValueFactory(new PropertyValueFactory<>("resolution"));


        TableColumn<Screen, Void> actionCol = new TableColumn<>("Actions");

        actionCol.setCellFactory(new Callback<TableColumn<Screen, Void>, TableCell<Screen, Void>>() {
            @Override
            public TableCell<Screen, Void> call(final TableColumn<Screen, Void> param) {
                return new TableCell<Screen, Void>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");

                    {

                        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");


                        editBtn.setOnAction(event -> {
                            Screen screen = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("Screen_E.fxml"));
                                Parent root = loader.load();

                                // Get controller and pass data
                                Screen_EController editController = loader.getController();
                                editController.setScreenToEdit(screen);

                                Scene scene = Pane.getScene();
                                scene.setRoot(root);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        deleteBtn.setOnAction(event -> {
                            Screen screen = getTableView().getItems().get(getIndex());
                            if (AlertHelper.showConfirm("Alert!", "Delete screen " + screen.getScreenType() + "?")) {
                                Screen.deleteScreen(screen.getScreenType());
                                loadScreens();
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

        
        typeCol.setPrefWidth(100);
        priceCol.setPrefWidth(100);
        resolutionCol.setPrefWidth(100);

        // Add columns to table
        screenTable.getColumns().addAll(typeCol, priceCol, resolutionCol, actionCol);

        // Add table to center pane
        AnchorPane.setTopAnchor(screenTable, 0.0);
        AnchorPane.setBottomAnchor(screenTable, 0.0);
        AnchorPane.setLeftAnchor(screenTable, 0.0);
        AnchorPane.setRightAnchor(screenTable, 0.0);
        Pane.getChildren().clear(); // Clear old content
        Pane.getChildren().add(screenTable);
        screenTable.setItems(allScreens);
    }
}