import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class CRUD {

    @FXML
    private Hyperlink ViewLink;

    @FXML
    private Hyperlink ReturnLink;

    @FXML
    private Hyperlink CreateLink;

    public void setupHyperlinkActions(String tableName) {
        // Set action for each hyperlink
        ReturnLink.setOnAction(event -> loadScene("HomePage.fxml"));
        CreateLink.setOnAction(event -> loadScene(tableName + "_C.fxml"));
        ViewLink.setOnAction(event -> loadScene(tableName + "_V.fxml"));
    }

    private void loadScene(String fxmlFile) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ReturnLink.getScene().getWindow();
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
            
            // Set the new scene on the stage
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
