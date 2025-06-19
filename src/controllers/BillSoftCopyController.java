package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import models.Bill;
import models.BillItem;
import models.BillViewItem;
import models.ReportItem;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class BillSoftCopyController implements Initializable {

    @FXML private Text billNoText;
    @FXML private Text billDateText;
    @FXML private Text receiverNameText;
    @FXML private Text receiverAddressText;
    @FXML private Text receiverGSTText;

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
    @FXML private TableColumn<BillItem, Double> hsnColumn;
    @FXML private Text roundOffText;
    @FXML private Text amountInWordsText;

    @FXML private TableView<ReportItem> taxSummaryTable;
    @FXML private TableColumn<ReportItem, Double> taxHsnColumn;
    @FXML private TableColumn<ReportItem, Double> taxableValueColumn;
    @FXML private TableColumn<ReportItem, Double> taxCgstColumn;
    @FXML private TableColumn<ReportItem, Double> taxSgstColumn;
    @FXML private TableColumn<ReportItem, Double> taxTotalColumn;

    @FXML private Text subTotalText;
    @FXML private Text totalCGSTText;
    @FXML private Text totalSGSTText;
    @FXML private Text grandTotalText;

    @FXML private VBox billContentVBox;
    @FXML private ScrollPane billRootPane;

    private double originalTablePrefHeight;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        originalTablePrefHeight = billItemsTable.getPrefHeight();

        // Serial No. column
        ((TableColumn<BillItem, String>) billItemsTable.getColumns().get(0))
                .setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                String.valueOf(cellData.getTableView().getItems().indexOf(cellData.getValue()) + 1)));

        // Set cell value factories
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        hsnColumn.setCellValueFactory(new PropertyValueFactory<>("hsn"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        basicRateColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        taxableAmountColumn.setCellValueFactory(new PropertyValueFactory<>("taxableAmount"));
        cgstPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("cgst"));
        cgstValueColumn.setCellValueFactory(new PropertyValueFactory<>("cgstAmount"));
        sgstPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("sgst"));
        sgstValueColumn.setCellValueFactory(new PropertyValueFactory<>("sgstAmount"));
        itemTotalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        taxHsnColumn.setCellValueFactory(new PropertyValueFactory<>("hsn"));
        taxableValueColumn.setCellValueFactory(new PropertyValueFactory<>("taxableAmount"));
        taxCgstColumn.setCellValueFactory(new PropertyValueFactory<>("cgstAmount"));
        taxSgstColumn.setCellValueFactory(new PropertyValueFactory<>("sgstAmount"));
        taxTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Apply two-decimal formatting
        StringConverter<Double> twoDecimalConverter = new StringConverter<>() {
            @Override
            public String toString(Double value) {
                return value == null ? "" : String.format("%.2f", value);
            }

            @Override
            public Double fromString(String string) {
                return Double.parseDouble(string);
            }
        };

        basicRateColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        taxableAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        cgstPercentageColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        cgstValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        sgstPercentageColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        sgstValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        itemTotalAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        hsnColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));

        taxHsnColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        taxableValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        taxCgstColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        taxSgstColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
        taxTotalColumn.setCellFactory(TextFieldTableCell.forTableColumn(twoDecimalConverter));
    }

    public void setBillData(Bill bill, String outletName, String outletAddress, String outletGSTIN) {
        billNoText.setText(String.valueOf(bill.getBillId()));
        billDateText.setText(bill.getBillDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString());
        receiverNameText.setText(outletName);
        receiverAddressText.setText(outletAddress);
        receiverGSTText.setText(outletGSTIN);

        billItemsTable.setItems(bill.getBillItems());

        double subTotal = bill.getTotalAmount() - bill.getTotalCGST() - bill.getTotalSGST();
        subTotalText.setText(String.format("₹%.2f", subTotal));
        totalCGSTText.setText(String.format("₹%.2f", bill.getTotalCGST()));
        totalSGSTText.setText(String.format("₹%.2f", bill.getTotalSGST()));
        grandTotalText.setText(String.format("₹%.2f", bill.getTotalAmount()));

        double roundedGrandTotal = Math.round(bill.getTotalAmount());
        double roundOff = roundedGrandTotal - bill.getTotalAmount();
        roundOffText.setText(String.format("%.2f", roundOff));
        amountInWordsText.setText("INR " + numberToWords((long) roundedGrandTotal) + " Only");

        ObservableList<ReportItem> taxSummary = FXCollections.observableArrayList();
        Map<Double, ReportItem> hsnMap = new HashMap<>();

        for (BillItem item : bill.getBillItems()) {
            double hsn = item.getHsn();
            ReportItem rep = hsnMap.getOrDefault(hsn, new ReportItem());
            rep.setHsn(hsn);
            rep.setTaxableAmount(rep.getTaxableAmount() + item.getTaxableAmount());
            rep.setCgstAmount(rep.getCgstAmount() + item.getCgstAmount());
            rep.setSgstAmount(rep.getSgstAmount() + item.getSgstAmount());
            rep.setTotal(rep.getTotal() + item.getCgstAmount() + item.getSgstAmount());
            hsnMap.put(hsn, rep);
        }

        taxSummary.addAll(hsnMap.values());
        taxSummaryTable.setItems(taxSummary);
    }

    public void setBillDetails(BillViewItem billDetails) {
        billNoText.setText(String.valueOf(billDetails.getBillId()));
        billDateText.setText(billDetails.getBillDate());
        receiverNameText.setText(billDetails.getOutletName());
        receiverAddressText.setText(billDetails.getOutletAddress());
        totalCGSTText.setText(String.format("₹%.2f", billDetails.getTotalCGST()));
        totalSGSTText.setText(String.format("₹%.2f", billDetails.getTotalSGST()));
        grandTotalText.setText(String.format("₹%.2f", billDetails.getTotalAmount()));

        double subtotal = billDetails.getTotalAmount() - billDetails.getTotalCGST() - billDetails.getTotalSGST();
        subTotalText.setText(String.format("₹%.2f", subtotal));
    }

    @FXML
    private void onPrintButtonClicked() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(billRootPane.getScene().getWindow())) {
            billItemsTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
            billContentVBox.applyCss();
            billContentVBox.layout();

            // Apply scaling for higher resolution
            SnapshotParameters params = new SnapshotParameters();
            params.setTransform(javafx.scene.transform.Transform.scale(2.0, 2.0)); // 2x resolution

            // Use scaled dimensions
            double scaledWidth = billContentVBox.getWidth() * 2;
            double scaledHeight = billContentVBox.getHeight() * 2.4;

            WritableImage highResImage = new WritableImage((int) scaledWidth, (int) scaledHeight);
            WritableImage snapshot = billContentVBox.snapshot(params, highResImage);

            ImageView printNode = new ImageView(snapshot);
            printNode.setPreserveRatio(true);
            printNode.setFitWidth(job.getJobSettings().getPageLayout().getPrintableWidth());

            boolean success = job.printPage(printNode);
            if (success) job.endJob();

            billItemsTable.setPrefHeight(originalTablePrefHeight);
            billContentVBox.layout();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setBillItems(java.util.List<ReportItem> items) {
        if (billItemsTable != null && items != null) {
            ObservableList data = FXCollections.observableArrayList(items);
            billItemsTable.setItems(data);
        }
    }

    private static final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };
    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private String numberToWords(long n) {
        if (n == 0) return "Zero";
        if (n < 0) return "Minus " + numberToWords(-n);
        String words = "";
        if ((n / 10000000) > 0) {
            words += numberToWords(n / 10000000) + " Crore ";
            n %= 10000000;
        }
        if ((n / 100000) > 0) {
            words += numberToWords(n / 100000) + " Lakh ";
            n %= 100000;
        }
        if ((n / 1000) > 0) {
            words += numberToWords(n / 1000) + " Thousand ";
            n %= 1000;
        }
        if ((n / 100) > 0) {
            words += numberToWords(n / 100) + " Hundred ";
            n %= 100;
        }
        if (n > 0) {
            if (!words.isEmpty()) words += "and ";
            if (n < 20) words += units[(int) n];
            else {
                words += tens[(int) n / 10];
                if ((n % 10) > 0) words += " " + units[(int) n % 10];
            }
        }
        return words.trim();
    }
}
