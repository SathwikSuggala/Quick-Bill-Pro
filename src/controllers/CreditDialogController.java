package controllers;

import DataBase.BillsDataBase;
import DataBase.OutletsDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import models.Outlet;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CreditDialogController {
    
    @FXML private ComboBox<Outlet> outletComboBox;
    @FXML private TextField amountField;
    @FXML private DatePicker dueDatePicker;
    @FXML private TextArea remarksArea;
    
    private OutletsDataBase outletsDataBase = new OutletsDataBase();
    private BillsDataBase billsDataBase = new BillsDataBase();
    
    @FXML
    public void initialize() {

            // Initialize outlets
            List<Outlet> outlets = outletsDataBase.getAllOutlets();
            outletComboBox.setItems(javafx.collections.FXCollections.observableArrayList(outlets));
            outletComboBox.setConverter(new StringConverter<Outlet>() {
                @Override
                public String toString(Outlet outlet) {
                    return outlet != null ? outlet.getName() : "";
                }
                
                @Override
                public Outlet fromString(String string) {
                    return null;
                }
            });
            
            // Set default due date to 30 days from now
            dueDatePicker.setValue(LocalDate.now().plusDays(30));
            
            // Add validation for amount field
            amountField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*\\.?\\d*")) {
                    amountField.setText(oldValue);
                }

            
        });
    }
    
    public void processResult() {
        Outlet selectedOutlet = outletComboBox.getValue();
        String amountText = amountField.getText();
        LocalDate dueDate = dueDatePicker.getValue();
        
        if (selectedOutlet == null) {
            showError("Error", "Please select an outlet");
            return;
        }
        
        if (amountText.isEmpty()) {
            showError("Error", "Please enter a credit amount");
            return;
        }
        
        if (dueDate == null) {
            showError("Error", "Please select a due date");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showError("Error", "Credit amount must be greater than 0");
                return;
            }
            
            // Create credit entry
            billsDataBase.createOutletCredit(
                selectedOutlet.getId(),
                0, // No associated bill
                amount,
                Date.valueOf(dueDate)
            );
            
            showSuccess("Success", "Credit created successfully");
            
        } catch (NumberFormatException e) {
            showError("Error", "Please enter a valid amount");
        } catch (SQLException e) {
            showError("Error", "Failed to create credit: " + e.getMessage());
        }
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 