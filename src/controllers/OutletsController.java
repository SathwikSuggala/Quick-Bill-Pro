package controllers;

import DataBase.OutletsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Outlet;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OutletsController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private TextField gstinField;
    @FXML private TableView<Outlet> outletsTable;
    @FXML private TableColumn<Outlet, Integer> idColumn;
    @FXML private TableColumn<Outlet, String> nameColumn;
    @FXML private TableColumn<Outlet, String> addressColumn;
    @FXML private TableColumn<Outlet, String> contactColumn;
    @FXML private TableColumn<Outlet, String> emailColumn;
    @FXML private TableColumn<Outlet, String> gstinColumn;
    @FXML private TableColumn<Outlet, Void> actionColumn;
//    @FXML private TableColumn<Outlet, Integer> billCountColumn;

    private final OutletsDataBase outletsDB = new OutletsDataBase();
    private ObservableList<Outlet> outletsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        gstinColumn.setCellValueFactory(new PropertyValueFactory<>("gstin"));
//        billCountColumn.setCellValueFactory(new PropertyValueFactory<>("billCount"));

        // Setup action column with update button
        setupActionColumn();

        // Set the table's items
        outletsTable.setItems(outletsList);

        // Load initial data
        refreshOutlets();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<Outlet, Void>() {
            private final Button updateButton = new Button("Update");
            {
                updateButton.setOnAction(event -> {
                    Outlet outlet = getTableView().getItems().get(getIndex());
                    openUpdateDialog(outlet);
                });
                updateButton.setStyle("-fx-background-color: #2C5364; -fx-text-fill: white; -fx-background-radius: 5;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : updateButton);
            }
        });
    }

    private void openUpdateDialog(Outlet outlet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateOutletDialog.fxml"));
            Parent root = loader.load();

            UpdateOutletDialogController controller = loader.getController();
            controller.setOutlet(outlet);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(outletsTable.getScene().getWindow());
            dialogStage.setTitle("Update Outlet");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            // Refresh the table if update was successful
            if (controller.isUpdateSuccessful()) {
                refreshOutlets();
            }

        } catch (IOException e) {
            showAlert("Error", "Failed to open update dialog: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddOutletClicked() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String gstin = gstinField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || contact.isEmpty() || email.isEmpty() || gstin.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        outletsDB.createNewOutlet(name, address, contact, email, gstin);
        clearFields();
        refreshOutlets();
        showAlert("Success", "Outlet added successfully", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onRefreshClicked() {
        refreshOutlets();
    }

    private void refreshOutlets() {
        outletsList.clear();
        outletsList.addAll(outletsDB.getAllOutlets());
    }

    private void clearFields() {
        nameField.clear();
        addressField.clear();
        contactField.clear();
        emailField.clear();
        gstinField.clear();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 