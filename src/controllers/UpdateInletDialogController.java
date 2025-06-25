package controllers;

import DataBase.InletsDataBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Inlet;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateInletDialogController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField contactField;

    private Inlet inletToUpdate;
    private InletsDataBase inletsDataBase;
    private boolean updateSuccessful = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inletsDataBase = new InletsDataBase();
    }

    public void setInlet(Inlet inlet) {
        this.inletToUpdate = inlet;
        if (inlet != null) {
            nameField.setText(inlet.getName());
            contactField.setText(inlet.getContactInfo());
        }
    }

    @FXML
    private void onUpdateClicked() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        if (inletToUpdate != null) {
            boolean success = inletsDataBase.updateInlet(inletToUpdate.getId(), name, contact);
            if (success) {
                updateSuccessful = true;
                showAlert("Success", "Inlet updated successfully", Alert.AlertType.INFORMATION);
                closeDialog();
            } else {
                showAlert("Error", "Failed to update inlet", Alert.AlertType.ERROR);
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