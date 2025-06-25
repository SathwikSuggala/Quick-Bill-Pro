package controllers;

import DataBase.OutletsDataBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Outlet;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateOutletDialogController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private TextField gstinField;

    private Outlet outletToUpdate;
    private boolean updateSuccessful = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // No initialization needed
    }

    public void setOutlet(Outlet outlet) {
        this.outletToUpdate = outlet;
        if (outlet != null) {
            nameField.setText(outlet.getName());
            addressField.setText(outlet.getAddress());
            contactField.setText(outlet.getContactInfo());
            emailField.setText(outlet.getEmail());
            gstinField.setText(outlet.getGstin());
        }
    }

    @FXML
    private void onUpdateClicked() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String gstin = gstinField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || contact.isEmpty() || email.isEmpty() || gstin.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        if (outletToUpdate != null) {
            boolean success = OutletsDataBase.updateOutlet(outletToUpdate.getId(), name, address, contact, email, gstin);
            if (success) {
                updateSuccessful = true;
                showAlert("Success", "Outlet updated successfully", Alert.AlertType.INFORMATION);
                closeDialog();
            } else {
                showAlert("Error", "Failed to update outlet", Alert.AlertType.ERROR);
            }
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

    public boolean isUpdateSuccessful() {
        return updateSuccessful;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 