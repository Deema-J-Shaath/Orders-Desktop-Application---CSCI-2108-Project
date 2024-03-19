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
public class ClientViewOrderDetailsController implements Initializable {

    @FXML
    private VBox rootNode;
    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private Button dashboardButton;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private MenuItem size22;
    @FXML
    private MenuItem size14;
    @FXML
    private MenuItem size20;
    @FXML
    private MenuItem size18;
    @FXML
    private MenuItem backgroundColorMenuItem;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private TableView<OrderProduct> ordersTable;
    @FXML
    private TableColumn<OrderProduct, Integer> orderID_tc;
    @FXML
    private TableColumn<OrderProduct, Integer> productID_tc;
    @FXML
    private TableColumn<OrderProduct, Integer> quantity_tc;

    Connection connection;
    Statement statement;

    int clientId = ApplyMethods.getCurrentClientId();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(ClientViewOrderDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Adjust column widths
        orderID_tc.prefWidthProperty().bind(ordersTable.widthProperty().multiply(0.25));
        productID_tc.prefWidthProperty().bind(ordersTable.widthProperty().multiply(0.25));
        quantity_tc.prefWidthProperty().bind(ordersTable.widthProperty().multiply(0.25));

        orderID_tc.setCellValueFactory(new PropertyValueFactory("order_id"));
        productID_tc.setCellValueFactory(new PropertyValueFactory("product_id"));
        quantity_tc.setCellValueFactory(new PropertyValueFactory("quantity"));

        showOrderDetails();
    }

    public void showOrderDetails() {
        try {
            String sql = "Select * from order_products where order_id in(select order_id from orders where user_id=" + clientId + ")";
            ResultSet rs = statement.executeQuery(sql);
            List<OrderProduct> opList = new ArrayList<>();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                OrderProduct op = new OrderProduct(orderId, productId, quantity);
                opList.add(op);
            }
            if (opList.size() > 0) {
                ordersTable.getItems().setAll(opList);
            } else {
                ApplyMethods.showErrorAlert("Error", "No orders to show!", "You have not order anything from our store.");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientDashboard.fxml", "Client Dashboard", ClientViewOrderDetailsController.class);
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
