package FinalProject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AdminAddOrderController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button mainBtnStyle;
    @FXML
    private ComboBox<String> clientsCombobox;
    @FXML
    private ListView<String> productsListView;
    @FXML
    private TextField productQuantityTextField;
    @FXML
    private DatePicker orderDatePicker;
    @FXML
    private Label datePickerErrorLabel;
    @FXML
    private Button resetButton;

    private Statement statement;
    private Connection connection;
    private Alert alert;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        fillClientsCombobox();
        fillProductsListView();
        productsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void fillClientsCombobox() {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT email FROM users WHERE role = 'client'");

            ObservableList<String> clientNames = clientsCombobox.getItems();
            while (resultSet.next()) {
                String clientName = resultSet.getString("email");
                clientNames.add(clientName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillProductsListView() {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT id, name FROM products where quantity>0");
            ObservableList<String> productItems = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int productId = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                String item = "[" + productId + "] " + productName;
                productItems.add(item);
            }

            if (productItems.isEmpty()) {
                ApplyMethods.showErrorAlert("Error", "There are no products in store",
                        "Please supply the store with products.");
            }

            productsListView.setItems(productItems);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        moveTo(menuBar, "AdminDashboard.fxml", "Admin Dashboard");
    }

    @FXML
    private void addButtonHandle(ActionEvent event) {
        try {
            String selectedClientEmail = clientsCombobox.getValue();
            String selectUserIdQuery = "SELECT id FROM users WHERE email = ?";
            PreparedStatement userIdStatement = connection.prepareStatement(selectUserIdQuery);
            userIdStatement.setString(1, selectedClientEmail);
            ResultSet userIdResult = userIdStatement.executeQuery();

            int userId = 0;
            if (userIdResult.next()) {
                userId = userIdResult.getInt("id");
            } else {
                ApplyMethods.showErrorAlert("Error", "No client is selected",
                        "There's no selected client. Therefore, the system cannot complete this operation! Please select a client.");
                return;
            }

            ObservableList<String> selectedProductItems = productsListView.getSelectionModel().getSelectedItems();
            ObservableList<Integer> selectedProductIds = FXCollections.observableArrayList();
            if (selectedProductItems.isEmpty()) {
                ApplyMethods.showErrorAlert("Error", "No product is selected",
                        "There's no selected product. Therefore, the system cannot complete this operation! Please select a client.");
                return;
            } else {
                for (String selectedItem : selectedProductItems) {
                    int productId = Integer.parseInt(selectedItem.substring(selectedItem.indexOf('[') + 1, selectedItem.indexOf(']')));
                    selectedProductIds.add(productId);
                }
            }

            if (productQuantityTextField != null && validateNumericInput(productQuantityTextField.getText(), "Invalid Quantity!", "Please enter a valid quantity.")
                    && Integer.parseInt(productQuantityTextField.getText()) > 0
                    && Integer.parseInt(productQuantityTextField.getText()) < 2) {
                int quantity = Integer.parseInt(productQuantityTextField.getText());
                LocalDate myDate = null;
                try {
                    myDate = orderDatePicker.getValue();
                } catch (NullPointerException ex) {
                    System.out.println(ex.getMessage());
                }
                if (orderDatePicker != null && orderDatePicker.getValue() != null) {
                    String insertOrderQuery = "INSERT INTO `orders` (user_id, date) VALUES ('" + userId + "','" + myDate.toString() + "')";
                    int affectedRows = statement.executeUpdate(insertOrderQuery);
                } else {
                    datePickerErrorLabel.setVisible(true);
                    return;
                }
                int orderId = getLastInsertedOrderId();
                System.out.println(orderId);
                if (orderId != 0) {
                    for (int productId : selectedProductIds) {
                        String insertOrderProductsQuery = "INSERT INTO order_products (order_id, product_id, quantity) VALUES (" + orderId + "," + productId + "," + quantity + ")";
                        int affectedRows = statement.executeUpdate(insertOrderProductsQuery);
                    }
                    for (int productId : selectedProductIds) {
                        // Subtract the ordered quantity from the store quantity
                        String updateQuantityQuery = "UPDATE products SET quantity = quantity - "+quantity+" WHERE id = "+productId;
                        int affectedRows = statement.executeUpdate(updateQuantityQuery);
                        // Check if the updated quantity is zero
                        String checkQuantityQuery = "SELECT quantity FROM products WHERE id = ?";
                        PreparedStatement checkQuantityStatement = connection.prepareStatement(checkQuantityQuery);
                        checkQuantityStatement.setInt(1, productId);
                        ResultSet quantityResult = checkQuantityStatement.executeQuery();
                        if (quantityResult.next()) {
                            int updatedQuantity = quantityResult.getInt("quantity");
                            if (updatedQuantity == 0) { // Remove the product from the list view if the quantity is zero
                                productsListView.getItems().removeIf(item -> item.startsWith("[" + productId + "]"));
                            }
                        }
                    }
                    ApplyMethods.showInfoAlert("Success", "Done!", "Order successfully added. Order ID is " + orderId);
                } else {
                    System.out.println("Failed to retrieve generated order ID.");
                }
            } else if (productQuantityTextField.getText().isEmpty()) {
                ApplyMethods.showErrorAlert("Error", "Invalid Quantity!", "Invalid quantity! Please enter a quantity.");
            } else {
                ApplyMethods.showErrorAlert("Error", "Invalid Quantity!", "Invalid quantity! You cannot add more than one piece of each product.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void resetButtonHandle(ActionEvent event) {
        clientsCombobox.getSelectionModel().clearSelection();
        productQuantityTextField.setText("");
        productsListView.getSelectionModel().clearSelection();
        orderDatePicker.setValue(null);
    }

    private boolean validateNumericInput(String input, String errorTitle, String errorMessage) {
        try {
            int value = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException ex) {
            ApplyMethods.showErrorAlert("Alert, empty values!", errorTitle, errorMessage);
            return false;
        }
    }

    private int getLastInsertedOrderId() throws SQLException {
        ResultSet lastInsertIdResult = statement.executeQuery("SELECT LAST_INSERT_ID()");
        if (lastInsertIdResult.next()) {
            return lastInsertIdResult.getInt(1);
        } else {
            return 0;
        }
    }

    private void moveTo(Node node, String fxmlFile, String stageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);
            Stage currentStage = (Stage) node.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle(stageTitle);
            currentStage.setMaximized(true);
            currentStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
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
