//package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
public class Screen_CController extends CRUD implements AlertHelper{

    @FXML
    private Hyperlink ReturnLink;

    @FXML
    private Hyperlink ViewLink;

    @FXML
    private Hyperlink CreateLink;

    @FXML
    private TextField ScreentypeField;

    @FXML
    private TextField ResolutionField;

    @FXML
    private TextField PriceField;

    @FXML
    private Button SubmitButton;

    @FXML
    private Button ClearButton;
    @FXML
    public void initialize() {
        setupHyperlinkActions("Screen");
    }
    @FXML
    private void clearFields(ActionEvent event) {
        ScreentypeField.clear();
        ResolutionField.clear();
        PriceField.clear();
        ScreentypeField.requestFocus();
    }
    @FXML
    private void submitForm(ActionEvent event) {
        // Validate required fields
        if (ScreentypeField.getText().isEmpty() || ResolutionField.getText().isEmpty() || PriceField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all fields.");
            return;
        }

        try {
            Double.parseDouble(PriceField.getText());
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a valid number.");
            return;
        }
        String screenType = ScreentypeField.getText();
        String resolution = ResolutionField.getText();
        double price = Double.parseDouble(PriceField.getText());

        if (price <= 0) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be greater than zero.");
            return;
        }
        if (AlertHelper.showConfirm("Confirm Submission", "Add new screen to database?")) {
            Screen screen = new Screen(screenType, price, resolution);
            if (screen.addScreen()) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Screen Added Successfully!");
                ViewLink.fire(); // Navigate to View All page
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add screen!");
            }
        }
    }



}
