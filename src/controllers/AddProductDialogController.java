package controllers;

import DataBase.InletsDataBase;
import DataBase.ProductsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private TextField cgstField;
    @FXML
    private TextField sgstField;
    @FXML
    private TextField hsnField;

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
        String inletName = inletComboBox.getValue();
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String priceText = priceField.getText().trim();
        String cgstText = cgstField.getText().trim();
        String sgstText = sgstField.getText().trim();
        String hsnText = hsnField.getText().trim();

        if (inletName == null || inletName.isEmpty() || name.isEmpty() || priceText.isEmpty() || 
            cgstText.isEmpty() || sgstText.isEmpty() || hsnText.isEmpty()) {
            showAlert("Error", "Please fill in all required fields", Alert.AlertType.ERROR);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            double cgst = Double.parseDouble(cgstText);
            double sgst = Double.parseDouble(sgstText);
            double hsn = Double.parseDouble(hsnText);

            if (price < 0 || cgst < 0 || sgst < 0) {
                showAlert("Error", "Price, CGST, and SGST must be non-negative", Alert.AlertType.ERROR);
                return;
            }

            productsDB.addProductForInlet(inletName, name, description, price, cgst, sgst, hsn);
            if (onAddCallback != null) {
                onAddCallback.run();
            }
            closeDialog();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for price, CGST, and SGST", Alert.AlertType.ERROR);
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