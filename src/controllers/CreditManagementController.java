package controllers;

import DataBase.BillsDataBase;
import DataBase.OutletsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import models.Outlet;
import models.OutletCredit;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreditManagementController implements Initializable {
    @FXML private TableView<OutletCredit> creditsTable;
    @FXML private TableColumn<OutletCredit, Integer> creditIdColumn;
    @FXML private TableColumn<OutletCredit, String> outletColumn;
    @FXML private TableColumn<OutletCredit, Double> amountColumn;
    @FXML private TableColumn<OutletCredit, Double> paidColumn;
    @FXML private TableColumn<OutletCredit, Double> remainingColumn;
    @FXML private TableColumn<OutletCredit, String> statusColumn;
    @FXML private TableColumn<OutletCredit, String> creditDateColumn;
    @FXML private TableColumn<OutletCredit, String> dueDateColumn;
    @FXML private TableColumn<OutletCredit, Void> actionColumn;
    
    @FXML private ComboBox<String> outletComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField searchField;
    
    @FXML private VBox paymentForm;
    @FXML private Label selectedCreditIdLabel;
    @FXML private Label selectedOutletLabel;
    @FXML private Label remainingAmountLabel;
    @FXML private TextField paymentAmountField;
    @FXML private ComboBox<String> paymentModeComboBox;
    @FXML private TextArea remarksArea;
    
    private final BillsDataBase billsDataBase = new BillsDataBase();
    private final OutletsDataBase outletsDataBase = new OutletsDataBase();
    private OutletCredit selectedCredit;
    private Integer selectedCreditId = 0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // First set up the combo boxes
            setupComboBoxes();
            
            // Then set up the table columns
            if (creditsTable != null) {
                setupTable();
            }
            
            // Finally set up the listeners
            setupListeners();
            
            // Load initial data
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to initialize: " + e.getMessage());
        }
    }
    
    private void setupComboBoxes() {
        try {
            // Setup outlet combo box
            if (outletComboBox != null) {
                List<Outlet> outlets = outletsDataBase.getAllOutlets();
                List<String> outletNames = outlets.stream()
                    .map(Outlet::getName)
                    .collect(Collectors.toList());
                outletNames.add(0, "All Outlets");
                outletComboBox.getItems().clear();
                outletComboBox.getItems().addAll(outletNames);
                outletComboBox.setValue("All Outlets");
            }
            
            // Setup status combo box
            if (statusComboBox != null) {
                statusComboBox.getItems().clear();
                statusComboBox.getItems().addAll("All", "Pending", "Partially Paid", "Paid", "Overdue");
                statusComboBox.setValue("All");
            }
        } catch (Exception e) {
            System.err.println("Error setting up combo boxes: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to setup combo boxes: " + e.getMessage());
        }
    }
    
    private void setupTable() {
        if (creditIdColumn != null) {
            creditIdColumn.setCellValueFactory(new PropertyValueFactory<>("creditId"));
        }
        if (outletColumn != null) {
            outletColumn.setCellValueFactory(new PropertyValueFactory<>("outletName"));
        }
        if (amountColumn != null) {
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("creditAmount"));
        }
        if (paidColumn != null) {
            paidColumn.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));
        }
        if (remainingColumn != null) {
            remainingColumn.setCellValueFactory(new PropertyValueFactory<>("remainingAmount"));
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
        
        // Format credit date
        if (creditDateColumn != null) {
            creditDateColumn.setCellValueFactory(new PropertyValueFactory<>("creditDate"));
            creditDateColumn.setCellFactory(col -> new TableCell<OutletCredit, String>() {
                @Override
                protected void updateItem(String date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty || date == null) {
                        setText(null);
                    } else {
                        setText(date);
                    }
                }
            });
        }
        
        // Format due date
        if (dueDateColumn != null) {
            dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
            dueDateColumn.setCellFactory(col -> new TableCell<OutletCredit, String>() {
                @Override
                protected void updateItem(String date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty || date == null) {
                        setText(null);
                    } else {
                        setText(date);
                    }
                }
            });
        }
        
        // Add action column
        if (actionColumn != null) {
            actionColumn.setCellFactory(col -> new TableCell<OutletCredit, Void>() {
                private final Button addPaymentButton = new Button("Add Payment");
                
                {
                    addPaymentButton.setOnAction(event -> {
                        OutletCredit credit = getTableView().getItems().get(getIndex());
                        if (credit != null) {
                            showPaymentDialog(credit);
                        }
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(addPaymentButton);
                    }
                }
            });
        }
    }
    
    private void setupListeners() {
        // Outlet combo box listener
        if (outletComboBox != null) {
            outletComboBox.setOnAction(event -> {
                try {
                    loadData();
                } catch (Exception e) {
                    showError("Error", "Failed to load data: " + e.getMessage());
                }
            });
        }
        
        // Status combo box listener
        if (statusComboBox != null) {
            statusComboBox.setOnAction(event -> {
                try {
                    loadData();
                } catch (Exception e) {
                    showError("Error", "Failed to load data: " + e.getMessage());
                }
            });
        }
        
        // Search field listener
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    loadData();
                } catch (Exception e) {
                    showError("Error", "Failed to load data: " + e.getMessage());
                }
            });
        }
    }
    
    private void loadData() throws SQLException {
        System.out.println("Starting to load data...");
        
        String selectedOutlet = outletComboBox != null ? outletComboBox.getValue() : "All Outlets";
        String selectedStatus = statusComboBox != null ? statusComboBox.getValue() : "All";
        
        System.out.println("Selected outlet: " + selectedOutlet);
        System.out.println("Selected status: " + selectedStatus);
        
        List<OutletCredit> credits;
        if (selectedOutlet != null && !selectedOutlet.equals("All Outlets")) {
            System.out.println("Loading credits for outlet: " + selectedOutlet);
            List<Outlet> outlets = outletsDataBase.getAllOutlets();
            Outlet outlet = outlets.stream()
                .filter(o -> o.getName().equals(selectedOutlet))
                .findFirst()
                .orElse(null);
                
            if (outlet != null) {
                System.out.println("Loading credits for outlet ID: " + outlet.getId());
                credits = billsDataBase.getCreditsByOutlet(outlet.getId());
            } else {
                System.out.println("Outlet not found, loading all credits");
                credits = billsDataBase.getAllCredits();
            }
        } else {
            System.out.println("Loading all credits");
            credits = billsDataBase.getAllCredits();
        }
        
        System.out.println("Number of credits loaded: " + credits.size());
        
        // Filter by status if needed
        if (selectedStatus != null && !selectedStatus.equals("All")) {
            System.out.println("Filtering by status: " + selectedStatus);
            credits = credits.stream()
                .filter(credit -> credit.getStatus().equals(selectedStatus.toUpperCase()))
                .collect(Collectors.toList());
            System.out.println("Number of credits after filtering: " + credits.size());
        }
        
        // Apply search filter if any
        String searchText = searchField != null ? searchField.getText() : "";
        if (searchText != null && !searchText.isEmpty()) {
            credits = credits.stream()
                .filter(credit -> 
                    credit.getOutletName().toLowerCase().contains(searchText.toLowerCase()) ||
                    String.valueOf(credit.getCreditId()).contains(searchText))
                .collect(Collectors.toList());
        }
        
        if (creditsTable != null) {
            creditsTable.getItems().setAll(credits);
        }
        
        System.out.println("Data loaded successfully");
    }
    
    private void showPaymentDialog(OutletCredit credit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/payment_dialog.fxml"));
            Parent root = loader.load();
            
            PaymentDialogController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Payment");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            controller.setCredit(credit);
            
            dialogStage.showAndWait();
            
            if (controller.isPaymentAdded()) {
                loadData(); // Refresh the table
            }
            
        } catch (Exception e) {
            System.err.println("Error showing payment dialog: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to show payment dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void onAddPaymentClicked() {
        if (selectedCreditId <= 0) {
            showError("Error", "Please select a credit first");
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
            billsDataBase.addCreditPayment(selectedCreditId, amount, mode, remarks);

            // Refresh data
            loadData();

            // Reset form
            clearPaymentForm();

            showSuccess("Success", "Payment added successfully");
        } catch (NumberFormatException e) {
            showError("Error", "Invalid payment amount");
        } catch (SQLException e) {
            showError("Error", "Failed to add payment: " + e.getMessage());
        }
    }
    
    @FXML
    private void onCancelPayment() {
        clearPaymentForm();
    }
    
    @FXML
    private void onRefreshClicked() {
        try {
            loadData();
        } catch (Exception e) {
            showError("Error", "Failed to refresh data: " + e.getMessage());
        }
    }
    
    private void clearPaymentForm() {
        selectedCreditId = 0;
        
        if (selectedCreditIdLabel != null) {
            selectedCreditIdLabel.setText("");
        }
        if (selectedOutletLabel != null) {
            selectedOutletLabel.setText("");
        }
        if (remainingAmountLabel != null) {
            remainingAmountLabel.setText("");
        }
        if (paymentAmountField != null) {
            paymentAmountField.clear();
        }
        if (paymentModeComboBox != null) {
            paymentModeComboBox.setValue(null);
        }
        if (remarksArea != null) {
            remarksArea.clear();
            // Only try to access parent if remarksArea exists
            Node parent = remarksArea.getParent();
            if (parent != null) {
                Node titledPane = parent.getParent();
                if (titledPane instanceof TitledPane) {
                    ((TitledPane) titledPane).setExpanded(false);
                }
            }
        }
        if (paymentForm != null) {
            paymentForm.setVisible(false);
        }
    }
    
    private void showError(String title, String content) {
        System.err.println("Error - " + title + ": " + content);
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