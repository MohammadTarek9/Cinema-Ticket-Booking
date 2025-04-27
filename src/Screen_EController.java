import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
public class Screen_EController extends CRUD implements AlertHelper {

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
    private Button RestoreButton;

    @FXML
    private Button SubmitButton;

    private  Screen screen;

    public void setScreenToEdit(Screen selectedScreen) {
        this.screen = selectedScreen;
        setupHyperlinkActions("Screen");
        setFields();
    }
    private void setFields() {
        ScreentypeField.setText(screen.getScreenType());
        ResolutionField.setText(screen.getResolution());
        PriceField.setText(String.valueOf(screen.getPrice()));
    }
    @FXML
    private void restoreForm() {
        setFields();
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
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Screen Updated Successfully!");
                ViewLink.fire(); // Navigate to View All page
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update screen!");
            }
        }
    }

}
