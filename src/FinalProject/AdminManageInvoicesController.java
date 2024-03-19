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
import java.util.ResourceBundle;
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
public class AdminManageInvoicesController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button resetButton;
    @FXML
    private TextField invoiceIDSearchTextField;
    @FXML
    private Button mainBtnStyle;
    @FXML
    private TableView<Invoices> tableView;
    @FXML
    private TableColumn<Invoices, Integer> invoiceID_tc;
    @FXML
    private TableColumn<Invoices, Double> totalPrice_tc;
    @FXML
    private TableColumn<Invoices, LocalDate> orderDate_tc;
    @FXML
    private TableColumn<Invoices, Integer> orderID_tc;

    Connection connection;
    Statement statement;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "AdminDashboard.fxml", "Admin Dahsboard", AdminManageInvoicesController.class);
    }

    @FXML
    private void resetHAndle(ActionEvent event) {
        invoiceIDSearchTextField.setText("");
        viewInvoices();
    }

    @FXML
    private void searchForInvoiceByIDHandle(ActionEvent event) {
        try {
            if (ApplyMethods.validateInput(invoiceIDSearchTextField.getText()) && ApplyMethods.validateNumericInput(invoiceIDSearchTextField.getText())) {
                String searchInvoiceID = invoiceIDSearchTextField.getText().trim();
                int invoiceID = Integer.parseInt(searchInvoiceID);
                String sql = "SELECT * FROM Invoices where id=" + invoiceID;
                ResultSet rs = statement.executeQuery(sql);
                ArrayList<Invoices> invoicesList = new ArrayList<>();
                LocalDate orderDate = null;
                if (rs.next()) {
                    String orderDateStr = rs.getString("date");
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Convert the string date to a Date object
                    try {
                        orderDate = LocalDate.parse(orderDateStr, dateFormatter);
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    Invoices invoice = new Invoices(rs.getInt("id"), rs.getInt("order_id"), rs.getDouble("total_price"), orderDate);
                    invoicesList.add(invoice);
                    tableView.getItems().setAll(invoicesList);
                } else {
                    ApplyMethods.showErrorAlert("Error", "Alert, No such invoice found!", "Please enter valid ID number and try again.");
                }
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void generateInvoicesHandle(ActionEvent event) {
        try {
            String selectOrdersQuery = "SELECT o.id, o.user_id, o.date, SUM(op.quantity * p.price) AS total_price\n"
                                     + "FROM orders o\n"
                                     + "JOIN order_products op ON o.id = op.order_id\n"
                                     + "JOIN products p ON op.product_id = p.id\n"
                                     + "LEFT JOIN invoices i ON o.id = i.order_id\n"
                                     + "WHERE i.order_id IS NULL\n"
                                     + "GROUP BY o.id";

            ResultSet rs = statement.executeQuery(selectOrdersQuery);

            ArrayList<Orders> ordersList = new ArrayList<>();
            LocalDate orderDate = null;

            while (rs.next()) {
                int orderId = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String orderDateStr = rs.getString("date");
                double totalPrice = rs.getDouble("total_price");

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                orderDate = LocalDate.parse(orderDateStr, dateFormatter);

                Orders order = new Orders(orderId, userId, orderDate);
                OrderProduct orderProduct = new OrderProduct(orderId, totalPrice);

                ordersList.add(order);

                // Check if invoice with the order_id already exists
                String checkInvoiceSql = "SELECT * FROM invoices WHERE order_id=" + orderId;
                ResultSet invoiceRs = statement.executeQuery(checkInvoiceSql);
                if (invoiceRs.next()) {
                    // Update the existing invoice with the updated total price
                    String updateSql = "UPDATE invoices SET total_price=" + totalPrice + " WHERE order_id=" + orderId;
                    statement.executeUpdate(updateSql);
                } else {
                    // Insert a new invoice
                    String insertSql = "INSERT INTO invoices (order_id, date, total_price) VALUES (" + orderId + ", '" + orderDateStr + "', " + totalPrice + ")";
                    statement.executeUpdate(insertSql);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void deleteInvoicesHandle(ActionEvent event) {
        if (ApplyMethods.validateInput(invoiceIDSearchTextField.getText()) && ApplyMethods.validateNumericInput(invoiceIDSearchTextField.getText())) {
            try {
                int invoiceId = Integer.parseInt(invoiceIDSearchTextField.getText());
                String sql = "Delete from invoices where id=" + invoiceId;
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    ApplyMethods.showInfoAlert("Success", "Deleted Invoice successfully", "You have just deleted invoice " + invoiceId + " successfully!");
                } else {
                    ApplyMethods.showErrorAlert("Error", "Cannot complete this operation!", "An error occurred during deletion. Try again!");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    private void viewInvoicesHandle(ActionEvent event) {
        viewInvoices();
    }

    public void viewInvoices() {
        try {
            String sql = "Select * from invoices";
            ResultSet rsss = statement.executeQuery(sql);
            ArrayList<Invoices> invoicesList = new ArrayList<>();
            LocalDate orderDate = null;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while (rsss.next()) {
                int id = rsss.getInt("id");
                double total_price = rsss.getDouble("total_price");
                int order_id = rsss.getInt("order_id");
                String orderDateStr = rsss.getString("date");
                orderDate = LocalDate.parse(orderDateStr, dateFormatter);
                Invoices invoice = new Invoices(id, order_id, total_price, orderDate);
                invoicesList.add(invoice);
            }
            if (invoicesList.isEmpty()) {
                ApplyMethods.showErrorAlert("Error", "No invoices to show!", "You didn't order anthing from our system.");
            } else {
                tableView.getItems().setAll(invoicesList);
            }
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
