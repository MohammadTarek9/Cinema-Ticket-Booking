

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public interface AlertHelper {

    static void showAlert(Alert.AlertType type, String title, String message, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    static void showAlert(Alert.AlertType type, String title, String message) {
        showAlert(type, title, message, null);
    }

    static boolean showConfirm(String title, String message, String header) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    static boolean showConfirm(String title, String message) {
        return showConfirm(title, message, null);
    }
}