//package org.example;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

public class Halls_EController extends CRUD implements AlertHelper {
    @FXML
    private TextField CinemaNameField;

    @FXML
    private Hyperlink CreateLink;

    @FXML
    private TextField HallNumField;

    @FXML
    private TextField NumOfSeatsField;

    @FXML
    private Button RestoreButton;

    @FXML
    private Hyperlink ReturnLink;

    @FXML
    private TextField ScreenTypeField;

    @FXML
    private TextField SoundSysField;

    @FXML
    private Button SubmitButton;

    @FXML
    private Hyperlink ViewLink;

    private Hall hall;

    public void setHallToEdit(Hall selectedItem){
        hall = selectedItem;
        setupHyperlinkActions("Halls");
        setFields();
    }

    private void setFields() {
        HallNumField.setText(String.valueOf(hall.getHall_no()));
        SoundSysField.setText(hall.getSound_sys());
        ScreenTypeField.setText(hall.getScreen_type());
        NumOfSeatsField.setText(String.valueOf(hall.getNo_of_seats()));
        CinemaNameField.setText(hall.getCinema().getCinemaID() + "");
    }

    @FXML
    void restoreForm(ActionEvent event) {
        setFields();

    }

    @FXML
    void submitForm(ActionEvent event) {
        if (HallNumField.getText().isEmpty() || SoundSysField.getText().isEmpty() || ScreenTypeField.getText().isEmpty()
                || NumOfSeatsField.getText().isEmpty() || CinemaNameField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        String hallNum = HallNumField.getText();
        String soundSys = SoundSysField.getText();
        String screenType = ScreenTypeField.getText();
        String numOfSeats = NumOfSeatsField.getText();
        String cinemaID = CinemaNameField.getText();

        try {
            Integer.parseInt(hallNum);
            Integer.parseInt(numOfSeats);
            Integer.parseInt(cinemaID);
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Hall number, Cinema ID and number of seats must be numbers.");
            return;
        }

        if (AlertHelper.showConfirm("Confirm Submission", "Update hall in database?")) {
            Hall updatedHall = new Hall(Integer.parseInt(hallNum), soundSys, screenType, Integer.parseInt(numOfSeats), Cinema.getCinemaByID(Integer.parseInt(cinemaID)));
            if (updatedHall.getCinema() == null) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Cinema not found.");
                return;
            }
            if (updatedHall.updateHall(hall.getHall_no())) {
                hall.deleteAllSeats(hall.getHall_no());
                hall.addSeats(hall.getHall_no());
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Hall Updated Successfully!");
                ViewLink.fire();
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update hall!");
            }
        }
    }
}
