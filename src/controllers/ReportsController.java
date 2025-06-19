package controllers;

import DataBase.ReportsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.ReportItem;
import models.BillViewItem;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<ReportItem> salesReportTable;
    @FXML private TableColumn<ReportItem, String> saleDateColumn;
    @FXML private TableColumn<ReportItem, Integer> billIdColumn;
    @FXML private TableColumn<ReportItem, String> outletNameColumn;
    @FXML private TableColumn<ReportItem, String> productNameColumn;
    @FXML private TableColumn<ReportItem, Integer> quantityColumn;
    @FXML private TableColumn<ReportItem, Double> unitPriceColumn;
    @FXML private TableColumn<ReportItem, Double> cgstColumn;
    @FXML private TableColumn<ReportItem, Double> sgstColumn;
    @FXML private TableColumn<ReportItem, Double> totalAmountColumn;
    @FXML private TableColumn<ReportItem, Void> viewBillColumn;

    private final ReportsDataBase reportsDataBase = new ReportsDataBase();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        billIdColumn.setCellValueFactory(new PropertyValueFactory<>("billId"));
        outletNameColumn.setCellValueFactory(new PropertyValueFactory<>("outletName"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        cgstColumn.setCellValueFactory(new PropertyValueFactory<>("cgst"));
        sgstColumn.setCellValueFactory(new PropertyValueFactory<>("sgst"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // Set up view bill button column
        viewBillColumn.setCellFactory(col -> new TableCell<ReportItem, Void>() {
            private final Button viewButton = new Button("View Bill");
            {
                viewButton.setOnAction(event -> {
                    ReportItem item = getTableView().getItems().get(getIndex());
                    viewBill(item.getBillId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewButton);
            }
        });
    }

    @FXML
    private void onGenerateReportClicked() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showAlert("Error", "Please select both start and end dates.");
            return;
        }

        try {
            ObservableList<ReportItem> reportData = FXCollections.observableArrayList(
                reportsDataBase.getSalesReport(startDate, endDate)
            );
            salesReportTable.setItems(reportData);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to generate report: " + e.getMessage());
        }
    }

    private void viewBill(int billId) {
        try {
            BillViewItem billDetails = reportsDataBase.getBillDetails(billId);
            if (billDetails != null) {
                showBillWindow(billDetails);
            } else {
                showAlert("Error", "Could not find bill details.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load bill details: " + e.getMessage());
        }
    }

    private void showBillWindow(BillViewItem billDetails) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/bill_soft_copy.fxml"));
            BorderPane billRoot = loader.load();
            
            BillSoftCopyController controller = loader.getController();
            controller.setBillDetails(billDetails);
            controller.setBillItems(reportsDataBase.getBillItems(billDetails.getBillId()));

            Stage stage = new Stage();
            stage.setTitle("Bill #" + billDetails.getBillId());
            stage.setScene(new Scene(billRoot));
            stage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open bill view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 