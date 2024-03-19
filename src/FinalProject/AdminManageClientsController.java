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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author deemashaath
 */
public class AdminManageClientsController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TableView<Users> tableView;
    @FXML
    private Button mainBtnStyle;
    @FXML
    private TextField serachForClientTextField;
    @FXML
    private TableColumn<Users, Integer> clientID_tc;
    @FXML
    private TableColumn<Users, Integer> name_tc;
    @FXML
    private TableColumn<Users, Integer> email_tc;
    @FXML
    private TableColumn<Users, Integer> mobile_tc;
    @FXML
    private TableColumn<Users, Integer> password_tc;
    @FXML
    private TableColumn<Users, Integer> image_tc;

    Statement statement;
    Connection connection;
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
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Adjust column widths
        clientID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        name_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        email_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        mobile_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        password_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        image_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        clientID_tc.setCellValueFactory(new PropertyValueFactory("id"));
        name_tc.setCellValueFactory(new PropertyValueFactory("name"));
        email_tc.setCellValueFactory(new PropertyValueFactory("email"));
        mobile_tc.setCellValueFactory(new PropertyValueFactory("mobile"));
        password_tc.setCellValueFactory(new PropertyValueFactory("password"));
        image_tc.setCellValueFactory(new PropertyValueFactory("image"));

        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        showClients();
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "AdminDashboard.fxml", "Admin Dashboard", AdminManageClientsController.class);
    }

    @FXML
    private void deleteButtonHandle(ActionEvent event) {
        try {
            Users client = tableView.getSelectionModel().getSelectedItem();
            int clientId = client.getId();

            String deleteInvoices = "DELETE FROM invoices WHERE order_id IN( SELECT id FROM orders WHERE user_id =" + clientId + ")";
            int deletedInvoices = statement.executeUpdate(deleteInvoices);
            System.out.println("Deleted associated Invoices rows: " + deletedInvoices);

            String deleteOrderProductsSql = "DELETE FROM order_products WHERE order_id IN (SELECT id FROM orders WHERE user_id = " + clientId + ")";
            int deletedOrderProducts = statement.executeUpdate(deleteOrderProductsSql);
            System.out.println("Deleted associated order_products rows: " + deletedOrderProducts);

            String deleteOrdersSql = "DELETE FROM orders WHERE user_id = " + clientId; // Delete associated orders
            int deletedOrders = statement.executeUpdate(deleteOrdersSql);
            System.out.println("Deleted associated orders: " + deletedOrders);

            // Delete the client from Users table
            String deleteClientSql = "DELETE FROM Users WHERE id = " + clientId;
            int affectedRows = statement.executeUpdate(deleteClientSql);
            if (affectedRows > 0) {
                ApplyMethods.showInfoAlert("Success", "Deleted successfully!", "You have just deleted client " + clientId + " successfully.");
            } else {
                ApplyMethods.showErrorAlert("Error", "Failed to complete the operaion!", "Failed to delete the client " + clientId + ". Please try again.");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void resetButtonHandle(ActionEvent event) {
        serachForClientTextField.setText("");
        showClients();
    }

    @FXML
    private void searchForClientHandle(ActionEvent event) {
        try {

            if (ApplyMethods.validateInput(serachForClientTextField.getText())) {
                String clientName = serachForClientTextField.getText().trim();
                String sql = "SELECT * FROM users u WHERE u.role='client' and u.name = '" + clientName + "'";
                ResultSet rs = statement.executeQuery(sql);
                ArrayList<Users> usersList = new ArrayList<>();
                if (rs.next()) {
                    Users order = new Users(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("mobile"), rs.getString("password"), rs.getString("role"), rs.getString("image"));
                    usersList.add(order);

                } else {
                    ApplyMethods.showErrorAlert("Error", "Alert, No such client found!", "Please enter valid ID number and try again.");
                }
                if (usersList.isEmpty()) {
                    ApplyMethods.showErrorAlert("Error", "Unable to find client", "There's no client with mentioned name");
                } else {
                    tableView.getItems().setAll(usersList);
                }
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void showClients() {
        try {
            String sql = "select * from Users where role='client'";
            ResultSet rs = statement.executeQuery(sql);
            ArrayList<Users> stdArrList = new ArrayList<>();
            while (rs.next()) {
                Users users = new Users(rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                        rs.getString("mobile"), rs.getString("password"), rs.getString("role"), rs.getString("image"));
                stdArrList.add(users);
            }
            tableView.getItems().setAll(stdArrList);
        } catch (SQLException ex) {
            Logger.getLogger(AdminManageClientsController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
