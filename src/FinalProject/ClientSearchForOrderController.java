package FinalProject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.ColorPicker;
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
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ClientSearchForOrderController implements Initializable {

    @FXML
    private VBox logoBar;
    @FXML
    private Label logo;
    @FXML
    private MenuBar menuBar;

    @FXML
    private Button editButton;
    @FXML
    private TableView<OrderProduct> tableView;
    @FXML
    private TableColumn<Orders, Integer> orderID_tc;
    @FXML
    private TableColumn<Orders, Integer> productID_tc;
    @FXML
    private TableColumn<Orders, Integer> quantity_tc;
    @FXML
    private Button resetButton;
    @FXML
    private TextField orderIdSerachTextField;

    Connection connection;
    Statement statement;

    int clientId = ApplyMethods.getCurrentClientId();
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
    private Button dashboardButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up cell value factories for the table columns
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        productID_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        quantity_tc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        // Set up cell value factories for the table columns
        orderID_tc.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        productID_tc.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        quantity_tc.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Enable multiple selection in the table view
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        try {
            connection= DB_Connection.db_connection();
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
    }

    @FXML
    private void dashboardHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "ClientDashboard.fxml", "Dashboard Screen", ClientSearchForOrderController.class);
    }

    @FXML
    private void searchButtonHandle(ActionEvent event) {
        if (ApplyMethods.validateInput(orderIdSerachTextField.getText()) && ApplyMethods.validateNumericInput(orderIdSerachTextField.getText())) {
            try {
                //fetch client orders then see if the id matches any
                int inputOrderId = Integer.parseInt(orderIdSerachTextField.getText());
                String sql = "Select id from orders where user_id= " + clientId;
                ResultSet ifHisOrder = statement.executeQuery(sql);
                boolean hisOrder = false;
                if (!ifHisOrder.isBeforeFirst()) {
                    // No rows returned from the query, i.e., the result set is empty
                    System.out.println("No orders found for the specified client ID.");
                } else {
                    while (ifHisOrder.next() && hisOrder == false) {
                        int id = ifHisOrder.getInt("id");
                        if (inputOrderId == id) {
                            hisOrder = true;
                            System.out.println("Found");
                        }
                    }
                    if (hisOrder == false) {
                        ApplyMethods.showErrorAlert("Error", "Order not found", "");
                        System.out.println("Order not found.");
                    } else {
                        String sqll = "select * from order_products where order_id=" + inputOrderId;
                        Statement secondStatement = connection.createStatement();
                        ResultSet rss = secondStatement.executeQuery(sqll);
                        ArrayList<OrderProduct> opList = new ArrayList<>();
                        while(rss.next()){
                            OrderProduct op = new OrderProduct(rss.getInt("order_id"), rss.getInt("product_id"), rss.getInt("quantity"));
                            opList.add(op);
                        }
                        tableView.getItems().setAll(opList);
                    }
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    private void resetHandle(ActionEvent event) {
        orderIdSerachTextField.setText("");
        tableView.getItems().clear();
    }

    private void logoutHandle(ActionEvent event) {
        ApplyMethods.moveTo(menuBar, "LoginScreen.fxml", "Login Screen", ClientViewProfileController.class);
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
//        size14.setOnAction(new MyEventHandler());
//        size18.setOnAction(new MyEventHandler());
//        size20.setOnAction(new MyEventHandler());
//        size22.setOnAction(new MyEventHandler());
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
//            if (t.getSource() == size14 || t.getSource() == size18 || t.getSource() == size20 || t.getSource() == size22) {
//                MenuItem menuItem = (MenuItem) t.getSource();
//                String fontSizeText = menuItem.getText();
//                int fontSize = Integer.parseInt(fontSizeText);
//                Font newFont = Font.font(fontSize);
//                Stage stage = (Stage) menuBar.getScene().getWindow();
//                stage.getScene().getRoot().setStyle("-fx-font-size: " + fontSize + "px; -fx-font-family: " + newFont.getFamily() + ";");
//            }
        }
    }

}
