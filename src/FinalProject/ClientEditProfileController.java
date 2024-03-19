/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FinalProject;

import java.io.File;
import java.net.MalformedURLException;
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
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author omen
 */
public class ClientEditProfileController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button editButton;
    @FXML
    private ImageView profileImage;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField mobileTextField;
    @FXML
    private Button chooseImageButton;
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
    private VBox rootNode;
    @FXML
    private MenuItem size22;
    @FXML
    private MenuItem size14;
    @FXML
    private MenuItem size20;
    @FXML
    private MenuItem size18;
    
    Connection connection;
    Statement statement;
    int userId;
    int clientId = ApplyMethods.getCurrentClientId();
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // TODO
        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            String fillTextFields = "Select name, email, mobile, image from users where id=" + clientId;
            ResultSet rss = statement.executeQuery(fillTextFields);
            if (rss.next()) {
                nameTextField.setText(rss.getString("name"));
                emailTextField.setText(rss.getString("email"));
                mobileTextField.setText(rss.getString("mobile"));  
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(profileImage, "ClientDashboard.fxml", "Client Dashboard Screen", ClientEditProfileController.class);
    }

    @FXML
    private void applyEditsButton(ActionEvent event) {
        if (validateFields()) {
            editProfile();
        } else {
            System.out.println("Error");
        }
    }

    private void editProfile() {
        try {
            String updatedName = nameTextField.getText().trim();
            String updatedEmail = emailTextField.getText().trim();
            String updatedMobile = mobileTextField.getText().trim();

            // Check if the updated email is different from the current email in the table
            String checkQuery = "SELECT email FROM Users WHERE id = " + clientId;
            ResultSet resultSet = statement.executeQuery(checkQuery);
            if (resultSet.next()) {
                String currentEmail = resultSet.getString("email");
                if (!updatedEmail.equals(currentEmail)) {
                    // The updated email is different, check if it already exists in the table
                    String emailCheckQuery = "SELECT * FROM Users WHERE email = '" + updatedEmail + "'";
                    ResultSet emailResultset = statement.executeQuery(emailCheckQuery);
                    if (emailResultset.next()) {
                        // An existing email already exists in the table, show an error alert
                        System.out.println("Error: Email already exists.");
                        ApplyMethods.showErrorAlert("Error", "Email alredy exists", "Cannot comlete the operation. Email alresy exists, please try again!");
                        return; // Exit the method
                    }
                }
            }

            statement = connection.createStatement();
            String updateQuery = "UPDATE Users SET name = '" + updatedName + "', email = '" + updatedEmail + "', mobile = '" + updatedMobile + "' WHERE id = " + clientId;
            statement.executeUpdate(updateQuery);

            System.out.println("Profile updated successfully.");
            ApplyMethods.showInfoAlert("Success", "Done!", "Profile edited successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void chooseProfileImageHandle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", ".jpg", ".png", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(profileImage.getScene().getWindow());
        if (selectedFile != null) {
            // Load the selected image and display it
            Image image = new Image("file:" + selectedFile.getAbsolutePath());
            profileImage.setImage(image);

            // Update the image in the database
            try {
                String query = "UPDATE users SET image = '" + selectedFile.getAbsolutePath() + "' WHERE id = " + clientId;
                int rowsAffected = statement.executeUpdate(query);
                if (rowsAffected > 0) {
                    System.out.println("Image updated successfully in the database.");
                } else {
                    System.out.println("Failed to update the image in the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateFields() {
        String name = nameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String mobile = mobileTextField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
            Tooltip tooltip = new Tooltip("All fields are required.");
            setTooltip(nameTextField, tooltip);
            setTooltip(emailTextField, tooltip);
            setTooltip(mobileTextField, tooltip);
            System.out.println("error1");
            return false;
        }
        if (name.isEmpty()) {
            Tooltip tooltipName = new Tooltip("fill name");
            tooltipName.show(nameTextField, screenXX(nameTextField) + 300, screenYY(nameTextField) + 10);
            System.out.println("error2");
            return false;
        }
        if (!isValidEmail(email)) {
            Tooltip tooltipEmail = new Tooltip("Invalid email format.");
            tooltipEmail.show(emailTextField, screenXX(emailTextField) + 300, screenYY(emailTextField) + 10);
            System.out.println("error3");
            return false;
        }

        if (!isValidMobile(mobile)) {
            Tooltip tooltipMobile = new Tooltip("Invalid mobile format.");
            tooltipMobile.show(mobileTextField, screenXX(mobileTextField) + 300, screenYY(mobileTextField) + 10);
            System.out.println("error4");
            return false;
        }

        return true;
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

    private void setTooltip(TextField textField, Tooltip tooltip) {
        textField.setTooltip(tooltip);
        tooltip.setAutoHide(true);
        tooltip.show(textField.getScene().getWindow());
    }

    private boolean isValidEmail(String email) {
        email = email.trim();
        if (email.isEmpty()) {
            return false;
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailPattern);
    }

    private boolean isValidMobile(String mobile) {
        return mobile.matches("^(059|056)\\d{7}$");
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
