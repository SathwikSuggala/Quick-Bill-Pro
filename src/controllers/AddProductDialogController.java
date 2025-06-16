package controllers;

import DataBase.InletsDataBase;
import DataBase.ProductsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Inlet;
import models.Product;

public class AddProductDialogController {

    @FXML
    private ComboBox<String> inletComboBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField quantityField;

    private ProductsDataBase productsDB;
    private InletsDataBase inletsDB;
    private Runnable onAddCallback;

    public void initialize() {
        inletsDB = new InletsDataBase();
        loadInlets();
    }

    private void loadInlets() {
        ObservableList<Inlet> inlets = inletsDB.fetchAllInletsWithProductCount();
        ObservableList<String> inletNames = FXCollections.observableArrayList();
        for (Inlet inlet : inlets) {
            inletNames.add(inlet.getName());
        }
        inletComboBox.setItems(inletNames);
    }

    public void setProductsDB(ProductsDataBase productsDB) {
        this.productsDB = productsDB;
    }

    public void setOnAddCallback(Runnable callback) {
        this.onAddCallback = callback;
    }

    @FXML
    private void onAddClicked() {
        String selectedInlet = inletComboBox.getValue();
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String priceStr = priceField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        if (selectedInlet == null || name.isEmpty() || description.isEmpty() || 
            priceStr.isEmpty() || quantityStr.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        // Check if product with same name exists
        ObservableList<Product> existingProducts = productsDB.searchProductsByName(name);
        if (!existingProducts.isEmpty()) {
            showAlert("Error", "A product with this name already exists", Alert.AlertType.ERROR);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            if (price <= 0 || quantity <= 0) {
                showAlert("Error", "Price and quantity must be greater than 0", Alert.AlertType.ERROR);
                return;
            }

            productsDB.addProductForInlet(selectedInlet, name, description, price, quantity, price);
            if (onAddCallback != null) {
                onAddCallback.run();
            }
            closeDialog();
            showAlert("Success", "Product added successfully", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for price and quantity", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCancelClicked() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) inletComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 