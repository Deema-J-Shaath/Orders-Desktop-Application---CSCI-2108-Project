package FinalProject;

import java.io.IOException;
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
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ClientDeleteOrderController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button editButton;
    @FXML
    private TableView<Orders> tableView;
    @FXML
    private TableColumn<Orders, Integer> orderID_tc;
    @FXML
    private TableColumn<Orders, String> orderdate_tc;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private MenuItem backgroundColorMenuItem;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private CheckBox selectMultipleCheckox;
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
    @FXML
    private Button resetButton;
    @FXML
    private Button dashboardButton;

    Connection connection;
    Statement statement;

    int clientId = ApplyMethods.getCurrentClientId();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        orderdate_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        // Set up cell value factories for the table columns
        orderID_tc.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderdate_tc.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Enable multiple selection in the table view
//        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        viewOrders();
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientDashboard.fxml", "Client Dashboard", ClientDeleteOrderController.class);
    }

    @FXML
    private void deleteHandle(ActionEvent event) {
        ObservableList<Orders> selectedOrders = tableView.getSelectionModel().getSelectedItems();
        if (!selectedOrders.isEmpty()) {
            deleteOrders(selectedOrders);
            tableView.getItems().removeAll(selectedOrders);
        }
    }

    private void deleteOrders(List<Orders> orders) {

        try {
            int orderId = 0;
            int affectedRows = 0;
            int affectedRows2 = 0;
            for (Orders order : orders) {
                orderId = order.getId();

                String deleteOrderProductsQuery = "DELETE FROM order_products WHERE order_id = " + orderId;
                affectedRows = statement.executeUpdate(deleteOrderProductsQuery);

                String deleteOrdersQuery = "DELETE FROM orders WHERE id = " + orderId;
                affectedRows2 = statement.executeUpdate(deleteOrdersQuery);
                System.out.println("Deleted " + orderId);
            }
            if (affectedRows > 0 && affectedRows2 > 0) {
                ApplyMethods.showInfoAlert("success", "Deleted successfully!", "You have just deleted order " + orderId);
            } else {
                ApplyMethods.showErrorAlert("Error", "Cannot complete this operation!", "Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting orders: " + e.getMessage());
        }
    }

    @FXML
    private void multipleSelectionhandle(ActionEvent event) {
        boolean isMultipleSelectionEnabled = selectMultipleCheckox.isSelected();
        if (isMultipleSelectionEnabled) {
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } else {
            tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
    }

    private void viewOrders() {
        List<Orders> orders = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Orders WHERE user_id = " + clientId;
            ResultSet rs = statement.executeQuery(sql);
            LocalDate orderDate = null;

            while (rs.next()) {

                String orderDateStr = rs.getString("date");
                // Convert the string date to a Date object
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                try {
                    orderDate = LocalDate.parse(orderDateStr, dateFormatter);
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
                Orders order = new Orders(rs.getInt("id"), rs.getInt("user_id"), orderDate);
                orders.add(order);
            }
            if (orders.isEmpty()) {
                ApplyMethods.showErrorAlert("Error", "No products to show!", "");
            } else {
                tableView.getItems().setAll(orders);
            }
        } catch (SQLException e) {
            ApplyMethods.showErrorAlert("Error", "Database Error", "Failed to retrieve orders from the database.");
            e.printStackTrace();
        }
    }

    @FXML
    private void resetHandle(ActionEvent event) {
        tableView.getSelectionModel().clearSelection();
        viewOrders();
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
