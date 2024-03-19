package FinalProject;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ClientEditOrderController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ComboBox<String> productComboBox;
    @FXML
    private TextField quantityTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button applyEdits;
    @FXML
    private Button resetButton;
    @FXML
    private TextField orderIDtextField;

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

    int clientId = ApplyMethods.getCurrentClientId();
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
        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        getOrderedProducts(clientId);
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientDashboard.fxml", "Client Dashboard", ClientEditOrderController.class);
    }

    @FXML
    private void applyEditsButton(ActionEvent event) {
        if (productComboBox.getValue() == null || productComboBox.getValue().isEmpty() || quantityTextField.getText().isEmpty() || datePicker.getValue().toString().isEmpty()) {
            ApplyMethods.showErrorAlert("Error", "Incomplete Form", "Please fill in all the fields.");
            return;
        }

        String product = productComboBox.getValue();
        String quantityText = quantityTextField.getText();
        String orderDate = datePicker.getValue().toString();

        if (!ApplyMethods.validateNumericInput(quantityText)) {
            ApplyMethods.showErrorAlert("Error", "Invalid Quantity", "Please enter a valid numeric quantity.");
            return;
        }

        int quantity = Integer.parseInt(quantityText);
        if (quantity != 1) {
            ApplyMethods.showErrorAlert("Error", "Invalid Quantity", "Quantity must be 1.");
            return;
        }

        int orderId = Integer.parseInt(orderIDtextField.getText());
        int clientId = ApplyMethods.getCurrentClientId();

        int productId = getProductId(productComboBox.getValue());

        try {
            String updateSql1 = "select id from orders where id=" + orderId; // search for orders
            ResultSet resultSetDuplicatesOrderId = statement.executeQuery(updateSql1);
            if (resultSetDuplicatesOrderId.next()) {
                String updateOrders = "update orders set date='" + orderDate + "'";
                String updateQuery = "UPDATE Orders o, Order_Products op "
                        + "SET op.quantity = " + quantity + ", o.date = '" + orderDate + "' "
                        + "WHERE o.id = " + orderId + " AND o.user_id = " + clientId + " AND op.order_id = o.id AND op.product_id = " + productId;
                statement.executeUpdate(updateQuery);

                // Show success message
                ApplyMethods.showInfoAlert("Success", "Order Updated", "The order data has been successfully updated.");
            }
        } catch (SQLException ex) {
            ApplyMethods.showErrorAlert("Error", "Database Error", "An error occurred while updating the order data.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void resetButtonHandle(ActionEvent event) {
        productComboBox.setValue(null);
        quantityTextField.clear();
        datePicker.setValue(null);
    }

    private void getOrderedProducts(int clientId) {
        List<String> orderedProducts = new ArrayList<>();
        try {
            String query = "SELECT name FROM products WHERE id IN (SELECT product_id FROM order_products WHERE order_id IN (SELECT order_id FROM orders WHERE user_id = " + clientId + "))";
            ResultSet rss = statement.executeQuery(query);

            while (rss.next()) {
                String productName = rss.getString("name");
                orderedProducts.add(productName);
                System.out.println("Ordered Product: " + productName);
            }
            if (orderedProducts.isEmpty()) {
                System.out.println("No categories"); //user doesn't need this info so it doesn't need an alert
            } else {
                productComboBox.getItems().addAll(orderedProducts);
            }
            
            System.out.println("Added ordered products to ComboBox");
        } catch (SQLException ex) {
            System.out.println("Error executing query: " + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("Error: productComboBox is not initialized");
        }
    }

    private int getProductId(String productName) {
        try {
            String query = "SELECT product_id FROM Order_Products op "
                    + "JOIN Products p ON op.product_id = p.id "
                    + "WHERE p.name = '" + productName + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("product_id");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return -1; // Return -1 if product ID is not found
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
