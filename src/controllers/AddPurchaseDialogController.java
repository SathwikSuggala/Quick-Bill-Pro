package controllers;

import DataBase.ProductsDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Product;

public class AddPurchaseDialogController {

    @FXML
    private Label inletNameLabel;
    @FXML
    private Label productNameLabel;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField costPriceField;

    private ProductsDataBase productsDB;
    private Product product;
    private Runnable onAddCallback;

    public void setProduct(Product product) {
        this.product = product;
        productNameLabel.setText(product.getName());
        inletNameLabel.setText(product.getInletName());
        // Pre-fill cost price with unit price
        costPriceField.setText(String.valueOf(product.getUnitPrice()));
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
        String costPriceStr = costPriceField.getText().trim();

        if (quantityStr.isEmpty() || costPriceStr.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            double costPrice = Double.parseDouble(costPriceStr);

            if (quantity <= 0 || costPrice <= 0) {
                showAlert("Error", "Quantity and cost price must be greater than 0", Alert.AlertType.ERROR);
                return;
            }

            productsDB.addPurchaseForProduct(product.getInletName(), product.getName(), quantity, costPrice);
            if (onAddCallback != null) {
                onAddCallback.run();
            }
            closeDialog();
            showAlert("Success", "Purchase added successfully", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for quantity and cost price", Alert.AlertType.ERROR);
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