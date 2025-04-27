

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.sql.PreparedStatement;

public class Movies_VController extends CRUD implements AlertHelper{

    @FXML
    private AnchorPane Pane;

    @FXML
    public void initialize() {
        loadMovies();
        setupHyperlinkActions("Movies");
    }

    private void loadMovies() {
        ObservableList<Movie> allMovies = FXCollections.observableArrayList();
        String query = "SELECT * FROM movie";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Movie movie = createMovieFromResultSet(rs);
                allMovies.add(movie);
            }

            setupMovieTable(allMovies);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load movies: " + e.getMessage());
        }
    }

    private Movie createMovieFromResultSet(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setLastMovieID(rs.getInt("movieID"));
        movie.setTitle(rs.getString("title"));
        movie.setDescription(rs.getString("description"));
        movie.setMainLanguage(rs.getString("main_language"));
        movie.setDuration(rs.getInt("duration"));
        movie.setReleaseDate(
                rs.getInt("movie_day"),
                rs.getInt("movie_month"),
                rs.getInt("movie_year")
        );
        movie.setNowShowing(rs.getBoolean("now_showing"));
        movie.setCensorship(rs.getString("censorship"));
        movie.setRating(rs.getDouble("rating"));
        movie.setLeadActor(rs.getString("lead_actor"));
        movie.setDirector(rs.getString("director"));

        Set<String> genres = getGenresForMovie(movie.getMovieID());
        movie.setGenres(genres);

        return movie;
    }

    private void setupMovieTable(ObservableList<Movie> allMovies) {
        TableView<Movie> movieTable = new TableView<>();

        // Create columns
        TableColumn<Movie, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("movieID"));

        TableColumn<Movie, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Movie, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Movie, String> langCol = new TableColumn<>("Language");
        langCol.setCellValueFactory(new PropertyValueFactory<>("mainLanguage"));

        TableColumn<Movie, Integer> durationCol = new TableColumn<>("Duration (min)");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));

        TableColumn<Movie, Integer> dayCol = new TableColumn<>("Movie Day");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("movieDay"));

        TableColumn<Movie, Integer> monthCol = new TableColumn<>("Movie Month");
        monthCol.setCellValueFactory(new PropertyValueFactory<>("movieMonth"));

        TableColumn<Movie, Integer> yearCol = new TableColumn<>("Movie Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("movieYear"));

        TableColumn<Movie, String> showingCol = new TableColumn<>("Now Showing");
        showingCol.setCellValueFactory(new PropertyValueFactory<>("nowShowing"));

        TableColumn<Movie, String> censorshipCol = new TableColumn<>("Censorship");
        censorshipCol.setCellValueFactory(new PropertyValueFactory<>("censorship"));

        TableColumn<Movie, Double> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));

        TableColumn<Movie, String> actorCol = new TableColumn<>("Lead Actor");
        actorCol.setCellValueFactory(new PropertyValueFactory<>("leadActor"));

        TableColumn<Movie, String> directorCol = new TableColumn<>("Director");
        directorCol.setCellValueFactory(new PropertyValueFactory<>("director"));

        TableColumn<Movie, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(new Callback<TableColumn<Movie, Void>, TableCell<Movie, Void>>() {
            @Override
            public TableCell<Movie, Void> call(final TableColumn<Movie, Void> param) {
                return new TableCell<Movie, Void>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");

                    {
                        // Style buttons (optional)
                        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                        // Set button actions
                        editBtn.setOnAction(event -> {
                            Movie movie = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("Movies_E.fxml"));
                                Parent root = loader.load();

                                // Get controller and pass data
                                Movies_EContoller editController = loader.getController();
                                editController.setMovieToEdit(movie);

                                Scene scene = Pane.getScene();
                                scene.setRoot(root);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        // Delete functionality
                        deleteBtn.setOnAction(event -> {

                            Movie movie = getTableView().getItems().get(getIndex());
                            if (AlertHelper.showConfirm("Alert!", "Delete " + movie.getTitle() + " movie?")) {
                                Movie.deleteMovie(movie.getMovieID());
                                loadMovies();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
                        }
                    }
                };
            }
        });

        descCol.setPrefWidth(100); // Preferred width
        // Add columns to table
        movieTable.getColumns().addAll(idCol, titleCol, descCol, langCol, durationCol,
                dayCol, monthCol, yearCol, showingCol, censorshipCol, ratingCol,
                actorCol, directorCol, actionCol);

        // Add table to center pane
        AnchorPane.setTopAnchor(movieTable, 0.0);
        AnchorPane.setBottomAnchor(movieTable, 0.0);
        AnchorPane.setLeftAnchor(movieTable, 0.0);
        AnchorPane.setRightAnchor(movieTable, 0.0);
        Pane.getChildren().add(movieTable);
        movieTable.setItems(allMovies);
    }

    private Set<String> getGenresForMovie(int movieID) throws SQLException {
        Set<String> genres = new HashSet<>();
        String query = "SELECT movie_genre FROM genre WHERE movieID = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, movieID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                genres.add(rs.getString("movie_genre"));
            }
        }
        return genres;
    }
}
