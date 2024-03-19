/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FinalProject;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author deemashaath
 */
public class AdminChangePasswordController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TextField newPasstextField;
    @FXML
    private TextField oldPassTextField;
    @FXML
    private TextField confirmNewPasstextField;
    @FXML
    private Button applyEdits;

    private Connection connection;
    private Statement statement;
    private int clientId = ApplyMethods.getCurrentClientId();
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private MenuItem backgroundColorMenuItem;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button resetButton;
    @FXML
    private VBox rootNode;
    @FXML
    private MenuItem size22;
    @FXML
    private MenuItem size14;
    @FXML
    private MenuItem size20;
    @FXML
    private MenuItem size18;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void applyEditsButton(ActionEvent event) {
        validateInput();
    }

    private void validateInput() {
        String oldPassInput = oldPassTextField.getText();
        String newPassInput = newPasstextField.getText();
        String confPassInput = confirmNewPasstextField.getText();

        // Check if the old password matches the current password of the client
        if(oldPassInput.isEmpty() || newPassInput.isEmpty() || confPassInput.isEmpty()){
            ApplyMethods.showErrorAlert("Error", "Empty fields!", "You must fill all the fields. Try again.");
            return;
        }
        if (!checkCurrentPassword(oldPassInput)) {
            ApplyMethods.showErrorAlert("Error", "Invalid Password!", "Incorrect old password. Please enter the correct old password.");
            return;
        }

        if (oldPassInput.equals(newPassInput)) {
            ApplyMethods.showErrorAlert("Error", "Invalid Password!", "New password must be different from the old password.");
            return;
        }
        String validatePass = ApplyMethods.isStrongPassword(newPassInput);
        if (!validatePass.isEmpty()) {
            ApplyMethods.showErrorAlert("Error", "Invalid Password!", "Invalid password. " + ApplyMethods.isStrongPassword(newPassInput));
            return;
        }

        if (!newPassInput.equals(confPassInput)) {
            ApplyMethods.showErrorAlert("Error", "Invalid Password!", "New password and confirmation password do not match.");
            return;
        }

        // If all validations pass
        updatePassword(newPassInput);
    }

    private boolean checkCurrentPassword(String password) {
        try {
            String query = "SELECT Password FROM users WHERE id = "+clientId;
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String currentPassword = resultSet.getString("Password");
                return currentPassword.equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void updatePassword(String newPassword) {
        try {
            String sql = "UPDATE users SET Password='" + newPassword + "' where id=" + clientId;
            statement.executeUpdate(sql);
            ApplyMethods.showInfoAlert("Success", "Password changed successfully", "You have just changed your password. Please don't share it with others.");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void resetButtonHandle(ActionEvent event) {
        oldPassTextField.setText("");
        newPasstextField.setText("");
        confirmNewPasstextField.setText("");
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "AdminDashboard.fxml", "Admin Dashboard", AdminChangePasswordController.class);
    }


    @FXML
    private void exitMenuItemHandle(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void helpmenuItem(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About App");
        alert.setHeaderText(null);
        alert.setContentText("This is the about information of the app.");
        alert.showAndWait();
    }

    @FXML
    private void fontSizeHandle(ActionEvent event) {
        size14.setOnAction(new MyEventHandler());
        size18.setOnAction(new MyEventHandler());
        size20.setOnAction(new MyEventHandler());
        size22.setOnAction(new MyEventHandler());
    }

    @FXML
    private void backgroundColorHandle(ActionEvent event) {
        Color color = colorPicker.getValue();

        if (color != null) {
            String colorString = String.format("#%02x%02x%02x",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));

            rootNode.setStyle("-fx-background-color: " + colorString);
        }
    }

    @FXML
    private void sansSeridFontFamily(ActionEvent event) {
        
        rootNode.setStyle("-fx-font-family: 'SansSerif';");
    }

    @FXML
    private void arialFontFamily(ActionEvent event) {
        rootNode.setStyle("-fx-font-family: 'Arial';");
    }

    @FXML
    private void monospaceFontFamilyHandle(ActionEvent event) {
        rootNode.setStyle("-fx-font-family: 'monospace';");
    }
    

    class MyEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent t) {
            if (t.getSource() == size14 || t.getSource() == size18 || t.getSource() == size20 || t.getSource() == size22) {
                MenuItem menuItem = (MenuItem) t.getSource();
                String fontSizeText = menuItem.getText();
                int fontSize = Integer.parseInt(fontSizeText);
                Font newFont = Font.font(fontSize);
                Stage stage = (Stage) menuBar.getScene().getWindow();
                stage.getScene().getRoot().setStyle("-fx-font-size: " + fontSize + "px; -fx-font-family: " + newFont.getFamily() + ";");
            }
        }
    }

}
