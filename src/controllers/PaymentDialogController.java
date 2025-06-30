package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.OutletCredit;
import DataBase.BillsDataBase;
import java.sql.SQLException;

public class PaymentDialogController {
    @FXML private Label creditIdLabel;
    @FXML private Label outletLabel;
    @FXML private Label remainingAmountLabel;
    @FXML private TextField paymentAmountField;
    @FXML private ComboBox<String> paymentModeComboBox;
    @FXML private TextArea remarksArea;
    
    private Stage dialogStage;
    private OutletCredit credit;
    private boolean paymentAdded = false;
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setCredit(OutletCredit credit) {
        this.credit = credit;
        
        // Initialize payment mode combo box if not already done
        if (paymentModeComboBox != null && paymentModeComboBox.getItems().isEmpty()) {
            paymentModeComboBox.getItems().addAll("Cash", "Bank Transfer", "UPI", "Cheque");
        }
        
        if (credit != null) {
            if (creditIdLabel != null) {
                creditIdLabel.setText(String.valueOf(credit.getCreditId()));
            }
            if (outletLabel != null) {
                outletLabel.setText(credit.getOutletName());
            }
            if (remainingAmountLabel != null) {
                remainingAmountLabel.setText(String.format("₹%.2f", credit.getRemainingAmount()));
            }
            if (paymentAmountField != null) {
                paymentAmountField.setText(String.format("%.2f", credit.getRemainingAmount()));
                // Add payment amount validation
                paymentAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*\\.?\\d*")) {
                        paymentAmountField.setText(oldValue);
                    }
                });
            }
            if (paymentModeComboBox != null) {
                paymentModeComboBox.setValue("Cash");
            }
            if (remarksArea != null) {
                remarksArea.clear();
            }
        }
    }
    
    public boolean isPaymentAdded() {
        return paymentAdded;
    }
    
    @FXML
    private void initialize() {
        // Any other initialization that doesn't depend on FXML fields
    }
    
    @FXML
    private void onAddPayment() {
        if (credit == null) {
            showError("Error", "No credit selected");
            return;
        }

        try {
            double amount = Double.parseDouble(paymentAmountField.getText());
            String mode = paymentModeComboBox.getValue();
            String remarks = remarksArea.getText();

            if (amount <= 0) {
                showError("Error", "Payment amount must be greater than 0");
                return;
            }

            if (mode == null || mode.isEmpty()) {
                showError("Error", "Please select a payment mode");
                return;
            }

            // Add payment to database
            BillsDataBase.addCreditPayment(credit.getCreditId(), amount, mode, remarks);
            paymentAdded = true;
            dialogStage.close();
            
        } catch (NumberFormatException e) {
            showError("Error", "Invalid payment amount");
        } catch (SQLException e) {
            showError("Error", "Failed to add payment: " + e.getMessage());
        } catch (Exception e) {
            showError("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    @FXML
    private void onCancel() {
        dialogStage.close();
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 