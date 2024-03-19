/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FinalProject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author deemashaath
 */
public class ClientManageOrdersController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private MenuItem backgroundColorMenuItem;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private VBox rootNode;
    @FXML
    private Button dashboardButton;
    @FXML
    private MenuItem size22;
    @FXML
    private MenuItem size14;
    @FXML
    private MenuItem size20;
    @FXML
    private MenuItem size18;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        moveTo(menuBar, "ClientDashboard.fxml", "Dashboard Screen");
    }

    @FXML
    private void addOrderScreenHandle(ActionEvent event) {
        moveTo(menuBar, "ClientAddOrder.fxml", "Add Order Screen");
    }

    @FXML
    private void editOrderScreenHandle(ActionEvent event) {
        moveTo(menuBar, "ClientEditOrder.fxml", "Edit Order Screen");
    }

    @FXML
    private void deleteOrderScreenHandle(ActionEvent event) {
        moveTo(menuBar, "ClientDeleteOrder.fxml", "Delete Order Screen");
    }

    @FXML
    private void viewOrderScreenHandle(ActionEvent event) {
        moveTo(menuBar, "ClientViewOrders.fxml", "View Orders Screen");
    }

    @FXML
    private void searchForOrderScreenHandle(ActionEvent event) {
        moveTo(menuBar, "ClientSearchForOrder.fxml", "Search For Order Screen");
    }

    public void moveTo(Node node, String fxmlFile, String stageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);
            Stage currentStage = (Stage) node.getScene().getWindow(); //to get the currrent stage
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

    @FXML
    private void logoutHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "LoginScreen.fxml", "Login Screen", ClientManageOrdersController.class);
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
