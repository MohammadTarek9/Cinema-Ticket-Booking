import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

public class Halls_CController extends CRUD implements AlertHelper {
     @FXML
    private TextField CinemaIDField;

    @FXML
    private Button ClearButton;

    @FXML
    private Hyperlink CreateLink;

    @FXML
    private TextField NumOfSeatsField;

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

    @FXML
    void initialize() {
        setupHyperlinkActions("Halls");
    }

    @FXML
    void clearFields(ActionEvent event) {

        CinemaIDField.clear();
        NumOfSeatsField.clear();
        ScreenTypeField.clear();
        SoundSysField.clear();

    }

    @FXML
    void submitForm(ActionEvent event) {

        String cinemaID = CinemaIDField.getText();
        String numOfSeats = NumOfSeatsField.getText();
        String screenType = ScreenTypeField.getText();
        String soundSystem = SoundSysField.getText();

        if (cinemaID.isEmpty() || numOfSeats.isEmpty() || screenType.isEmpty() || soundSystem.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        if (!isNumeric(numOfSeats)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Number of seats must be a valid number.");
            return;
        }

        if (AlertHelper.showConfirm("Confirm Submission", "Add new hall to database?")) {
            Hall hall = new Hall(soundSystem, screenType, Integer.parseInt(numOfSeats), Cinema.getCinemaByID(Integer.parseInt(cinemaID)));
            if (hall.addHall()) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Hall Added Successfully!");
                ViewLink.fire();
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add hall!");
            }
        }
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
