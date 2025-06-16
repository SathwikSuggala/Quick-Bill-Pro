package controllers;

import DataBase.ProductsDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Product;

public class UpdateProductDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField quantityField;

    private Product product;
    private ProductsDataBase productsDB;
    private Runnable onUpdateCallback;

    public void setProduct(Product product) {
        this.product = product;
        nameField.setText(product.getName());
        descriptionField.setText(product.getDescription());
        priceField.setText(String.valueOf(product.getUnitPrice()));
        quantityField.setText(String.valueOf(product.getQuantity()));
    }

    public void setProductsDB(ProductsDataBase productsDB) {
        this.productsDB = productsDB;
    }

    public void setOnUpdateCallback(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    @FXML
    private void onUpdateClicked() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String priceStr = priceField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            if (price <= 0 || quantity <= 0) {
                showAlert("Error", "Price and quantity must be greater than 0", Alert.AlertType.ERROR);
                return;
            }

            productsDB.updateProductOfInlet(product.getId(), name, description, price, quantity);
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
            closeDialog();
            showAlert("Success", "Product updated successfully", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for price and quantity", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCancelClicked() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
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