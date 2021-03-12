package TennisBallGames;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AddScoreController implements Initializable {
    // some FXML Variables
    @FXML
    ComboBox<String> matchBox;

    @FXML
    TextField homeTeamScore;

    @FXML
    TextField visitorTeamScore;

    @FXML
    Button cancelBtn;

    // The data variable is used to populate the ComboBox
    final ObservableList<String> data = FXCollections.observableArrayList();

    // local variables
    private MatchesAdapter matchesAdapter;
    private TeamsAdapter teamsAdapter;

    public void setModel(MatchesAdapter match, TeamsAdapter team) {
        matchesAdapter = match;
        teamsAdapter = team;
        buildComboBoxData();
    }

    @FXML
    public void cancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void save() {
        try {
            int identify = Integer.parseInt(matchBox.getValue().substring(0, 1)); // The unique match number,first character of the matchBox entry
            String homeTeam = matchBox.getValue().split("-")[1].strip(); // HomeTeam and VisitorTeam strings, made by referencing specific entries of the split array
            int homeScore = Integer.parseInt(homeTeamScore.getText());
            String visitorTeam = matchBox.getValue().split("-")[2].strip();
            int visitorScore = Integer.parseInt(visitorTeamScore.getText());


            matchesAdapter.setTeamsScore(identify, homeScore, visitorScore);
            teamsAdapter.setStatus(homeTeam, visitorTeam, homeScore, visitorScore);

        } catch (SQLException ex) {
            displayAlert("ERROR: " + ex.getMessage());
        }

        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    public void buildComboBoxData() {
        try {
            data.addAll(matchesAdapter.getMatchesNamesList());
        } catch (SQLException ex) {
            displayAlert("ERROR: " + ex.getMessage());
        }
    }

    private void displayAlert(String msg) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Alert.fxml"));
            Parent ERROR = loader.load();
            AlertController controller = (AlertController) loader.getController();

            Scene scene = new Scene(ERROR);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.getIcons().add(new Image("file:src/TennisBallGames/WesternLogo.png"));
            controller.setAlertText(msg);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex1) {

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        matchBox.setItems(data);
    }
}
