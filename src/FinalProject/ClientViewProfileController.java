/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FinalProject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
public class ClientViewProfileController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label mobileLabel;
    @FXML
    private Button editButton;
    @FXML
    private ImageView profileImage;

    // Assuming you have a database connection
    Connection connection;
    Statement statement;
    private int userId; // Assuming you have the current user's ID
    @FXML
    private Button logoutButton;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private MenuItem backgroundColorMenuItem;
    @FXML
    private Button dashboardButton;
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
    @FXML
    private VBox rootNode;

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

        // View the client profile
        viewProfile();
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        moveTo(profileImage, "ClientDashboard.fxml", "Client Dashboard Screen");
    }

    @FXML
    private void moveToEdit(ActionEvent event) {
        moveTo(profileImage, "ClientEditProfile.fxml", "Edit Profile Screen");
    }

    public void moveTo(Node node, String fxmlFile, String stageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);
            Stage currentStage = (Stage) node.getScene().getWindow(); //to get the current stage
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

    private void viewProfile() {
        try {
            int clientId = getCurrentClientId();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name, email, mobile, image FROM Users WHERE id = " + clientId);

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String mobile = resultSet.getString("mobile");
                String path = resultSet.getString("image");
                System.out.println(path);

                File imageFile = new File(path);
                String imageUrl = imageFile.toURI().toURL().toString();
                Image image = new Image(imageUrl);
                profileImage.setImage(image);

                nameLabel.setText(name);
                emailLabel.setText(email);
                mobileLabel.setText(mobile);
            } else {
                System.out.println("Client not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (MalformedURLException ex) {
            System.out.println("Brackets error URL" + ex.getMessage());
        }
    }

    private int getCurrentClientId() {
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

    private boolean validateNumericInput(String input) {
        try {
            int value = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void logoutHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "LoginScreen.fxml", "Login Screen", ClientViewProfileController.class);
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
