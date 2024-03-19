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
import javafx.collections.FXCollections;
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

public class ClientViewOrdersController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;

    @FXML
    private TableView<Orders> ordersTable;
    @FXML
    private TableColumn<Orders, Integer> orderID_tc;
    @FXML
    private TableColumn<Orders, Integer> clientID_tc;
    @FXML
    private TableColumn<Orders, LocalDate> orderDate_tc;
    @FXML
    private Button mainBtnStyle;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private MenuItem backgroundColorMenuItem;
    @FXML
    private VBox rootNode;
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

    private Connection connection;
    private Statement statement;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            // Adjust column widths
            orderID_tc.prefWidthProperty().bind(ordersTable.widthProperty().multiply(0.3));
            clientID_tc.prefWidthProperty().bind(ordersTable.widthProperty().multiply(0.3));
            orderDate_tc.prefWidthProperty().bind(ordersTable.widthProperty().multiply(0.3));
            mainBtnStyle.prefWidthProperty().bind(ordersTable.widthProperty().multiply(0.1));

            clientID_tc.setCellValueFactory(new PropertyValueFactory("user_id"));
            orderID_tc.setCellValueFactory(new PropertyValueFactory("id"));
            orderDate_tc.setCellValueFactory(new PropertyValueFactory("date"));

            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
            viewOrders();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientDashboard.fxml", "Client Dashboard", ClientViewOrdersController.class);
    }

    private void viewOrders() {
        List<Orders> orders = new ArrayList<>();

        try {
            // Retrieve the client ID of the logged-in user
            int clientId = ApplyMethods.getCurrentClientId();

            // Fetch the orders for the client from the database
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
                ApplyMethods.showErrorAlert("Error", "No orders to show!", "You didn't order anything from our system.");
            } else {
                ordersTable.getItems().setAll(orders);
            }

        } catch (SQLException e) {
            ApplyMethods.showErrorAlert("Error", "Database Error", "Failed to retrieve orders from the database.");
            e.printStackTrace();
        }

    }

    @FXML
    public void goToClientViewOrderDetails() {
        ApplyMethods.moveTo(menuBar, "ClientViewOrderDetails.fxml", "Orders Details", ClientViewOrdersController.class);
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
