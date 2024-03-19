/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FinalProject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author deemashaath
 */
public class LoginScreenController implements Initializable {

    @FXML
    private VBox card;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Label registrationErrLabel;
    @FXML
    private Button loginBtn;
    @FXML
    private Button registerBtn;
    @FXML
    private Tooltip emailTooltip;
    @FXML
    private Tooltip passwordTooltip;

    Connection connection;
    Statement statement;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    private void loginHandle(ActionEvent event) throws SQLException {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        if (email.isEmpty() || !validatePassword(password)) {
            registrationErrLabel.setText("Please fill in all the fields.");
            if (email.isEmpty()) {
                emailTooltip.show(emailTextField, screenXX(emailTextField) + 300, screenYY(emailTextField) + 10);
            }
            if (password.isEmpty()) {
                passwordTooltip.show(passwordTextField, screenXX(passwordTextField) + 300, screenYY(passwordTextField) + 10);
            }
            return;
        }

        String query = "SELECT * FROM Users WHERE email = '" + email + "' AND BINARY password = '" + password + "'"; //BINARY to make it case-sensitive for the password
        statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);

        if (rs.next()) {
            String role = rs.getString("role");

            if (role.equalsIgnoreCase("client")) {
                ApplyMethods.moveTo(card, "ClientDashboard.fxml", "Client Dashboard", LoginScreenController.class);
            } else if (role.equalsIgnoreCase("admin")) {
                ApplyMethods.moveTo(card, "AdminDashboard.fxml", "Admin Dashboard", LoginScreenController.class);
            }
            Users user = new Users(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("mobile"), rs.getString("password"), rs.getString("role"), rs.getString("image"));
            ClientSession.setCurrentUser(user);
        } else {
            registrationErrLabel.setText("Invalid email or password.");
        }

    }

    private boolean validatePassword(String password) {
        return password.length() >= 8 && password != null && !password.isEmpty();
    }

    private boolean validateEmail(String email) {
        email = email.trim();
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailPattern)) {
            return false;
        }
        if (email.startsWith(".") || email.endsWith(".")
                || email.startsWith("@") || email.endsWith("@")
                || email.startsWith("_") || email.endsWith("_")) {
            return false;
        }
        return true;
    }

    @FXML
    private void registerHandle(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterScreen.fxml"));
        Parent dashboardRoot = loader.load();
        Scene registrationScene = new Scene(dashboardRoot);
        Stage currentStage = (Stage) card.getScene().getWindow(); //to get the currrent stage
        currentStage.setScene(registrationScene);
        currentStage.setTitle("Registration Form");
        currentStage.show();
    }

    private double screenXX(Node node) {
        Bounds bounds = node.localToScreen(emailTextField.getBoundsInLocal());
        double screenX = bounds.getMinX();
        return screenX;
    }

    private double screenYY(Node node) {
        Bounds bounds = node.localToScreen(emailTextField.getBoundsInLocal());
        double screenY = bounds.getMinY();
        return screenY;
    }

    @FXML
    private void resetHandle(ActionEvent event) {
        passwordTextField.setText("");
        emailTextField.setText("");
        registrationErrLabel.setText("");
    }

}
