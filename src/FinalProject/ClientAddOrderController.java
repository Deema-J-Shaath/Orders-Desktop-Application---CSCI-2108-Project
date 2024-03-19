package FinalProject;

import java.net.URL;
import java.sql.Connection;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ClientAddOrderController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ComboBox<String> categoryCombobox;
    @FXML
    private TableView<Product> tableView;
    @FXML
    private TableColumn<Product, String> nameTc;
    @FXML
    private TableColumn<Product, String> categoryTc;
    @FXML
    private TableColumn<Product, Double> priceTc;
    @FXML
    private Label totalPrice;
    @FXML
    private Button mainBtnStyle;
    @FXML
    private Button resetButton;
    @FXML
    private TextField quantityTextField;
    @FXML
    private TableColumn<Product, String> description_tc;
    @FXML
    private Button logoutButton;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu submenu;
    @FXML
    private MenuItem backgroundColorMenuItem;
    @FXML
    private ColorPicker colorPicker;
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

    private Statement statement;
    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        nameTc.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryTc.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceTc.setCellValueFactory(new PropertyValueFactory<>("price"));
        description_tc.setCellValueFactory(new PropertyValueFactory<>("description"));
        bindColumnWidths();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        fillCategoryCombobox();
        showProducts();
    }

    private void bindColumnWidths() {
        nameTc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        categoryTc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        priceTc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        description_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
    }

    private void fillCategoryCombobox() {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT distinct category FROM products WHERE quantity > 0");

            ObservableList<String> categories = FXCollections.observableArrayList();
            while (resultSet.next()) {
                String category = resultSet.getString("category");
                categories.add(category);
            }

            categoryCombobox.setItems(categories);
            categoryCombobox.setOnAction((event) -> {
                String selectedCategory = categoryCombobox.getSelectionModel().getSelectedItem();
                if (selectedCategory == null) {
                    showProducts();
                } else {
                    showProducts();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientDashboard.fxml", "Client Dashboard", ClientAddOrderController.class);
    }

    @FXML
    private void addButtonHandle(ActionEvent event) {
        ObservableList<Product> selectedProducts = tableView.getSelectionModel().getSelectedItems();
        if (selectedProducts.isEmpty()) {
            ApplyMethods.showErrorAlert("No Products Selected", "Please select at least one product.", " ");
            return;
        }
        int clientId = ApplyMethods.getCurrentClientId();
        System.out.println(clientId);
        if (clientId == 0) {
            ApplyMethods.showErrorAlert("Error!", "Invalid Client ID", "The client ID is not valid.");
            return;
        }
        if (!ApplyMethods.validateNumericInput(quantityTextField.getText())) {
            ApplyMethods.showErrorAlert("Invalid Quantity", "Invalid quantity. Please make sure to enter a numeric value.", "");
            return;
        }
        int quantity = Integer.parseInt(quantityTextField.getText());
        if (quantity != 1) {
            ApplyMethods.showErrorAlert("Invalid Quantity", "Invalid quantity. We can only offer you one piece of each product.", "");
            return;
        }
        try {
            LocalDate currentDate = LocalDate.now();
            String insertOrderQuery = "INSERT INTO orders (user_id, date) VALUES (" + clientId + ", '" + currentDate + "')";
            int affectedRows = statement.executeUpdate(insertOrderQuery);
            int orderId = getLastInsertedOrderId();
            double tot_price = 0.00;
            if (orderId != 0) {
                for (Product product : selectedProducts) {
                    int productId = product.getId();
                    tot_price += product.getPrice();
                    int productQuantity = product.getQuantity();
                    if (productQuantity <= 0) {
                        ApplyMethods.showErrorAlert("Error!", "Invalid Quantity!", "The selected product is out of stock.");
                        return;
                    }
                    String insertOrderProductQuery = "INSERT INTO order_products (order_id, product_id, quantity) VALUES (" + orderId + ", " + productId + ", 1)";
                    int affecttedRows = statement.executeUpdate(insertOrderProductQuery);
                    System.out.println(affecttedRows);
                    String updateProductQuery = "UPDATE Products SET quantity = " + (productQuantity - 1) + " WHERE id = " + productId;
                    statement.executeUpdate(updateProductQuery);
                }
                ApplyMethods.showInfoAlert("Order Added", "The order has been added successfully.", "");
                totalPrice.setText(String.valueOf(tot_price) + "$");
                tableView.getSelectionModel().clearSelection();
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            ApplyMethods.showErrorAlert("Error", "An error occurred while adding the order.", "Please try again.");
        }
    }

    @FXML
    private void resetButtonHandle(ActionEvent event) {
        categoryCombobox.getSelectionModel().clearSelection();
        categoryCombobox.setPromptText("Category");
        quantityTextField.setText("");
        showProducts();
    }

    private void showProducts() {
        try {
            if (categoryCombobox.getSelectionModel().getSelectedItem() == null || categoryCombobox.getSelectionModel().getSelectedItem().isEmpty()) {

                ResultSet rs = statement.executeQuery("SELECT * FROM Products where quantity>0");
                ObservableList<Product> productList = FXCollections.observableArrayList();
                while (rs.next()) {
                    Product product = new Product(rs.getInt("id"), rs.getString("name"), rs.getString("category"), rs.getDouble("price"), rs.getInt("quantity"), rs.getString("description"));
                    productList.add(product);
                }
                tableView.setItems(productList);
            } else {
                String selectedCateg = categoryCombobox.getSelectionModel().getSelectedItem();
                ResultSet rs = statement.executeQuery("SELECT * FROM Products where quantity>0 and category='" + selectedCateg + "'");
                ObservableList<Product> productList = FXCollections.observableArrayList();
                while (rs.next()) {
                    Product product = new Product(rs.getInt("id"), rs.getString("name"), rs.getString("category"), rs.getDouble("price"), rs.getInt("quantity"), rs.getString("description"));
                    productList.add(product);
                }
                if (productList.isEmpty()) {
                    ApplyMethods.showErrorAlert("Error", "No products to show!", "");
                } else {
                    tableView.setItems(productList);
                }

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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

    @FXML
    private void applyEditsButton(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientEditOrder.fxml", "Edit Order Screen", ClientAddOrderController.class);
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
