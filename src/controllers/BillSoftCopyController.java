package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Bill;
import models.BillItem;

import java.net.URL;
import java.util.ResourceBundle;

public class BillSoftCopyController implements Initializable {

    @FXML private Text billNoText;
    @FXML private Text billDateText;
    @FXML private Text receiverNameText;
    @FXML private Text receiverAddressText;

    @FXML private TableView<BillItem> billItemsTable;
    @FXML private TableColumn<BillItem, String> descriptionColumn;
    @FXML private TableColumn<BillItem, Integer> qtyColumn;
    @FXML private TableColumn<BillItem, Double> basicRateColumn;
    @FXML private TableColumn<BillItem, Double> taxableAmountColumn;
    @FXML private TableColumn<BillItem, Double> cgstPercentageColumn;
    @FXML private TableColumn<BillItem, Double> cgstValueColumn;
    @FXML private TableColumn<BillItem, Double> sgstPercentageColumn;
    @FXML private TableColumn<BillItem, Double> sgstValueColumn;
    @FXML private TableColumn<BillItem, Double> itemTotalAmountColumn;

    @FXML private Text subTotalText;
    @FXML private Text totalCGSTText;
    @FXML private Text totalSGSTText;
    @FXML private Text grandTotalText;

    @FXML private VBox billContentVBox;
    @FXML private BorderPane billRootPane;

    private double originalTablePrefHeight;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        originalTablePrefHeight = billItemsTable.getPrefHeight();

        ((TableColumn<BillItem, String>) billItemsTable.getColumns().get(0))
                .setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                String.valueOf(cellData.getTableView().getItems().indexOf(cellData.getValue()) + 1)));

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        basicRateColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        taxableAmountColumn.setCellValueFactory(new PropertyValueFactory<>("taxableAmount"));
        cgstPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("cgst"));
        cgstValueColumn.setCellValueFactory(new PropertyValueFactory<>("cgstAmount"));
        sgstPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("sgst"));
        sgstValueColumn.setCellValueFactory(new PropertyValueFactory<>("sgstAmount"));
        itemTotalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    public void setBillData(Bill bill, String outletName, String outletAddress) {
        billNoText.setText(String.valueOf(bill.getBillId()));
        billDateText.setText(bill.getBillDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString());
        receiverNameText.setText(outletName);
        receiverAddressText.setText(outletAddress);

        billItemsTable.setItems(bill.getBillItems());

        subTotalText.setText(String.format("₹%.2f", bill.getTotalAmount() - bill.getTotalCGST() - bill.getTotalSGST()));
        totalCGSTText.setText(String.format("₹%.2f", bill.getTotalCGST()));
        totalSGSTText.setText(String.format("₹%.2f", bill.getTotalSGST()));
        grandTotalText.setText(String.format("₹%.2f", bill.getTotalAmount()));
    }

    @FXML
    private void onPrintButtonClicked() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(billRootPane.getScene().getWindow())) {
            // Expand TableView height to fit all rows before taking snapshot
            billItemsTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
            billContentVBox.applyCss();
            billContentVBox.layout();

            WritableImage snapshot = billContentVBox.snapshot(new SnapshotParameters(), null);
            ImageView printNode = new ImageView(snapshot);
            printNode.setPreserveRatio(true);
            printNode.setFitWidth(job.getJobSettings().getPageLayout().getPrintableWidth());

            boolean success = job.printPage(printNode);
            if (success) {
                job.endJob();
            }

            // Reset TableView height
            billItemsTable.setPrefHeight(originalTablePrefHeight);
            billContentVBox.layout();
        }
    }
}
