import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

public class Cinemas_CController extends CRUD implements AlertHelper {
    @FXML
    private TextField CInemaNameField;

    @FXML
    private TextField CityField;

    @FXML
    private Button ClearButton;

    @FXML
    private TextField ClosingHrsField;

    @FXML
    private TextField ContactNumField;

    @FXML
    private Hyperlink CreateLink;

    @FXML
    private TextField DistrictField;

    @FXML
    private TextField OpeningHrsField;

    @FXML
    private Hyperlink ReturnLink;

    @FXML
    private TextField StreetField;

    @FXML
    private Button SubmitButton;

    @FXML
    private Hyperlink ViewLink;

    @FXML
    void initialize() {
        setupHyperlinkActions("Cinemas");
    }

    @FXML
    void clearFields(ActionEvent event) {
        CInemaNameField.clear();
        ContactNumField.clear();
        OpeningHrsField.clear();
        ClosingHrsField.clear();
        StreetField.clear();
        CityField.clear();
        DistrictField.clear();
    }

    @FXML
    void submitForm(ActionEvent event) {

        String cinemaName = CInemaNameField.getText();
        String contactNo = ContactNumField.getText();
        String openingHours = OpeningHrsField.getText();
        String closingHours = ClosingHrsField.getText();
        String street = StreetField.getText();
        String city = CityField.getText();
        String district = DistrictField.getText();

        if (cinemaName.isEmpty() || contactNo.isEmpty() || openingHours.isEmpty() || closingHours.isEmpty() || street.isEmpty() || city.isEmpty() || district.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        if (!isValidContactNumber(contactNo)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid contact number format.");
            return;
        }

        if (!isValidOpeningHours(openingHours) || !isValidClosingHours(closingHours)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid opening or closing hours format.");
            return;
        }

        if (AlertHelper.showConfirm("Confirm Submission", "Add new cinema to database?")) {
            Cinema cinema = new Cinema(cinemaName, contactNo, openingHours, closingHours, street, city, district);
            if (cinema.addCinema()) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Cinema Added Successfully!");
            ViewLink.fire();
            } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add cinema!");
            }
        }

    }

    private boolean isValidContactNumber(String contactNum) {
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
