package controllers;

import DataBase.PurchasesDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Purchase;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class PurchaseHistoryDialogController implements Initializable {

    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<LocalDate> dateComboBox;
    @FXML private ComboBox<String> productFilterComboBox;
    @FXML private TableView<Purchase> purchasesTable;
    @FXML private TableColumn<Purchase, Integer> purchaseIdColumn;
    @FXML private TableColumn<Purchase, String> inletNameColumn;
    @FXML private TableColumn<Purchase, String> productNameColumn;
    @FXML private TableColumn<Purchase, Integer> quantityColumn;
    @FXML private TableColumn<Purchase, Double> costPriceColumn;
    @FXML private TableColumn<Purchase, LocalDate> purchaseDateColumn;

    private final PurchasesDataBase purchasesDB = new PurchasesDataBase();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
    private ObservableList<LocalDate> allDates;
    private ObservableList<Purchase> allPurchases;
    private FilteredList<Purchase> filteredPurchases;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        purchaseIdColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseId"));
        inletNameColumn.setCellValueFactory(new PropertyValueFactory<>("inletName"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        costPriceColumn.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        purchaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));

        // Format the date column
        purchaseDateColumn.setCellFactory(col -> new TableCell<Purchase, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty ? null : dateFormatter.format(date));
            }
        });

        // Initialize month names
        ObservableList<String> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            months.add(YearMonth.of(2000, i).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }
        monthComboBox.setItems(months);

        // Load all dates
        allDates = purchasesDB.fetchAllPurchaseDates();
        
        // Initialize years from available dates
        TreeSet<Integer> years = new TreeSet<>();
        for (LocalDate date : allDates) {
            years.add(date.getYear());
        }
        yearComboBox.setItems(FXCollections.observableArrayList(years));

        // Initialize filtered purchases list
        allPurchases = FXCollections.observableArrayList();
        filteredPurchases = new FilteredList<>(allPurchases);
        purchasesTable.setItems(filteredPurchases);

        // Add listeners for filtering
        yearComboBox.setOnAction(e -> updateDateFilter());
        monthComboBox.setOnAction(e -> updateDateFilter());
        dateComboBox.setOnAction(e -> {
            LocalDate selectedDate = dateComboBox.getValue();
            if (selectedDate != null) {
                loadPurchasesForDate(selectedDate);
            }
        });

        // Add listener for product filter
        productFilterComboBox.setOnAction(e -> updateProductFilter());
    }

    private void updateDateFilter() {
        Integer selectedYear = yearComboBox.getValue();
        String selectedMonth = monthComboBox.getValue();
        
        ObservableList<LocalDate> filteredDates = FXCollections.observableArrayList();
        
        if (selectedYear != null && selectedMonth != null) {
            // Filter by both year and month
            int monthValue = getMonthValue(selectedMonth);
            filteredDates.addAll(allDates.stream()
                .filter(date -> date.getYear() == selectedYear && date.getMonthValue() == monthValue)
                .collect(Collectors.toList()));
        } else if (selectedYear != null) {
            // Filter by year only
            filteredDates.addAll(allDates.stream()
                .filter(date -> date.getYear() == selectedYear)
                .collect(Collectors.toList()));
        } else if (selectedMonth != null) {
            // Filter by month only
            int monthValue = getMonthValue(selectedMonth);
            filteredDates.addAll(allDates.stream()
                .filter(date -> date.getMonthValue() == monthValue)
                .collect(Collectors.toList()));
        } else {
            // No filters, show all dates
            filteredDates.addAll(allDates);
        }
        
        dateComboBox.setItems(filteredDates);
        dateComboBox.setValue(null);
    }

    private int getMonthValue(String monthName) {
        return YearMonth.of(2000, 1).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 1 :
               YearMonth.of(2000, 2).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 2 :
               YearMonth.of(2000, 3).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 3 :
               YearMonth.of(2000, 4).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 4 :
               YearMonth.of(2000, 5).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 5 :
               YearMonth.of(2000, 6).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 6 :
               YearMonth.of(2000, 7).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 7 :
               YearMonth.of(2000, 8).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 8 :
               YearMonth.of(2000, 9).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 9 :
               YearMonth.of(2000, 10).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 10 :
               YearMonth.of(2000, 11).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName) ? 11 : 12;
    }

    private void loadPurchasesForDate(LocalDate date) {
        allPurchases.setAll(purchasesDB.fetchPurchasesByDate(date));
        
        // Update product filter options
        Set<String> productNames = allPurchases.stream()
            .map(Purchase::getProductName)
            .collect(Collectors.toCollection(TreeSet::new));
        productFilterComboBox.setItems(FXCollections.observableArrayList(productNames));
        productFilterComboBox.setValue(null);
    }

    private void updateProductFilter() {
        String selectedProduct = productFilterComboBox.getValue();
        if (selectedProduct != null) {
            filteredPurchases.setPredicate(purchase -> 
                purchase.getProductName().equals(selectedProduct));
        } else {
            filteredPurchases.setPredicate(null);
        }
    }

    @FXML
    private void onClearProductFilterClicked() {
        productFilterComboBox.setValue(null);
        filteredPurchases.setPredicate(null);
    }

    @FXML
    private void onCloseClicked() {
        Stage stage = (Stage) dateComboBox.getScene().getWindow();
        stage.close();
    }
} 