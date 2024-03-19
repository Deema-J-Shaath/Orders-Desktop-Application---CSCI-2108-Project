/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FinalProject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * FXML Controller class
 *
 * @author deemashaath
 */
public class ClientDashboardController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private Button logoutButton;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem backgroundColorMenuItem;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private VBox rootNode;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private MenuItem size22;
    @FXML
    private MenuItem size14;
    @FXML
    private MenuItem size20;
    @FXML
    private MenuItem size18;

    //Parent root = primaryStage.getScene().getRoot();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    @FXML
    private void logoutHandle(ActionEvent event) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Logout");
        confirmationDialog.setContentText("Are you sure you want to logout?");

        ButtonType cancelButton = new ButtonType("Cancel"); // to customize the dialog buttons
        ButtonType confirmButton = new ButtonType("Confirm");
        confirmationDialog.getButtonTypes().setAll(cancelButton, confirmButton);

        confirmationDialog.showAndWait().ifPresent(buttonType -> { // to handle the user's choice of buttons
            if (buttonType == confirmButton) {
                ApplyMethods.moveTo(menuBar, "LoginScreen.fxml", "Login Screen", RegisterScreenController.class);
            }
        });
    }

    @FXML
    private void profileHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientProfile.fxml", "Client Profile", ClientDashboardController.class);
    }

    @FXML
    private void manageOrdersHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientManageOrders.fxml", "Manage Orders", ClientDashboardController.class);
    }

    @FXML
    private void viewInvoicesHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientViewInvoices.fxml", "View Invoices", ClientDashboardController.class);
    }

    @FXML
    private void changePasswordHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientChangePassword.fxml", "Change Password", ClientDashboardController.class);
    }

    @FXML
    private void exitMenuItemHandle(ActionEvent event) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Logout");
        confirmationDialog.setContentText("Are you sure you want to exit?");

        ButtonType cancelButton = new ButtonType("Cancel"); // to customize the dialog buttons
        ButtonType confirmButton = new ButtonType("Confirm");
        confirmationDialog.getButtonTypes().setAll(cancelButton, confirmButton);

        confirmationDialog.showAndWait().ifPresent(buttonType -> { // to handle the user's choice of buttons
            if (buttonType == confirmButton) {
                Platform.exit();
            }
        });
    }

    @FXML
    private void helpmenuItem(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About App");
        alert.setHeaderText(null);
        alert.setContentText("TechZone is an order system application designed for a tech store. It provides a\n"
                + "user-friendly interface for both admins and clients to manage products, orders,\n"
                + "clients, and invoices efficiently. The application includes various features and\n"
                + "validations to enhance the user experience.");
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
