package org.asu.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.LocalTimeStringConverter;
import org.asu.Show;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Shows_EContoller extends CRUD {

    @FXML
    private TextField HallField;

    @FXML
    private TextField MovieIdField;


    @FXML
    private TextField ShowIdField;

    @FXML
    private DatePicker ShowDatePicker;

    @FXML
    private TextField ShowTimeField;

    @FXML
    private Hyperlink ViewLink;

    private Show show;

    public void setShowToEdit(Show selectedItem) {
        show = selectedItem;
        setupHyperlinkActions("Shows");
        setFields();
    }

    private void setFields() {
        HallField.setText(String.valueOf(show.getHall_no()));
        MovieIdField.setText(String.valueOf(show.getMovieID()));
        ShowTimeField.setText(show.getShow_time().toString());
        ShowDatePicker.setValue(show.getShow_date());
        ShowIdField.setText(String.valueOf(show.getShowID()));
    }

    @FXML
    private void restoreForm() {
        setFields();
    }

    @FXML
    private void submitForm() {
        // Validate required fields
        if (ShowIdField.getText().isEmpty() || HallField.getText().isEmpty() || MovieIdField.getText().isEmpty() || ShowTimeField.getText().isEmpty() || ShowDatePicker.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all required fields");
            return;
        }


        // Get all values from form
        int showID = Integer.parseInt(ShowIdField.getText());
        int hall = Integer.parseInt(HallField.getText());
        int movie = Integer.parseInt(MovieIdField.getText());
        LocalDate date = ShowDatePicker.getValue();
        LocalTime time = new LocalTimeStringConverter(FormatStyle.SHORT, Locale.UK).fromString(ShowTimeField.getText());

        if (AlertHelper.showConfirm("Confirm Submission", "Update show in database?")) {
            Show show = new Show(hall, movie, date, time);
            if (show.updateShow(showID)) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Show Updated Successfully!");
                ViewLink.fire();
            } else AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update show!");

        }
    }
}
