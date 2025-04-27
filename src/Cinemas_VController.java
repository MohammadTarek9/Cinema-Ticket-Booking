import java.sql.Statement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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

public class Cinemas_VController extends CRUD implements AlertHelper {
     @FXML
    private AnchorPane Pane;

    @FXML
    public void initialize() {
        loadCinemas();
        setupHyperlinkActions("Cinemas");
    }

    private void loadCinemas() {
        ObservableList<Cinema> allCinemas = FXCollections.observableArrayList();
        String query = "SELECT * FROM cinema";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Cinema cinema = createCinemaFromResultSet(rs);
                allCinemas.add(cinema);
            }

            setupCinemaTable(allCinemas);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load cinemas: " + e.getMessage());
        }
    }

    private Cinema createCinemaFromResultSet(ResultSet rs) throws SQLException {
        Cinema cinema = new Cinema();
        cinema.setCinemaID(rs.getInt("cinemaID"));
        cinema.setCinemaName(rs.getString("cinema_name"));
        cinema.setContact_no(rs.getString("contact_no"));
        cinema.setOpening_hours(rs.getString("opening_hours"));
        cinema.setClosing_hours(rs.getString("closing_hours"));
        cinema.setStreet(rs.getString("street"));
        cinema.setCity(rs.getString("city"));
        cinema.setDistrict(rs.getString("district"));
        return cinema;
    }

    private void setupCinemaTable(ObservableList<Cinema> allCinemas) {
        TableView<Cinema> cinemaTable = new TableView<>();

        // Create columns
        TableColumn<Cinema, Integer> cinemaIDCol = new TableColumn<>("Cinema ID");
        cinemaIDCol.setCellValueFactory(new PropertyValueFactory<>("cinemaID"));

        TableColumn<Cinema, String> cinemaNameCol = new TableColumn<>("Cinema Name");
        cinemaNameCol.setCellValueFactory(new PropertyValueFactory<>("cinemaName"));

        TableColumn<Cinema, String> contactNoCol = new TableColumn<>("Contact No");
        contactNoCol.setCellValueFactory(new PropertyValueFactory<>("contact_no"));

        TableColumn<Cinema, String> openingHoursCol = new TableColumn<>("Opening Hours");
        openingHoursCol.setCellValueFactory(new PropertyValueFactory<>("opening_hours"));

        TableColumn<Cinema, String> closingHoursCol = new TableColumn<>("Closing Hours");
        closingHoursCol.setCellValueFactory(new PropertyValueFactory<>("closing_hours"));

        TableColumn<Cinema, String> streetCol = new TableColumn<>("Street");
        streetCol.setCellValueFactory(new PropertyValueFactory<>("street"));

        TableColumn<Cinema, String> cityCol = new TableColumn<>("City");
        cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<Cinema, String> districtCol = new TableColumn<>("District");
        districtCol.setCellValueFactory(new PropertyValueFactory<>("district"));

        TableColumn<Cinema, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(new Callback<TableColumn<Cinema, Void>, TableCell<Cinema, Void>>() {
            @Override
            public TableCell<Cinema, Void> call(final TableColumn<Cinema, Void> param) {
                return new TableCell<Cinema, Void>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");

                    {
                        // Style buttons (optional)
                        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                        // Set button actions
                        editBtn.setOnAction(event -> {
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("Cinemas_E.fxml"));
                                Parent root = loader.load();

                                // Get controller and pass data
                                Cinemas_EController editController = loader.getController();
                                editController.setCinemaToEdit(cinema);

                                Scene scene = Pane.getScene();
                                scene.setRoot(root);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        // Delete functionality
                        deleteBtn.setOnAction(event -> {
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            if (AlertHelper.showConfirm("Alert!", "Delete " + cinema.getCinemaName() + " cinema?")) {
                                Cinema.deleteCinema(cinema.getCinemaID());
                                loadCinemas();
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
        cinemaTable.getColumns().addAll(cinemaIDCol, cinemaNameCol, contactNoCol, openingHoursCol,
            closingHoursCol, streetCol, cityCol, districtCol, actionCol);

        // Add table to center pane
        AnchorPane.setTopAnchor(cinemaTable, 0.0);
        AnchorPane.setBottomAnchor(cinemaTable, 0.0);
        AnchorPane.setLeftAnchor(cinemaTable, 0.0);
        AnchorPane.setRightAnchor(cinemaTable, 0.0);
        Pane.getChildren().add(cinemaTable);
        cinemaTable.setItems(allCinemas);
    }
}
