package controllers;

import DataBase.ReportsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.ReportItem;

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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 