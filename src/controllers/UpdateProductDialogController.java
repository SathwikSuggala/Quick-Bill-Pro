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
    @FXML
    private TextField cgstField;
    @FXML
    private TextField sgstField;
    @FXML
    private TextField hsnField;

    private Product product;
    private ProductsDataBase productsDB;
    private Runnable onUpdateCallback;

    public void setProduct(Product product) {
        this.product = product;
        nameField.setText(product.getName());
        descriptionField.setText(product.getDescription());
        hsnField.setText(String.valueOf(product.getHsn()));
        priceField.setText(String.valueOf(product.getUnitPrice()));
        quantityField.setText(String.valueOf(product.getQuantity()));
        cgstField.setText(String.valueOf(product.getCgst()));
        sgstField.setText(String.valueOf(product.getSgst()));
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
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String cgstText = cgstField.getText().trim();
        String sgstText = sgstField.getText().trim();
        String hsnText = hsnField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty() || 
            cgstText.isEmpty() || sgstText.isEmpty() || hsnText.isEmpty()) {
            showAlert("Error", "Please fill in all required fields", Alert.AlertType.ERROR);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);
            double cgst = Double.parseDouble(cgstText);
            double sgst = Double.parseDouble(sgstText);
            double hsn = Double.parseDouble(hsnText);

            if (price < 0 || quantity < 0 || cgst < 0 || sgst < 0) {
                showAlert("Error", "Price, quantity, CGST, and SGST must be non-negative", Alert.AlertType.ERROR);
                return;
            }

            productsDB.updateProductOfInlet(product.getId(), name, description, price, quantity, cgst, sgst, hsn);
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
            closeDialog();
            showAlert("Success", "Product updated successfully", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for price, quantity, CGST, and SGST", Alert.AlertType.ERROR);
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