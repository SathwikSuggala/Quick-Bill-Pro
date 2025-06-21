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
import javafx.stage.Stage;
import models.BillViewItem;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<BillViewItem> salesReportTable;
    @FXML private TableColumn<BillViewItem, String> billDateColumn;
    @FXML private TableColumn<BillViewItem, Integer> billIdColumn;
    @FXML private TableColumn<BillViewItem, String> outletNameColumn;
    @FXML private TableColumn<BillViewItem, Double> totalCGSTColumn;
    @FXML private TableColumn<BillViewItem, Double> totalSGSTColumn;
    @FXML private TableColumn<BillViewItem, Double> totalAmountColumn;
    @FXML private TableColumn<BillViewItem, Void> viewBillColumn;

    private final ReportsDataBase reportsDataBase = new ReportsDataBase();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns
        billDateColumn.setCellValueFactory(new PropertyValueFactory<>("billDate"));
        billIdColumn.setCellValueFactory(new PropertyValueFactory<>("billId"));
        outletNameColumn.setCellValueFactory(new PropertyValueFactory<>("outletName"));
        totalCGSTColumn.setCellValueFactory(new PropertyValueFactory<>("totalCGST"));
        totalSGSTColumn.setCellValueFactory(new PropertyValueFactory<>("totalSGST"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // Set up view bill button column
        viewBillColumn.setCellFactory(col -> new TableCell<BillViewItem, Void>() {
            private final Button viewButton = new Button("View Bill");
            {
                viewButton.setOnAction(event -> {
                    BillViewItem item = getTableView().getItems().get(getIndex());
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
            ObservableList<BillViewItem> reportData = FXCollections.observableArrayList(
                reportsDataBase.getBillsReport(startDate, endDate)
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
            ScrollPane billRoot = loader.load();
            
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