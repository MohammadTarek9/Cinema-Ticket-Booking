
//package org.example;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Movies_CController extends CRUD implements AlertHelper {

    @FXML
    private Hyperlink ViewLink;

    @FXML
    private Button AddButton;

    @FXML
    private TextField GenreField;

    @FXML
    private ListView<String> GenreList;

    @FXML
    private ComboBox<String> CensorList;

    @FXML
    private Button ClearButton;

    @FXML
    private TextArea DescriptionField;

    @FXML
    private TextField DirectorField;

    @FXML
    private TextField DurationField;

    @FXML
    private TextField LanguageField;

    @FXML
    private TextField LeadField;

    @FXML
    private TextField RatingField;

    @FXML
    private DatePicker ReleaseDatePicker;

    @FXML
    private Button SubmitButton;

    @FXML
    private TextField TitleField;

    @FXML
    private RadioButton NoRadio;

    @FXML
    private RadioButton YesRadio;

    private final ObservableList<String> genres = FXCollections.observableArrayList("Action", "Drama", "Comedy");


    @FXML
    public void initialize() {
        loadComboList();
        setupHyperlinkActions("Movies");
    }


    @FXML
    private void clearFields(ActionEvent event) {
        // Clear text fields
        TitleField.clear();
        DirectorField.clear();
        LeadField.clear();
        DurationField.clear();
        LanguageField.clear();
        RatingField.clear();
        DescriptionField.clear();
        GenreField.clear();

        // Reset date picker
        ReleaseDatePicker.setValue(null);

        // Reset radio buttons
        YesRadio.setSelected(false);
        NoRadio.setSelected(false);

        // Set focus back to the first field
        TitleField.requestFocus();
    }

    @FXML
    private void submitForm(ActionEvent event) {
        // Validate required fields
        if (TitleField.getText().isEmpty() || DirectorField.getText().isEmpty() || LeadField.getText().isEmpty() || DurationField.getText().isEmpty() || ReleaseDatePicker.getValue() == null || CensorList.getValue() == null || (!YesRadio.isSelected() && !NoRadio.isSelected())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all required fields");
            return;
        }

        // Validate numeric fields
        try {
            Double.parseDouble(RatingField.getText());
            Integer.parseInt(DurationField.getText());
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Rating and Duration must be numbers");
            return;
        }

        // Get all values from form
        String title = TitleField.getText();
        String director = DirectorField.getText();
        String leadActor = LeadField.getText();
        int duration = Integer.parseInt(DurationField.getText());
        String language = LanguageField.getText();
        double rating = Double.parseDouble(RatingField.getText());
        String description = DescriptionField.getText();
        LocalDate releaseDate = ReleaseDatePicker.getValue();
        String censorRating = CensorList.getValue();
        boolean isAvailable = YesRadio.isSelected();
        Set<String> genres = new HashSet<String>(GenreList.getSelectionModel().getSelectedItems());

        if (rating < 0 || rating > 10) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Rating must be between 0 and 10");
            return;
        }
        if (duration <= 0) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Duration must be greater than zero");
            return;
        }

        if (AlertHelper.showConfirm("Confirm Submission", "Add new movie to database?")) {
            Movie movie = new Movie(title, director, leadActor, duration, language, rating, description, releaseDate, censorRating, isAvailable, genres);
            if (movie.addMovie()) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Movie Added Successfully!");
                ViewLink.fire();
            } else AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add movie!");

        }
    }

    private void loadComboList() {
        ObservableList<String> options = FXCollections.observableArrayList("PG-13", "PG", "R");
        CensorList.setItems(options);
        GenreList.setItems(genres);
        GenreList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        AddButton.setOnAction(event -> {
            String newGenre = GenreField.getText().trim();
            if (!newGenre.isEmpty() && !genres.contains(newGenre)) {
                genres.add(newGenre);
                GenreField.clear();
            }
        });
    }
}
