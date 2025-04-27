import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

public class Cinemas_EController extends CRUD implements AlertHelper {

    @FXML
    private TextField CinemaNameField;

    @FXML
    private TextField CityField;

    @FXML
    private TextField ClosingHrsField;

    @FXML
    private TextField ContatcNumField;

    @FXML
    private Hyperlink CreateLink;

    @FXML
    private TextField DistrictField;

    @FXML
    private TextField IdField;

    @FXML
    private TextField OpeningHrsField;

    @FXML
    private Button RestoreButton;

    @FXML
    private Hyperlink ReturnLink;

    @FXML
    private TextField StreetField;

    @FXML
    private Button SubmitButton;

    @FXML
    private Hyperlink ViewLink;

    private Cinema cinema;

    public void setCinemaToEdit(Cinema selectedItem) {
        cinema = selectedItem;
        setupHyperlinkActions("Cinemas");
        setFields();
    }

    @FXML
    void restoreForm(ActionEvent event) {
        setFields();

    }

    private void setFields() {
        IdField.setText(String.valueOf(cinema.getCinemaID()));
        CinemaNameField.setText(cinema.getCinemaName());
        ContatcNumField.setText(cinema.getContact_no());
        OpeningHrsField.setText(cinema.getOpening_hours());
        ClosingHrsField.setText(cinema.getClosing_hours());
        StreetField.setText(cinema.getStreet());
        CityField.setText(cinema.getCity());
        DistrictField.setText(cinema.getDistrict());
    }
    

    @FXML
    void submitForm(ActionEvent event) {

        if (IdField.getText().isEmpty() || CinemaNameField.getText().isEmpty() || ContatcNumField.getText().isEmpty()
                || OpeningHrsField.getText().isEmpty() || ClosingHrsField.getText().isEmpty() || StreetField.getText().isEmpty()
                || CityField.getText().isEmpty() || DistrictField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        String cinemaName = CinemaNameField.getText();
        String contactNum = ContatcNumField.getText();
        String openingHrs = OpeningHrsField.getText();
        String closingHrs = ClosingHrsField.getText();
        String street = StreetField.getText();
        String city = CityField.getText();
        String district = DistrictField.getText();

        try {
            Integer.parseInt(IdField.getText());
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Cinema ID must be a number.");
            return;
        }

        if (!isValidContactNumber(contactNum)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid contact number format.");
            return;
        }

        if (!isValidOpeningHours(openingHrs) || !isValidClosingHours(closingHrs)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid opening or closing hours format.");
            return;
        }

        if (AlertHelper.showConfirm("Confirm Submission", "Update cinema in database?")) {
            Cinema cinema = new Cinema(cinemaName, contactNum, openingHrs, closingHrs, street, city, district);
            if (cinema.updateCinema(Integer.parseInt(IdField.getText()))) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Cinema Updated Successfully!");
                ViewLink.fire();
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update cinema!");
            }
        }

    }

    private boolean isValidContactNumber(String contactNum) {
        // match this: +20123456789 (must start with +20)
        return contactNum.matches("\\+20\\d{9}");
    }

    private boolean isValidOpeningHours(String openingHrs) {
        // validate format hh:mm:ss
        return openingHrs.matches("\\d{2}:\\d{2}:\\d{2}");
        
    }

    private boolean isValidClosingHours(String closingHrs) {
        // validate format hh:mm:ss
        return closingHrs.matches("\\d{2}:\\d{2}:\\d{2}");
        
    }


}


