//package org.asu.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.LocalTimeStringConverter;
//import org.asu.Show;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Shows_CController extends CRUD {

    @FXML
    private TextField HallField;

    @FXML
    private TextField MovieIdField;

    @FXML
    private DatePicker ShowDatePicker;

    @FXML
    private TextField ShowTimeField;

    @FXML
    private Hyperlink ViewLink;

    @FXML
    public void initialize() {
        setupHyperlinkActions("Shows");
    }


    @FXML
    private void clearFields(ActionEvent event) {
        // Clear text fields
        HallField.clear();
        MovieIdField.clear();
        ShowTimeField.clear();
        ShowDatePicker.setValue(null);

        // Set focus back to the first field
        HallField.requestFocus();
    }

    @FXML
    private void submitForm(ActionEvent event) {
        // Validate required fields
        if (HallField.getText().isEmpty() || MovieIdField.getText().isEmpty() || ShowDatePicker.getValue() == null || ShowTimeField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all required fields");
            return;
        }

        // Get all values from form
        int hall = Integer.parseInt(HallField.getText());
        int movie = Integer.parseInt(MovieIdField.getText());
        LocalDate date = ShowDatePicker.getValue();
        LocalTime time = new LocalTimeStringConverter(FormatStyle.SHORT, Locale.UK).fromString(ShowTimeField.getText());

        if (AlertHelper.showConfirm("Confirm Submission", "Add new show to database?")) {
            Show show = new Show(hall, movie, date, time);
            if (show.addShow()) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Show Added Successfully!");
                ViewLink.fire();
            } else AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add show!");

        }
    }

}
