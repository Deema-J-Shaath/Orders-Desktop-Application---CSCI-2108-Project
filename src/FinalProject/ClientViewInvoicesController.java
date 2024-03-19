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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.CheckBox;
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
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author deemashaath
 */
public class ClientViewInvoicesController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TableView<Invoices> tableView;
    @FXML
    private TableColumn<Invoices, Integer> invoiceID_tc;
    @FXML
    private TableColumn<Invoices, Double> totalPrice_tc;
    @FXML
    private TableColumn<Invoices, LocalDate> orderDate_tc;
    @FXML
    private TableColumn<?, ?> orderID_tc;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private ColorPicker colorPicker;
    private MenuItem size22;
    private MenuItem size14;
    private MenuItem size20;
    private MenuItem size18;
    private VBox rootNode;

    Connection connection;
    Statement statement;

    int clientId = ApplyMethods.getCurrentClientId();
    @FXML
    private MenuItem backgroundColorMenuItem;
    @FXML
    private Button dashboardButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // TODO
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Adjust column widths
        orderID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        orderDate_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        invoiceID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        totalPrice_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        orderID_tc.setCellValueFactory(new PropertyValueFactory("order_id"));
        invoiceID_tc.setCellValueFactory(new PropertyValueFactory("id"));
        orderDate_tc.setCellValueFactory(new PropertyValueFactory("date"));
        totalPrice_tc.setCellValueFactory(new PropertyValueFactory("total_price"));

        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        viewInvoices();

    }

    public void viewInvoices() {
        try {
            String sql = "SELECT * FROM invoices WHERE order_id in (select id from orders where user_id="+clientId+")";
            ResultSet rs = statement.executeQuery(sql);
            ArrayList<Invoices> invoicesList = new ArrayList<>();
            LocalDate orderDate = null;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while (rs.next()) {
                int id = rs.getInt("id");
                double total_price = rs.getDouble("total_price");
                int order_id = rs.getInt("order_id");
                String orderDateStr = rs.getString("date");
                orderDate = LocalDate.parse(orderDateStr, dateFormatter);

                Invoices invoice = new Invoices(id, order_id, total_price, orderDate);
                invoicesList.add(invoice);
            }
            if (invoicesList.isEmpty()) {
                ApplyMethods.showErrorAlert("Error", "No invoices to show!", "You didn't order anything from our system.");
            } else {
                tableView.getItems().addAll(invoicesList);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientDashboard.fxml", "Dashboard Screen", ClientViewInvoicesController.class);
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
