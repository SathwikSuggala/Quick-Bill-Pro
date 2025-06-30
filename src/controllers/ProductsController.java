package controllers;

import DataBase.InletsDataBase;
import DataBase.ProductsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Inlet;
import models.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductsController implements Initializable {

    private static final Logger logger = LogManager.getLogger(ProductsController.class);

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Double> cgstColumn;
    @FXML
    private TableColumn<Product, Double> sgstColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, String> inletColumn;
    @FXML
    private TableColumn<Product, Double> hsnColumn;
    @FXML
    private TableColumn<Product, Void> actionColumn;

    private final ProductsDataBase productsDB = new ProductsDataBase();
    private final InletsDataBase inletsDB = new InletsDataBase();
    private ObservableList<Product> productsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        cgstColumn.setCellValueFactory(new PropertyValueFactory<>("cgst"));
        sgstColumn.setCellValueFactory(new PropertyValueFactory<>("sgst"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        inletColumn.setCellValueFactory(new PropertyValueFactory<>("inletName"));
        hsnColumn.setCellValueFactory(new PropertyValueFactory<>("hsn"));

        hsnColumn.setCellFactory(col -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item.longValue()));
                }
            }
        });

        // Set up action column with purchase and update buttons
        actionColumn.setCellFactory(col -> new TableCell<Product, Void>() {
            private final Button purchaseButton = new Button("Purchase");
            private final Button updateButton = new Button("Update");
            private final HBox buttonsBox = new HBox(5, purchaseButton, updateButton);

            {
                purchaseButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showAddPurchaseDialog(product);
                });

                updateButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showUpdateDialog(product);
                });

                // Style the buttons
                purchaseButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10;");
                updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });

        // Load initial products
        refreshProductsList();
    }

    @FXML
    private void onHistoryClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/purchaseHistoryDialog.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Purchase History");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Could not open purchase history dialog", Alert.AlertType.ERROR);
            logger.error("Error opening purchase history dialog", e);
        }
    }

    @FXML
    private void onAddProductClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addProductDialog.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Add New Product");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            AddProductDialogController controller = loader.getController();
            controller.setProductsDB(productsDB);
            controller.setOnAddCallback(this::refreshProductsList);

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Could not open add product dialog", Alert.AlertType.ERROR);
            logger.error("Error opening add product dialog", e);
        }
    }

    private void showUpdateDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateProductDialog.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Update Product");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            UpdateProductDialogController controller = loader.getController();
            controller.setProduct(product);
            controller.setProductsDB(productsDB);
            controller.setOnUpdateCallback(this::refreshProductsList);

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Could not open update dialog", Alert.AlertType.ERROR);
            logger.error("Error opening update dialog", e);
        }
    }

    private void showAddPurchaseDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addPurchaseDialog.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Add Purchase");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            AddPurchaseDialogController controller = loader.getController();
            controller.setProduct(product);
            controller.setProductsDB(productsDB);
            controller.setOnAddCallback(this::refreshProductsList);

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Could not open add purchase dialog", Alert.AlertType.ERROR);
            logger.error("Error opening add purchase dialog", e);
        }
    }

    @FXML
    private void onSearchClicked() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            productsList = productsDB.searchProductsByName(searchText);
            productsTable.setItems(productsList);
        }
    }

    @FXML
    private void onShowAllClicked() {
        refreshProductsList();
    }

    @FXML
    private void onRefreshClicked() {
        refreshProductsList();
    }

    private void refreshProductsList() {
        productsList = productsDB.getAllProductsWithAvailableQuantity();
        productsTable.setItems(productsList);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 