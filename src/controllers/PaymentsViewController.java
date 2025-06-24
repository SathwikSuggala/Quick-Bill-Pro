package controllers;

import DataBase.OutletsDataBase;
import DataBase.PaymentsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.application.Platform;

import models.CreditPaymentView;
import models.Outlet;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentsViewController implements Initializable {

    @FXML private ComboBox<Outlet> outletComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button filterButton;
    @FXML private Button clearButton;
    @FXML private TableView<CreditPaymentView> paymentsTable;
    @FXML private TableColumn<CreditPaymentView, Integer> paymentIdColumn;
    @FXML private TableColumn<CreditPaymentView, String> outletNameColumn;
    @FXML private TableColumn<CreditPaymentView, Integer> billIdColumn;
    @FXML private TableColumn<CreditPaymentView, Double> paymentAmountColumn;
    @FXML private TableColumn<CreditPaymentView, String> paymentModeColumn;
    @FXML private TableColumn<CreditPaymentView, String> paymentDateColumn;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;

    private PaymentsDataBase paymentsDB = new PaymentsDataBase();
    private OutletsDataBase outletsDB = new OutletsDataBase();
    private ObservableList<Outlet> allOutlets = FXCollections.observableArrayList();
    
    private int currentPage = 1;
    private final int pageSize = 20;
    private boolean hasNextPage = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupOutletComboBox();
        Platform.runLater(this::loadInitialData);
    }

    private void setupTable() {
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        outletNameColumn.setCellValueFactory(new PropertyValueFactory<>("outletName"));
        billIdColumn.setCellValueFactory(new PropertyValueFactory<>("billId"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
        paymentModeColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMode"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
    }

    private void setupOutletComboBox() {
        allOutlets = FXCollections.observableArrayList(outletsDB.getAllOutlets());
        outletComboBox.setConverter(new javafx.util.StringConverter<Outlet>() {
            @Override public String toString(Outlet outlet) { return outlet != null ? outlet.getName() : ""; }
            @Override public Outlet fromString(String string) { return null; }
        });
        outletComboBox.setEditable(false);
        outletComboBox.setOnMouseClicked(e -> showOutletSearchPopup());
        outletComboBox.setPromptText("Click to select outlet...");
    }

    private void showOutletSearchPopup() {
        // This is the same popup logic from ReportsController and can be extracted to a utility class later
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(outletComboBox.getScene().getWindow());
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 15; -fx-background-color: white;");
        TextField searchField = new TextField();
        searchField.setPromptText("Search outlets...");
        ListView<Outlet> outletListView = new ListView<>(allOutlets);
        
        outletListView.setCellFactory(param -> new ListCell<Outlet>() {
            @Override
            protected void updateItem(Outlet outlet, boolean empty) {
                super.updateItem(outlet, empty);
                if (empty || outlet == null) {
                    setText(null);
                } else {
                    setText(outlet.getName() + " - " + outlet.getAddress());
                }
            }
        });
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                outletListView.setItems(allOutlets);
            } else {
                outletListView.setItems(allOutlets.filtered(o -> o.getName().toLowerCase().contains(newVal.toLowerCase())));
            }
        });
        
        outletListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                outletComboBox.setValue(outletListView.getSelectionModel().getSelectedItem());
                popupStage.close();
            }
        });

        root.getChildren().addAll(searchField, outletListView);
        popupStage.setScene(new Scene(root));
        popupStage.show();
    }
    
    private void loadInitialData() {
        startDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        endDatePicker.setValue(LocalDate.now());
        onFilterClicked();
    }

    @FXML
    private void onFilterClicked() {
        fetchPayments();
    }

    @FXML
    private void onClearClicked() {
        outletComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        currentPage = 1;
        loadInitialData();
    }

    @FXML
    private void onPrevPageClicked() {
        if (currentPage > 1) {
            currentPage--;
            fetchPayments();
        }
    }

    @FXML
    private void onNextPageClicked() {
        if (hasNextPage) {
            currentPage++;
            fetchPayments();
        }
    }

    private void fetchPayments() {
        String outletName = outletComboBox.getValue() != null ? outletComboBox.getValue().getName() : null;
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        int offset = (currentPage - 1) * pageSize;

        try {
            List<CreditPaymentView> pageData = paymentsDB.getCreditPayments(outletName, startDate, endDate, pageSize + 1, offset);
            
            hasNextPage = pageData.size() > pageSize;
            if (hasNextPage) {
                pageData = pageData.subList(0, pageSize);
            }
            
            paymentsTable.setItems(FXCollections.observableArrayList(pageData));
            updatePageInfo();
        } catch (SQLException e) {
            e.printStackTrace();
            // Show alert
        }
    }
    
    private void updatePageInfo() {
        pageInfoLabel.setText("Page " + currentPage);
        prevPageButton.setDisable(currentPage == 1);
        nextPageButton.setDisable(!hasNextPage);
    }
} 