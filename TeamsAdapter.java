/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class TeamsAdapter {

    Connection connection;

    public TeamsAdapter(Connection conn, Boolean reset) throws SQLException {
        connection = conn;
        if (reset) {
            Statement stmt = connection.createStatement();
            try {
                // Remove tables if database tables have been created.
                // This will throw an exception if the tables do not exist
                // We drop Matches first because it refrences the table Teams
                stmt.execute("DROP TABLE Matches");
                stmt.execute("DROP TABLE Teams");
                // then do finally
            } catch (SQLException ex) {
                // No need to report an error.
                // The table simply did not exist.
                // do finally to create it
            } finally {
                // Create the table of teams
                stmt.execute("CREATE TABLE Teams ("
                        + "TeamName CHAR(15) NOT NULL PRIMARY KEY, "
                        + "Wins INT, " + "Losses INT, "
                        + "Ties INT" + ")");
                populateSampls();
            }
        }
    }

    private void populateSampls() throws SQLException {
        // Add some teams
        this.insertTeam("Astros");
        this.insertTeam("Marlins");
        this.insertTeam("Brewers");
        this.insertTeam("Cubs");
    }

    public void insertTeam(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("INSERT INTO Teams (TeamName, Wins, Losses, Ties) VALUES ('" + name + "', 0, 0, 0)");
    }

    // Get all teams Data
    public ObservableList<Teams> getTeamsList() throws SQLException {
        ObservableList<Teams> list = FXCollections.observableArrayList();
        ResultSet rs;

        // Create a Statement object
        Statement stmt = connection.createStatement();

        // Create a string with a SELECT statement
        String sqlStatement = "SELECT * FROM Teams";

        // Execute the statement and return the result
        rs = stmt.executeQuery(sqlStatement);

        // Populated the list by going through each row of the ResultSet
        while (rs.next()) {
            list.add(new Teams(rs.getString("TeamName"),
                    rs.getInt("Wins"),
                    rs.getInt("Losses"),
                    rs.getInt("Ties")));
        }
        return list;
    }

    // Get all teams names to populate the ComboBoxs used in Task #3.
    public ObservableList<String> getTeamsNames() throws SQLException {
        ObservableList<String> list = FXCollections.observableArrayList();
        ResultSet rs;

        // Created a Statement object
        Statement stmt = connection.createStatement();

        // Created a string with a SELECT statement
        String sqlStatement = "SELECT * FROM Teams";

        // Executed the statement and return the result
        rs = stmt.executeQuery(sqlStatement);

        // loop for the all rs rows and update list
        while (rs.next()) {
            list.add(rs.getString("TeamName"));
        }

        return list;
    }

    public void setStatus(String homeTeam, String vTeam, int hScore, int vScore) throws SQLException {
        // Declared Win Variables
        boolean homeWin;
        boolean visitorWin;

        //If they are both false, its a tie, if not team with higher score wins
        if (hScore == vScore)
            homeWin = visitorWin = false;
        else {
            homeWin = hScore > vScore;
            visitorWin = !homeWin;
        }

        boolean tied = !(homeWin || visitorWin);

        // Created a Statement object
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs;

        // // Created a string with a SELECT statement
        String sqlStatement = "SELECT * FROM Teams";

        // Executed statement and returned the result
        rs = stmt.executeQuery(sqlStatement);

        while (rs.next()) {
            String team = rs.getString("TeamName").strip(); // remove whitespace


            //Increments score based on winning/losing team
            if (team.equals(homeTeam) && homeWin) {
                rs.updateInt("Wins", rs.getInt("Wins") + 1);
                rs.updateRow(); // row updated after change
            }
            if (team.equals(homeTeam) && visitorWin) {
                rs.updateInt("Losses", rs.getInt("Losses") + 1);
                rs.updateRow();
            }
            if (team.equals(vTeam) && visitorWin) {
                rs.updateInt("Wins", rs.getInt("Wins") + 1);
                rs.updateRow();
            }
            if (team.equals(vTeam) && homeWin) {
                rs.updateInt("Losses", rs.getInt("Losses") + 1);
                rs.updateRow();
            }
            if ((team.equals(homeTeam) || team.equals(vTeam)) && tied) {
                rs.updateInt("Ties", rs.getInt("Ties") + 1);
                rs.updateRow();
            }

        }

    }
}
