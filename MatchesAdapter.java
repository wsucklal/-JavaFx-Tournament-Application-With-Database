package TennisBallGames;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Abdelkader
 */
public class MatchesAdapter {

    Connection connection;

    public MatchesAdapter(Connection conn, Boolean reset) throws SQLException {
        connection = conn;
        if (reset) {
            Statement stmt = connection.createStatement();
            try {
                    // Remove tables if database tables have been created.
                // This will throw an exception if the tables do not exist
                stmt.execute("DROP TABLE Matches");
                // then do finally
            } catch (SQLException ex) {
                // No need to report an error.
                // The table simply did not exist.
                // do finally to create it
            } finally {
                // Create the table of Matches
                stmt.execute("CREATE TABLE Matches ("
                        + "MatchNumber INT NOT NULL PRIMARY KEY, "
                        + "HomeTeam CHAR(15) NOT NULL REFERENCES Teams (TeamName), "
                        + "VisitorTeam CHAR(15) NOT NULL REFERENCES Teams (TeamName), "
                        + "HomeTeamScore INT, "
                        + "VisitorTeamScore INT "
                        + ")");
                populateSamples();
            }
        }
    }

    private void populateSamples() throws SQLException {
        // Create a listing of the matches to be played
        this.insertMatch(1, "Astros", "Brewers");
        this.insertMatch(2, "Brewers", "Cubs");
        this.insertMatch(3, "Cubs", "Astros");
    }


    public int getMax() throws SQLException {

        // Add your work code here for Task #3
        ResultSet rs;

        // Created a Statement object
        Statement stmt = connection.createStatement();

        // Created a string with a SELECT MAX Statement
        String sqlStatement = "SELECT MAX(MatchNumber) FROM Matches";

        rs = stmt.executeQuery(sqlStatement);

        rs.next();

        //Got the int from column 1 of one-column table
        int num = rs.getInt(1);

        return num;
    }

    public void insertMatch(int num, String home, String visitor) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("INSERT INTO Matches (MatchNumber, HomeTeam, VisitorTeam, HomeTeamScore, VisitorTeamScore) "
                + "VALUES (" + num + " , '" + home + "' , '" + visitor + "', 0, 0)");
    }

    // Get all Matches
    public ObservableList<Matches> getMatchesList() throws SQLException {
        ObservableList<Matches> matchesList = FXCollections.observableArrayList();
        ResultSet rs;

        // Created a Statement object
        Statement stmt = connection.createStatement();

        // Created a string with a SELECT statement
        String sqlStatement = "SELECT * FROM Matches";

        // Executed the statement and return the result
        rs = stmt.executeQuery(sqlStatement);


        while (rs.next()) {
            matchesList.add(new Matches(rs.getInt("MatchNumber"), rs.getString("HomeTeam"), rs.getString("VisitorTeam"), rs.getInt("HomeTeamScore"), rs.getInt("VisitorTeamScore")));
            // Add a new match object with required parameters
        }

        return matchesList;
    }

    // Get a String list of matches to populate the ComboBox used in Task #4.
    public ObservableList<String> getMatchesNamesList() throws SQLException {
        ObservableList<String> list = FXCollections.observableArrayList();
        ResultSet rs;

        // Create a Statement object
        Statement stmt = connection.createStatement();


        // Create a string with a SELECT statement
        String sqlStatement = "SELECT * FROM Matches";

        // Execute the statement and return the result
        rs = stmt.executeQuery(sqlStatement);

        // Loop the entire rows of rs and set the string values of list
        while (rs.next()) {
            Matches myMatch = new Matches(rs.getInt("MatchNumber"), rs.getString("HomeTeam"), rs.getString("VisitorTeam"));

            list.add(String.valueOf(myMatch.getMatchNumber() + " - " + myMatch.getHomeTeam() + " - " + myMatch.getVisitorTeam())); // Populating list for usage in ComboBox
        }

        return list;
    }


    public void setTeamsScore(int matchNumber, int hScore, int vScore) throws SQLException {
        String matchID = String.valueOf(matchNumber); // String representation of match number

        // Add your code here for Task #4
        ResultSet rs;

        // Created a Statement object
        Statement stmt = connection.createStatement();

        // Created a string with an UPDATE statement with WHERE condition to ensure correct match is picked
        String sqlStatement = "UPDATE Matches SET HomeTeamScore = " + hScore + ", VisitorTeamScore = " + vScore + "WHERE MatchNumber = " + matchID;

        stmt.executeUpdate(sqlStatement);

    }
}
