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
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.BillViewItem;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import java.util.Arrays;
import java.time.Month;
import java.util.List;

public class ReportsController implements Initializable {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField billIdSearchField;
    @FXML private Button billIdSearchButton;
    @FXML private TableView<BillViewItem> salesReportTable;
    @FXML private TableColumn<BillViewItem, String> billDateColumn;
    @FXML private TableColumn<BillViewItem, Integer> billIdColumn;
    @FXML private TableColumn<BillViewItem, String> outletNameColumn;
    @FXML private TableColumn<BillViewItem, Double> totalCGSTColumn;
    @FXML private TableColumn<BillViewItem, Double> totalSGSTColumn;
    @FXML private TableColumn<BillViewItem, Double> totalAmountColumn;
    @FXML private TableColumn<BillViewItem, Void> viewBillColumn;
    @FXML private ComboBox<models.Outlet> outletComboBox;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private Button filterButton;
    @FXML private Button clearButton;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;

    private final ReportsDataBase reportsDataBase = new ReportsDataBase();
    private ObservableList<models.Outlet> allOutlets = FXCollections.observableArrayList();
    private int currentPage = 1;
    private final int pageSize = 20;
    private boolean hasNextPage = false;

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

        // Populate outlets, months, years
        setupOutletComboBox();
        setupMonthYearComboBoxes();
        
