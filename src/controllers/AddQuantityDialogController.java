package controllers;

import DataBase.ProductsDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Product;

public class AddQuantityDialogController {

    @FXML
    private Label productNameLabel;
    @FXML
    private Label currentQuantityLabel;
    @FXML
    private TextField quantityField;

    private ProductsDataBase productsDB;
    private Product product;
    private Runnable onAddCallback;

    public void setProduct(Product product) {
        this.product = product;
        productNameLabel.setText(product.getName());
        currentQuantityLabel.setText(String.valueOf(product.getQuantity()));
    }

    public void setProductsDB(ProductsDataBase productsDB) {
        this.productsDB = productsDB;
    }

    public void setOnAddCallback(Runnable callback) {
        this.onAddCallback = callback;
    }

    @FXML
    private void onAddClicked() {
        String quantityStr = quantityField.getText().trim();

        if (quantityStr.isEmpty()) {
            showAlert("Error", "Please enter quantity", Alert.AlertType.ERROR);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);

            if (quantity <= 0) {
                showAlert("Error", "Quantity must be greater than 0", Alert.AlertType.ERROR);
                return;
            }

            // Update the product quantity
            productsDB.updateProductOfInlet(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getUnitPrice(),
                product.getQuantity() + quantity
            );

            if (onAddCallback != null) {
                onAddCallback.run();
            }
            closeDialog();
            showAlert("Success", "Quantity added successfully", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for quantity", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCancelClicked() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) quantityField.getScene().getWindow();
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