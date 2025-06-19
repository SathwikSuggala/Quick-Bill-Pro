package controllers;

import DataBase.BillsDataBase;
import DataBase.OutletsDataBase;
import DataBase.ProductsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.BillItem;
import models.Outlet;
import models.Product;
import models.Bill;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.io.IOException;

public class BillingController implements Initializable {
    @FXML private ComboBox<Outlet> outletComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private Label availableQuantityLabel;
    @FXML private TableView<BillItem> itemsTableView;
    @FXML private TableColumn<BillItem, String> productNameColumn;
    @FXML private TableColumn<BillItem, Integer> quantityColumn;
    @FXML private TableColumn<BillItem, Double> priceColumn;
    @FXML private TableColumn<BillItem, Double> cgstColumn;
    @FXML private TableColumn<BillItem, Double> sgstColumn;
    @FXML private TableColumn<BillItem, Double> totalColumn;
    @FXML private TableColumn<BillItem, Double> hsnColumn;
    @FXML private TableColumn<BillItem, Void> actionColumn;
    @FXML private Label totalAmountLabel;
    @FXML private Label totalCGSTLabel;
    @FXML private Label totalSGSTLabel;
    @FXML private Label grandTotalLabel;
    @FXML private ComboBox<String> paymentTypeComboBox;
    @FXML private ComboBox<Integer> creditMonthsComboBox;
    @FXML private Label creditMonthsLabel;
    @FXML private TextField remarksField;