        // Load initial report for current month and year
        Platform.runLater(this::onFilterClicked);
        prevPageButton.setDisable(true);
        nextPageButton.setDisable(true);
    }

    private void setupOutletComboBox() {
        // Load all outlets (simulate DB call)
        allOutlets = FXCollections.observableArrayList(new DataBase.OutletsDataBase().getAllOutlets());
        outletComboBox.setConverter(new javafx.util.StringConverter<models.Outlet>() {
            @Override
            public String toString(models.Outlet outlet) {
                return outlet != null ? outlet.getName() : "";
            }
            @Override
            public models.Outlet fromString(String string) { return null; }
        });
        outletComboBox.setEditable(false);
        outletComboBox.setOnMouseClicked(e -> showOutletSearchPopup());
        outletComboBox.setPromptText("Click to select outlet...");
    }

    private void showOutletSearchPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(outletComboBox.getScene().getWindow());
        popupStage.setTitle("Select Outlet");

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 15; -fx-background-color: white;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search outlets...");
        searchField.setPrefWidth(300);

        ListView<models.Outlet> outletListView = new ListView<>();
        outletListView.setPrefHeight(300);
        outletListView.setItems(allOutlets);
        outletListView.setCellFactory(param -> new ListCell<models.Outlet>() {
            @Override
            protected void updateItem(models.Outlet outlet, boolean empty) {
                super.updateItem(outlet, empty);
                setText(empty || outlet == null ? null : outlet.getName() + " - " + outlet.getAddress());
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                outletListView.setItems(allOutlets);
            } else {
                ObservableList<models.Outlet> filtered = FXCollections.observableArrayList();
                for (models.Outlet outlet : allOutlets) {
                    if (outlet.getName().toLowerCase().contains(newVal.toLowerCase()) ||
                        outlet.getAddress().toLowerCase().contains(newVal.toLowerCase())) {
                        filtered.add(outlet);
                    }
                }
                outletListView.setItems(filtered);
            }
        });

        outletListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                models.Outlet selected = outletListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    outletComboBox.setValue(selected);
                    popupStage.close();
                }
            }
        });

        HBox buttonBox = new HBox(10);
        Button selectButton = new Button("Select");
        Button cancelButton = new Button("Cancel");
        selectButton.setOnAction(e -> {
            models.Outlet selected = outletListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                outletComboBox.setValue(selected);
                popupStage.close();
            }
        });
        cancelButton.setOnAction(e -> popupStage.close());
        buttonBox.getChildren().addAll(selectButton, cancelButton);
        root.getChildren().addAll(searchField, outletListView, buttonBox);
        Scene scene = new Scene(root);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void setupMonthYearComboBoxes() {
        // Months
        monthComboBox.getItems().clear();
        String[] months = Arrays.stream(Month.values())
            .map(m -> m.toString().substring(0,1) + m.toString().substring(1).toLowerCase())
            .toArray(String[]::new);
        monthComboBox.getItems().addAll(months);
        String currentMonth = LocalDate.now().getMonth().toString();
        monthComboBox.setValue(currentMonth.substring(0,1) + currentMonth.substring(1).toLowerCase());

        // Years (last 10 years)
        yearComboBox.getItems().clear();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear; y >= currentYear - 10; y--) {
            yearComboBox.getItems().add(y);
        }
        yearComboBox.setValue(currentYear);
    }

    @FXML
    private void onFilterClicked() {
        models.Outlet selectedOutlet = outletComboBox.getValue();
        String selectedMonth = monthComboBox.getValue();
        Integer selectedYear = yearComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (selectedYear == null && startDate == null) {
            showAlert("Error", "A year or date range must be selected.");
            return;
        }

        try {
            ObservableList<BillViewItem> reportData;
            hasNextPage = false;
            int offset = (currentPage - 1) * pageSize;
            List<BillViewItem> pageData;

            if (selectedOutlet != null) {
                if (selectedYear == null) {
                    showAlert("Error", "Please select a year to filter for the selected outlet.");
                    return;
                }
                // Pagination for outlet+year
                pageData = reportsDataBase.getBillsReportByOutletAndYearPaginated(selectedOutlet.getName(), selectedYear, pageSize + 1, offset);
            } else if (startDate != null && endDate != null) {
                // No outlet selected, use date range if provided
                pageData = reportsDataBase.getBillsReportPaginated(startDate, endDate, pageSize + 1, offset);
            } else if (selectedMonth != null && selectedYear != null) {
                // Fallback to month and year
                LocalDate monthStart = LocalDate.of(selectedYear, Month.valueOf(selectedMonth.toUpperCase()), 1);
                LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
                pageData = reportsDataBase.getBillsReportPaginated(monthStart, monthEnd, pageSize + 1, offset);
            } else if (selectedYear != null) {
                // Fallback to year only
                LocalDate yearStart = LocalDate.of(selectedYear, 1, 1);
                LocalDate yearEnd = LocalDate.of(selectedYear, 12, 31);
                pageData = reportsDataBase.getBillsReportPaginated(yearStart, yearEnd, pageSize + 1, offset);
            } else {
                salesReportTable.setItems(FXCollections.observableArrayList());
                return;
            }

            if (pageData.size() > pageSize) {
                hasNextPage = true;
                pageData = pageData.subList(0, pageSize);
            }
            reportData = FXCollections.observableArrayList(pageData);

            salesReportTable.setItems(reportData);
            updatePageInfoLabel();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to generate report: " + e.getMessage());
        }
    }

    @FXML
    private void onClearClicked() {
        outletComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        
        // Reset to current month and year and fetch report
        setupMonthYearComboBoxes();
        currentPage = 1;
        onFilterClicked();
    }

    @FXML
    private void onPrevPageClicked() {
        if (currentPage > 1) {
            currentPage--;
            onFilterClicked();
        }
    }

    @FXML
    private void onNextPageClicked() {
        if (hasNextPage) {
            currentPage++;
            onFilterClicked();
        }
    }

    @FXML
    private void onBillIdSearchClicked() {
        String billIdText = billIdSearchField.getText().trim();
        if (billIdText.isEmpty()) {
            // If empty, revert to normal filter
            onFilterClicked();
            return;
        }
        try {
            int billId = Integer.parseInt(billIdText);
            BillViewItem bill = reportsDataBase.getBillViewItemById(billId);
            ObservableList<BillViewItem> result = FXCollections.observableArrayList();
            if (bill != null) {
                result.add(bill);
            }
            salesReportTable.setItems(result);
            pageInfoLabel.setText("Search Result");
            prevPageButton.setDisable(true);
            nextPageButton.setDisable(true);
        } catch (NumberFormatException e) {
            showAlert("Invalid Bill ID", "Please enter a valid numeric Bill ID.");
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

    private void updatePageInfoLabel() {
        pageInfoLabel.setText("Page " + currentPage + (hasNextPage ? " (more)" : ""));
        prevPageButton.setDisable(currentPage == 1);
        nextPageButton.setDisable(!hasNextPage);
    }
} 