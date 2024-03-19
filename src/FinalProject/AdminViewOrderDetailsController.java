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
import java.util.ArrayList;
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
public class AdminViewOrderDetailsController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TextField serachForOrderTextField;
    @FXML
    private Button mainBtnStyle;
    @FXML
    private TableView<OrderProduct> tableView;
    @FXML
    private TableColumn<OrderProduct, Integer> orderID_tc;
    @FXML
    private TableColumn<OrderProduct, Integer> quantity_tc;
    @FXML
    private TableColumn<OrderProduct, Integer> productID_tc;
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
    Alert alert;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Adjust column widths
        orderID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        productID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        quantity_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        orderID_tc.setCellValueFactory(new PropertyValueFactory("order_id"));
        productID_tc.setCellValueFactory(new PropertyValueFactory("product_id"));
        quantity_tc.setCellValueFactory(new PropertyValueFactory("quantity"));

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
        ApplyMethods.moveTo(menuBar, "AdminDashboard.fxml", "Admin Dashboard", AdminViewOrderDetailsController.class);
    }

    @FXML
    private void searchForOrderHandle(ActionEvent event) throws SQLException {
        if (validateInput(serachForOrderTextField.getText()) && validateNumericInput(serachForOrderTextField.getText())) {
            String searchOrderID = serachForOrderTextField.getText().trim();
            int orderID = Integer.parseInt(searchOrderID);
            String sql = "SELECT * FROM order_Products WHERE order_id = " + orderID;
            ResultSet rs = statement.executeQuery(sql);
            ArrayList<OrderProduct> ordersList = new ArrayList<>();

            while (rs.next()) {
                OrderProduct orderProduct = new OrderProduct(rs.getInt("order_id"), rs.getInt("product_id"), rs.getInt("quantity"));
                ordersList.add(orderProduct);

            }
            if (ordersList.isEmpty()) {
                ApplyMethods.showErrorAlert("Error", "Alert, No such order found!", "Please enter valid ID number and try again.");
            } else {
                tableView.getItems().setAll(ordersList);
            }
        }
    }


    @FXML
    private void resetButtonHandle(ActionEvent event) {
        serachForOrderTextField.setText("");
        showOrders();
    }

    public void showOrders() {
        try {
            String sql = "SELECT * FROM order_Products";
            ResultSet rs = statement.executeQuery(sql);
            ArrayList<OrderProduct> ordersList = new ArrayList<>();

            while (rs.next()) {
                OrderProduct ordersproducts = new OrderProduct(rs.getInt("order_id"), rs.getInt("product_id"), rs.getInt("quantity"));
                ordersList.add(ordersproducts);
            }

            tableView.getItems().setAll(ordersList);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }


    private boolean validateInput(String input) {
        if (!input.isBlank()) {
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

    private boolean validateNumericInput(String input) {
        try {
            int value = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException ex) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alert, empty values!");
            alert.setHeaderText("Invalid Fields!");
            alert.setContentText("Please check on your entries and make sure you enetered a number in level text field!");
            alert.show();
            return false;
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
