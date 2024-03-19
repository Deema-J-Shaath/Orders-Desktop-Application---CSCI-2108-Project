/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FinalProject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class AdminManageOrdersController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private Button dashboardButton;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button mainBtnStyle;
    @FXML
    private TableView<Orders> tableView;
    @FXML
    private TableColumn<Orders, Integer> clientID_tc;
    @FXML
    private TableColumn<Orders, Integer> orderID_tc;
    @FXML
    private TableColumn<Orders, Date> orderDate_tc;

    Statement statement;
    Connection connection;
    Alert alert;
    @FXML
    private TextField clientNameSearchtTextField;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private MenuItem backgroundColorMenuItem;
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
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Adjust column widths
        clientID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        orderID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        orderDate_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        clientID_tc.setCellValueFactory(new PropertyValueFactory("user_id"));
        orderID_tc.setCellValueFactory(new PropertyValueFactory("id"));
        orderDate_tc.setCellValueFactory(new PropertyValueFactory("date"));

        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        showOrders();
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "AdminDashboard.fxml", "Admin Dashboard", AdminManageOrdersController.class);
    }

    @FXML
    private void resetButtonHandle(ActionEvent event) {
        clientNameSearchtTextField.setText("");
        showOrders();
    }

    @FXML
    private void orderDetailsHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "AdminViewOrderDetails.fxml", "Admin View Order Details", AdminManageOrdersController.class);
    }

    @FXML
    private void searchForOrderHandle(ActionEvent event) {
        try {

            if (ApplyMethods.validateInput(clientNameSearchtTextField.getText()) && ApplyMethods.validateNumericInput(clientNameSearchtTextField.getText())) {
                int clientId = Integer.parseInt(clientNameSearchtTextField.getText().trim());
                String sql = "SELECT * FROM orders o where o.user_id = " + clientId;
                ResultSet rs = statement.executeQuery(sql);
                ArrayList<Orders> ordersList = new ArrayList<>();
                LocalDate orderDate = null;
                while (rs.next()) {
                    String orderDateStr = rs.getString("date");
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Convert the string date to a Date object
                    try {
                        orderDate = LocalDate.parse(orderDateStr, dateFormatter);
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    Orders order = new Orders(rs.getInt("id"), rs.getInt("user_id"), orderDate);
                    ordersList.add(order);
                }
                if (ordersList.isEmpty()) {
                    ApplyMethods.showErrorAlert("Error", "No order found!", "Client with ID " + clientId + " didn't order anything from TechZone ;(/");
                } else {
                    tableView.getItems().setAll(ordersList);
                }
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void showOrders() {
        try {
            String sql = "select * from Orders";
            ResultSet rs = statement.executeQuery(sql);
            ArrayList<Orders> ordersList = new ArrayList<>();
            while (rs.next()) {
                LocalDate orderDate = null;
                String orderDateStr = rs.getString("date");
                // Convert the string date to a Date object
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                try {
                    orderDate = LocalDate.parse(orderDateStr, dateFormatter);
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
                // Use the retrieved date value as needed
                System.out.println("Order Date: " + orderDate);

                Orders users = new Orders(rs.getInt("id"), rs.getInt("user_id"), orderDate);
                ordersList.add(users);
            }
            tableView.getItems().setAll(ordersList);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
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
