
//package org.example;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Movies_EContoller extends CRUD {

    @FXML
    private Hyperlink ViewLink;

    @FXML
    private TextField IdField;

    @FXML
    private Button AddButton;

    @FXML
    private TextField GenreField;

    @FXML
    private ListView<String> GenreList;

    @FXML
    private ComboBox<String> CensorList;

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
    private RadioButton NoRadio;

    @FXML
    private TextField RatingField;

    @FXML
    private DatePicker ReleaseDatePicker;

    @FXML
    private TextField TitleField;

    @FXML
    private RadioButton YesRadio;


    @FXML
    private Button RestoreButton;

    @FXML
    private Button SubmitButton;

    private Movie movie;

    private ObservableList<String> genres;

    public void setMovieToEdit(Movie selectedItem) {
        movie = selectedItem;
        setupHyperlinkActions("Movies");
        loadComboList();
        setFields();
    }

    private void setFields() {
        IdField.setText(String.valueOf(movie.getMovieID()));
        DescriptionField.setText(movie.getDescription());
        TitleField.setText(movie.getTitle());
        RatingField.setText(String.valueOf(movie.getRating()));
        DirectorField.setText(movie.getDirector());
        DurationField.setText(String.valueOf(movie.getDuration()));
        LanguageField.setText(movie.getMainLanguage());
        LeadField.setText(movie.getLeadActor());

        final ToggleGroup group = new ToggleGroup();
        YesRadio.setToggleGroup(group);
        NoRadio.setToggleGroup(group);
        if (movie.isNowShowing()) YesRadio.setSelected(true);
        else NoRadio.setSelected(true);

        LocalDate date = LocalDate.of(movie.getMovieYear(), movie.getMovieMonth(), movie.getMovieDay());
        ReleaseDatePicker.setValue(date);
        CensorList.getSelectionModel().select(movie.getCensorship());
    }

    private void loadComboList() {
        genres = FXCollections.observableArrayList(movie.getGenres());
        ObservableList<String> options = FXCollections.observableArrayList("PG-13", "PG", "R");
        CensorList.setItems(options);
        GenreList.setItems(genres);
        GenreList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        GenreList.getSelectionModel().selectAll();

        AddButton.setOnAction(event -> {
            String newGenre = GenreField.getText().trim();
            if (!newGenre.isEmpty() && !genres.contains(newGenre)) {
                genres.add(newGenre);
                GenreField.clear();
            }
        });
    }

    @FXML
    private void restoreForm() {
        loadComboList();
        setFields();
    }

    @FXML
    private void submitForm() {
        // Validate required fields
        if (IdField.getText().isEmpty() || TitleField.getText().isEmpty() || DirectorField.getText().isEmpty() || LeadField.getText().isEmpty() || DurationField.getText().isEmpty() || ReleaseDatePicker.getValue() == null || CensorList.getValue() == null || (!YesRadio.isSelected() && !NoRadio.isSelected())) {
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
        String movieID = IdField.getText();
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

        if (AlertHelper.showConfirm("Confirm Submission", "Update movie in database?")) {
            Movie movie = new Movie(title, director, leadActor, duration, language, rating, description, releaseDate, censorRating, isAvailable, genres);
            if (movie.updateMovie(Integer.parseInt(movieID))) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Movie Updated Successfully!");
                ViewLink.fire();
            } else AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update movie!");

        }
    }
}
