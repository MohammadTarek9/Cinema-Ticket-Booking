//package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminLoginController {

    // Hardcoded admin code (for simplicity)
    private static final String ADMIN_CODE = "admin123";

    @FXML
    private PasswordField codeField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String enteredCode = codeField.getText().trim();

        if (enteredCode.equals(ADMIN_CODE)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) codeField.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 700));
                stage.setTitle("Admin Dashboard");

            } catch (IOException e) {
                e.printStackTrace();
                e.printStackTrace();
                errorLabel.setText("Error loading admin dashboard");
                errorLabel.setVisible(true);
            }
        } else {
            // Show error message
            errorLabel.setText("Invalid admin code");
            errorLabel.setVisible(true);
            codeField.clear();
        }
    }
}