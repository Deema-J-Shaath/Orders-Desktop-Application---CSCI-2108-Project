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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
public class AdminManageProductsController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TableView<Product> tableView;
    @FXML
    private TableColumn<Product, Integer> orderID_tc;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<String> categoryCombobox;
    @FXML
    private TextField priceTextField;
    @FXML
    private TextField quantityTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Button editButton;
    @FXML
    private Button mainBtnStyle;

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
    private TextField serchTextField;
    @FXML
    private TextField iDtextField;
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
        orderID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        categoryColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        priceColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
        quantityColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        descriptionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        try {
            connection = DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        bindingTableColumnsWithAttributes();
        fillCategoryCombobox();
        showProducts();

        TableView.TableViewSelectionModel<Product> selectionModel = tableView.getSelectionModel();

// Add a selection listener to the selection model
        selectionModel.selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Product product = newSelection;
                iDtextField.setText(String.valueOf(product.getId()));
                nameTextField.setText(String.valueOf(product.getName()));
                priceTextField.setText(String.valueOf(product.getPrice()));
                quantityTextField.setText(String.valueOf(product.getQuantity()));
                descriptionTextArea.setText(String.valueOf(product.getDescription()));
            } else {
                iDtextField.setText("");
                nameTextField.setText("");
                priceTextField.setText("");
                quantityTextField.setText("");
                descriptionTextArea.setText("");
            }
        });

    }

    public void fillTextField() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Product product = tableView.getSelectionModel().getSelectedItem();

        }
    }

    public void bindingTableColumnsWithAttributes() {
        orderID_tc.setCellValueFactory(new PropertyValueFactory("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory("quantity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory("description"));
    }

    private void fillCategoryCombobox() {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT distinct category FROM products");

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
    private void addAction(ActionEvent event) {
        String name = nameTextField.getText();
        String category = categoryCombobox.getValue();
        String price = priceTextField.getText();
        String quantity1 = quantityTextField.getText();
        String description = descriptionTextArea.getText();
        if (ApplyMethods.validateInput(name) && ApplyMethods.validateInput(category) && ApplyMethods.validateInput(price)
                && ApplyMethods.validateInput(quantity1) && ApplyMethods.validateInput(description) && ApplyMethods.validateNumericInput(quantity1) && ApplyMethods.isDouble(price)) {
            try {
                String sql = "INSERT INTO products (name, category, price, quantity, description) values('" + name + "','" + category + "'," + price + "," + quantity1 + ",'" + description + "')";
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println(" number of afected Row = " + affectedRows);
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
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

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "AdminDashboard.fxml", "Admin Dashboard", AdminManageProductsController.class);
    }

    @FXML
    private void editButtonhandle(ActionEvent event) {
        if (iDtextField.getText().isEmpty()) {
            ApplyMethods.showErrorAlert("Error", "Cannot Complete this operation", "Fill ID text field!");
            return;
        }
        if (ApplyMethods.validateInput(iDtextField.getText()) && ApplyMethods.validateInput(iDtextField.getText()) && ApplyMethods.validateInput(nameTextField.getText()) && ApplyMethods.validateInput(categoryCombobox.getValue()) && ApplyMethods.validateInput(priceTextField.getText())
                && ApplyMethods.validateInput(quantityTextField.getText()) && ApplyMethods.validateInput(descriptionTextArea.getText()) && ApplyMethods.validateNumericInput(quantityTextField.getText()) && ApplyMethods.isDouble(priceTextField.getText())) {
            try {
                String name = nameTextField.getText();
                String category = categoryCombobox.getValue();
                String price = priceTextField.getText();
                String quantity1 = quantityTextField.getText();
                String description = descriptionTextArea.getText();
                int id = Integer.parseInt(iDtextField.getText());
                String sql = "update products set name='" + name + "',category='" + category + "',description='" + description + "',price=" + price + ",quantity=" + quantity1 + " where id=" + id;
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    ApplyMethods.showInfoAlert("Success", "Updated successfully!", "");
                } else {
                    ApplyMethods.showErrorAlert("Error", "Failed to update the product!", "Please try again later... ");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    private void searchButtonHandle(ActionEvent event) {
        try {
            if (ApplyMethods.validateInput(serchTextField.getText()) && ApplyMethods.validateNumericInput(serchTextField.getText())) {
                int id = Integer.parseInt(serchTextField.getText());

                String sql = "SELECT * FROM Products where id =" + id;

                ResultSet rs = statement.executeQuery(sql);
                ArrayList<Product> productList = new ArrayList();
                while (rs.next()) {
                    productList.add(new Product(rs.getInt("id"), rs.getString("name"), rs.getString("category"), rs.getDouble("price"), rs.getInt("quantity"), rs.getString("description")));
                }
                if (productList.isEmpty()) {
                    ApplyMethods.showErrorAlert("Error", "No product found!", "There's no product holds ID " + id);
                } else {
                    tableView.getItems().setAll(productList);
                }
            } else {
                if (!ApplyMethods.validateNumericInput(serchTextField.getText())) {
                    ApplyMethods.showErrorAlert("Error", "Cannot complete this operaton!", "Try again.");
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    private void deleteButtonHandle(ActionEvent event) {
        int rowIndex = tableView.getSelectionModel().getSelectedIndex();
        int id = 0;
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                id = selectedProduct.getId();
                System.out.println("Selected row ID: " + id);

                // Delete associated records from order_products table
                String deleteOrderProductsSql = "DELETE FROM order_products WHERE product_id = " + id;
                int deletedOrderProducts = statement.executeUpdate(deleteOrderProductsSql);
                System.out.println("Deleted associated order_products rows: " + deletedOrderProducts);

                // Delete the product from products table
                String deleteProductSql = "DELETE FROM products WHERE id = " + id;
                int affectedRows = statement.executeUpdate(deleteProductSql);
                if (affectedRows > 0) {
                    ApplyMethods.showInfoAlert("Success", "Deleted Successfully!", "You have just deleted product number " + id);
                } else {
                    ApplyMethods.showErrorAlert("Error", "Deletion failed!", "Cannot complete operation.");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            ApplyMethods.showErrorAlert("Error", "Cannot complete this operation!", "Please select a product to delete.");
        }
    }

    @FXML
    private void resetButtonHandle(ActionEvent event) {
        nameTextField.setText("");
        categoryCombobox.setValue(null);
        priceTextField.setText("");
        quantityTextField.setText("");
        descriptionTextArea.setText("");
        showProducts();
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
