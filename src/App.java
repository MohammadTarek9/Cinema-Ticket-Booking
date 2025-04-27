
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("HomePage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

        scene.getStylesheets().add((Objects.requireNonNull(getClass().getResource("dark-theme.css"))).toExternalForm());
        stage.setMaximized(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
        stage.setTitle("Cinema Booking System");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> {
            DatabaseConnector.closeConnection();
        });
    }

    public static void main(String[] args) {
        launch();
    }

}