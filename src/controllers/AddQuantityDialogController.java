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
    private TextField quantityField;

    private Product product;
    private ProductsDataBase productsDB;
    private Runnable onAddCallback;

    public void setProduct(Product product) {
        this.product = product;
        productNameLabel.setText(product.getName());
    }

    public void setProductsDB(ProductsDataBase productsDB) {
        this.productsDB = productsDB;
    }

    public void setOnAddCallback(Runnable callback) {
        this.onAddCallback = callback;
    }

    @FXML
    private void onAddClicked() {
        String quantityText = quantityField.getText().trim();

        if (quantityText.isEmpty()) {
            showAlert("Error", "Please enter quantity", Alert.AlertType.ERROR);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);

            if (quantity <= 0) {
                showAlert("Error", "Quantity must be greater than 0", Alert.AlertType.ERROR);
                return;
            }

            // Update the product with the new quantity while maintaining other values
            productsDB.updateProductOfInlet(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getUnitPrice(),
                product.getQuantity() + quantity,
                product.getCgst(),
                product.getSgst()
            );

            if (onAddCallback != null) {
                onAddCallback.run();
            }
            closeDialog();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for quantity", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCancelClicked() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) productNameLabel.getScene().getWindow();
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