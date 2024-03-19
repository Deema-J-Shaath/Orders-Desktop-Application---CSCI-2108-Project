/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FinalProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author deemashaath
 */
public class RegisterScreenController implements Initializable {
    
    @FXML
    private VBox card;
    @FXML
    private Label registrationErrLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField mobileTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button submitBtn;
    @FXML
    private Tooltip mobileTooltip;
    @FXML
    private Tooltip nameTooltip;
    @FXML
    private Tooltip emailTooltip;
    @FXML
    private Tooltip passwordTooltip;
    @FXML
    private Button uploadImageChooser;    
    
    Connection connection;
    Statement statement;
    File selectedFile;

    /**
     * Initializes the controller class.
     */
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
    private void registerHandle(ActionEvent events) {
        // Get the input values from the text fields
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String mobile = mobileTextField.getText();
        String password = passwordTextField.getText();
        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
            registrationErrLabel.setText("Please fill in all the fields.");
            if (name.isEmpty()) {
                nameTooltip.show(nameTextField, screenXX(nameTextField) + 300, screenYY(nameTextField) + 10);
            }
            if (email.isEmpty()) {
                emailTooltip.show(emailTextField, screenXX(emailTextField) + 300, screenYY(emailTextField) + 10);
            }
            if (mobile.isEmpty()) {
                mobileTooltip.show(emailTextField, screenXX(mobileTextField) + 300, screenYY(mobileTextField) + 10);
            }
            if (password.isEmpty()) {
                passwordTooltip.show(passwordTextField, screenXX(passwordTextField) + 300, screenYY(passwordTextField) + 10);
            }
            return;
        }
        if (validateEmail(email) == false) {
            registrationErrLabel.setText("Invalid email address!");
            emailTooltip.setText("Invalid email format");
            emailTooltip.show(emailTextField, screenXX(emailTextField) + 300, screenYY(emailTextField) + 10);
            return;
        }
        String passwordError = ApplyMethods.isStrongPassword(password);
        if (!passwordError.isEmpty()) {
            registrationErrLabel.setText(passwordError);
            return;
        }
        
        if (!validateMobileNumber(mobile)) {
            registrationErrLabel.setText("Invalid mobile number! Please double-check.");
            return;
        }
        try {
            String imageFilePath = uploadImageFileChooser(events);
            if (imageFilePath == null && imageFilePath.isEmpty()) {
                ApplyMethods.showErrorAlert("Error", "Please choose an image!", " ");
                return;
            }
            String query = "INSERT INTO users (name, email, mobile, password, role, image) VALUES ('" + name + "', '" + email + "', '" + mobile + "', '" + password + "', 'client','" + imageFilePath + "')";
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(query);
            
            if (affectedRows >= 1) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientDashboard.fxml"));
                Parent loginRoot = loader.load();
                Scene loginScene = new Scene(loginRoot);
                Stage currentStage = (Stage) emailTextField.getScene().getWindow(); //to get the currrent stage
                currentStage.setScene(loginScene);
                currentStage.setTitle("Client Dashboard");
                // Get the dimensions of the screen
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();

                // Set the stage dimensions to match the screen size
                currentStage.setX(bounds.getMinX());
                currentStage.setY(bounds.getMinY());
                currentStage.setWidth(bounds.getWidth());
                currentStage.setHeight(bounds.getHeight());
                System.out.println("7");
                currentStage.show();
            }
            
            registrationErrLabel.setText("Registration successful!");
            registrationErrLabel.setTextFill(Color.GREEN);
        } catch (SQLException e) {
            registrationErrLabel.setText("Error occurred during registration.");
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void returnToLogin(MouseEvent event) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Skip Registration");
        confirmationDialog.setContentText("Are you sure you want to skip registration and go back to the login screen?");
        
        ButtonType cancelButton = new ButtonType("Cancel"); // to customize the dialog buttons
        ButtonType confirmButton = new ButtonType("Confirm");
        confirmationDialog.getButtonTypes().setAll(cancelButton, confirmButton);
        
        confirmationDialog.showAndWait().ifPresent(buttonType -> { // to handle the user's choice of buttons
            if (buttonType == confirmButton) {
                    ApplyMethods.moveTo(card, "LoginScreen.fxml", "Login Screen", RegisterScreenController.class);
            }
        });
    }
    
    private boolean validateEmail(String email) {
        try {
            if (email == null || email.isEmpty()) {
                return false;
            }
            String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!email.matches(emailPattern)) {
                return false;
            }
            if (email.startsWith(".") || email.endsWith(".")
                    || email.startsWith("@") || email.endsWith("@") || email.equals("@")
                    || email.startsWith("_") || email.endsWith("_")) {
                return false;
            }
            String duplicateEmail = "Select email from users";
            ResultSet rss = statement.executeQuery(duplicateEmail);
            while (rss.next()) {
                if (email.equals(rss.getString("email"))) {
                    return false;
                }
            }
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }
    
    private boolean validateMobileNumber(String mobile) {
        boolean hasCharacter = false;
        if (mobile.length() >= 4) {
            for (char c : mobile.toCharArray()) {
                if (!Character.isDigit(c)) {
                    hasCharacter = true;
                    return false;
                }
            }
        } else {
            return false;
        }
        return true; //Note that minimum number of digits for phone number accross the world is 4 numbers and is rare.
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
    private String uploadImageFileChooser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        ExtensionFilter imageFilter = new ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif"); // Create an extension filter for image files
        fileChooser.getExtensionFilters().add(imageFilter); // Add the image filter to the file chooser
        selectedFile = fileChooser.showOpenDialog(null); // Show the file chooser dialog and get the selected file
        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath()); // Process the selected file
            return selectedFile.getAbsolutePath();
        } else {
            System.out.println("No file selected.");
            ApplyMethods.showErrorAlert("Error", "No file selected!", "Please choose a beautiful profile image.");
        }
        return " ";
    }
    
    @FXML
    private void resetButton(ActionEvent event) {
        selectedFile = null;
        registrationErrLabel.setText("");
        nameTextField.setText("");
        emailTextField.setText("");
        mobileTextField.setText("");
        passwordTextField.setText("");
    }
}