    private final ObservableList<BillItem> billItems = FXCollections.observableArrayList();
    private final BillsDataBase billsDataBase = new BillsDataBase();
    private final OutletsDataBase outletsDataBase = new OutletsDataBase();
    private final ProductsDataBase productsDataBase = new ProductsDataBase();
    private boolean outletSelected = false;
    private Product selectedProduct = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupTable();
        setupListeners();
    }

    private void setupComboBoxes() {
        // Setup outlet combo box
        outletComboBox.setItems(FXCollections.observableArrayList(outletsDataBase.getAllOutlets()));
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

        // Setup payment type combo box
        paymentTypeComboBox.setItems(FXCollections.observableArrayList(
            "Cash", "UPI", "Credit"
        ));

        // Setup credit months combo box
        creditMonthsComboBox.setItems(FXCollections.observableArrayList(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
        ));
    }

    private void setupTable() {
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        hsnColumn.setCellValueFactory(new PropertyValueFactory<>("hsn"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cgstColumn.setCellValueFactory(new PropertyValueFactory<>("cgst"));
        sgstColumn.setCellValueFactory(new PropertyValueFactory<>("sgst"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        actionColumn.setCellFactory(col -> new TableCell<BillItem, Void>() {
            private final Button removeButton = new Button("Remove");
            {
                removeButton.setOnAction(event -> {
                    BillItem item = getTableView().getItems().get(getIndex());
                    billItems.remove(item);
                    updateTotals();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });

        itemsTableView.setItems(billItems);
    }

    private void setupListeners() {
        // Disable outlet selection after first item is added
        outletComboBox.setOnAction(event -> {
            if (!outletSelected && outletComboBox.getValue() != null) {
                outletSelected = true;
                loadProductsForOutlet(outletComboBox.getValue().getId());
            }
        });

        // Show/hide credit months based on payment type
        paymentTypeComboBox.setOnAction(event -> {
            boolean isCredit = "Credit".equals(paymentTypeComboBox.getValue());
            creditMonthsLabel.setVisible(isCredit);
            creditMonthsLabel.setManaged(isCredit);
            creditMonthsComboBox.setVisible(isCredit);
            creditMonthsComboBox.setManaged(isCredit);
        });

        // Update available quantity when product is selected
        productComboBox.setOnAction(event -> {
            selectedProduct = productComboBox.getValue();
            if (selectedProduct != null) {
                availableQuantityLabel.setText("Available: " + selectedProduct.getQuantity());
            } else {
                availableQuantityLabel.setText("Available: 0");
            }
        });

        // Validate quantity input
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(newValue);
                    if (selectedProduct != null && quantity > selectedProduct.getQuantity()) {
                        quantityField.setText(String.valueOf(selectedProduct.getQuantity()));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            }
        });
    }

    private void loadProductsForOutlet(int outletId) {
        productComboBox.setItems(FXCollections.observableArrayList(
            productsDataBase.getAllProductsWithAvailableQuantity()
        ));
        productComboBox.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                return product != null ? product.getName() : "";
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void onAddItemClicked() {
        if (!validateInputs()) return;

        Product selectedProduct = productComboBox.getValue();
        int quantity = Integer.parseInt(quantityField.getText());

        BillItem item = new BillItem(
            0, // billItemId will be set by database
            0, // billId will be set by database
            selectedProduct.getId(),
            selectedProduct.getName(),
            quantity,
            selectedProduct.getUnitPrice(),
            selectedProduct.getCgst(),
            selectedProduct.getSgst(),
            selectedProduct.getHsn()
        );

        billItems.add(item);
        updateTotals();
        clearInputFields();
    }

    private boolean validateInputs() {
        if (outletComboBox.getValue() == null) {
            showError("Error", "Please select an outlet");
            return false;
        }
        if (productComboBox.getValue() == null) {
            showError("Error", "Please select a product");
            return false;
        }
        if (quantityField.getText().isEmpty()) {
            showError("Error", "Please enter quantity");
            return false;
        }
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                showError("Error", "Quantity must be greater than 0");
                return false;
            }
            if (quantity > selectedProduct.getQuantity()) {
                showError("Error", "Quantity cannot exceed available stock");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Error", "Invalid quantity");
            return false;
        }
        return true;
    }

    private void clearInputFields() {
        productComboBox.setValue(null);
        quantityField.clear();
        availableQuantityLabel.setText("Available: 0");
        selectedProduct = null;
    }

    private void updateTotals() {
        double subtotal = billItems.stream()
            .mapToDouble(item -> item.getQuantity() * item.getPrice())
            .sum();
        
        double totalCGST = billItems.stream()
            .mapToDouble(BillItem::getCgstAmount)
            .sum();
            
        double totalSGST = billItems.stream()
            .mapToDouble(BillItem::getSgstAmount)
            .sum();
            
        double grandTotal = subtotal + totalCGST + totalSGST;

        totalAmountLabel.setText(String.format("%.2f", subtotal));
        totalCGSTLabel.setText(String.format("%.2f", totalCGST));
        totalSGSTLabel.setText(String.format("%.2f", totalSGST));
        grandTotalLabel.setText(String.format("%.2f", grandTotal));
    }

    @FXML
    private void onGenerateBillClicked() {
        if (billItems.isEmpty()) {
            showError("Error", "Please add items to the bill");
            return;
        }

        if (paymentTypeComboBox.getValue() == null) {
            showError("Error", "Please select payment type");
            return;
        }

        if ("Credit".equals(paymentTypeComboBox.getValue()) && creditMonthsComboBox.getValue() == null) {
            showError("Error", "Please select credit months");
            return;
        }

        try {
            double subtotal = billItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
            
            double totalCGST = billItems.stream()
                .mapToDouble(BillItem::getCgstAmount)
                .sum();
                
            double totalSGST = billItems.stream()
                .mapToDouble(BillItem::getSgstAmount)
                .sum();
                
            double grandTotal = subtotal + totalCGST + totalSGST;

            // Create bill
            int billId = billsDataBase.createBill(
                outletComboBox.getValue().getId(),
                totalCGST,
                totalSGST,
                grandTotal,
                paymentTypeComboBox.getValue(),
                remarksField.getText(),
                java.sql.Date.valueOf(LocalDate.now())
            );

            // Add bill items
            for (BillItem item : billItems) {
                billsDataBase.addBillItem(
                    billId,
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getCgst(),
                    item.getSgst()
                );
            }

            // Retrieve the newly created bill with its items
            Bill createdBill = billsDataBase.getBillById(billId);
            if (createdBill == null) {
                showError("Error", "Failed to retrieve generated bill.");
                return;
            }
            createdBill.setBillItems(billItems);

            // If credit payment, create outlet credit
            if ("Credit".equals(paymentTypeComboBox.getValue())) {
                LocalDate dueDate = LocalDate.now().plusMonths(creditMonthsComboBox.getValue());
                billsDataBase.createOutletCredit(
                    outletComboBox.getValue().getId(),
                    billId,
                    grandTotal,
                    java.sql.Date.valueOf(dueDate)
                );
            }

            // Open bill soft copy page
            openBillSoftCopyPage(createdBill, outletComboBox.getValue().getName(), outletComboBox.getValue().getAddress());

            clearBill();
        } catch (SQLException e) {
            showError("Error", "Failed to generate bill: " + e.getMessage());
        }
    }

    private void openBillSoftCopyPage(Bill bill, String outletName, String outletAddress) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/bill_soft_copy.fxml"));
            Parent root = loader.load();

            BillSoftCopyController controller = loader.getController();
            
            // Create a new Bill object with a copy of the billItems
            Bill billForSoftCopy = new Bill(
                bill.getBillId(),
                bill.getOutletId(),
                bill.getTotalCGST(),
                bill.getTotalSGST(),
                bill.getTotalAmount(),
                bill.getBillDate(),
                bill.getPaymentType(),
                bill.getRemarks()
            );
            billForSoftCopy.setBillItems(FXCollections.observableArrayList(bill.getBillItems()));

            // Fetch GSTIN from the selected outlet
            String outletGSTIN = outletComboBox.getValue() != null ? outletComboBox.getValue().getGstin() : "";
            controller.setBillData(billForSoftCopy, outletName, outletAddress, outletGSTIN);

            Stage stage = new Stage();
            stage.setTitle("Bill Soft Copy");
            Scene scene = new Scene(root, 800, 700); // Set a preferred size for the scene
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error", "Failed to open bill soft copy page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearBill() {
        billItems.clear();
        updateTotals();
        outletComboBox.setValue(null);
        outletSelected = false;
        paymentTypeComboBox.setValue(null);
        remarksField.clear();
        creditMonthsComboBox.setValue(null);
        clearInputFields();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 