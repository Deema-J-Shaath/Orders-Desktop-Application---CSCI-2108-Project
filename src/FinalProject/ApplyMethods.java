/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FinalProject;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author deemashaath
 */
public class ApplyMethods {

    static Alert alert;

    public static void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void showInfoAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void moveTo(Node node, String fxmlFile, String stageTitle, Class<?> clazz) {
        try {
            FXMLLoader loader = new FXMLLoader(clazz.getResource(fxmlFile));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);
            Stage currentStage = (Stage) node.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle(stageTitle);
            // Get the dimensions of the screen
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            // Set the stage dimensions to match the screen size
            currentStage.setX(bounds.getMinX());
            currentStage.setY(bounds.getMinY());
            currentStage.setWidth(bounds.getWidth());
            currentStage.setHeight(bounds.getHeight());
            currentStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int getCurrentClientId() {
        Users loggedInUser = ClientSession.getCurrentUser();
        if (loggedInUser != null) {
            int clientId = loggedInUser.getId();
            if (validateNumericInput(String.valueOf(clientId))) {
                return clientId;
            } else {
                System.out.println("Client ID not found for the current user");
            }
        } else {
            System.out.println("No logged-in user found");
        }
        return 0;
    }

    public static boolean validateNumericInput(String input) {
        try {
            int value = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String isStrongPassword(String password) {
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        String errMessage = "";

        if (password.length() < 8) {
            errMessage = "Your password must be at least 8 characters long.";
            return errMessage;
        }
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
                continue;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
                continue;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
                continue;
            } else {
                hasSpecialChar = true;
                continue;
            }
        }
        if (hasUppercase == false || hasLowercase == false || hasDigit == false || hasSpecialChar == false) {
            errMessage = "Your password must ";
            if (hasUppercase == false || hasLowercase == false) {
                errMessage += "include both lower and upper case characters. ";
            }
            if (hasDigit == false || hasSpecialChar == false) {
                errMessage += "include at least one number or symbol";
            }
            return errMessage;
        }
        return errMessage;
    }

    public static boolean validateInput(String input) {

        if (input != null && !input.isEmpty()) {
            return true;
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alert, empty values!");
            alert.setHeaderText("Empty Fields!");
            alert.setContentText("Please check on your entries and make sure you filled all fields!");
            alert.show();
            return false;
        }
    }

    public static boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Alert, invalid value!");
            alert.setContentText("Please check on your entries and make sure you filled all fields with valid values!");
            alert.show();
            return false;
        }
    }

    public static void informationAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }
}
