package controllers;

import DataBase.InletsDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Inlet;

import java.net.URL;
import java.util.ResourceBundle;

public class InletsController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private TableView<Inlet> inletsTable;
    @FXML private TableColumn<Inlet, Integer> idColumn;
    @FXML private TableColumn<Inlet, String> nameColumn;
    @FXML private TableColumn<Inlet, String> contactColumn;
    @FXML private TableColumn<Inlet, Integer> productCountColumn;

    //private final InletsDataBase inletsDB = new InletsDataBase();
    InletsDataBase inletsDataBase = new InletsDataBase();
    private ObservableList<Inlet> inletsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        productCountColumn.setCellValueFactory(new PropertyValueFactory<>("productCount"));

        // Set the table's items
        inletsTable.setItems(inletsList);

        // Load initial data
        refreshInlets();
    }

    @FXML
    private void onAddInletClicked() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        inletsDataBase.addInlet(name, contact);
        clearFields();
        refreshInlets();
        showAlert("Success", "Inlet added successfully", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onRefreshClicked() {
        refreshInlets();
    }

    private void refreshInlets() {
        inletsList.clear();
        inletsList.addAll(inletsDataBase.fetchAllInletsWithProductCount());

        // Optional: print once for debugging
        System.out.println(inletsList);
    }


    private void clearFields() {
        nameField.clear();
        contactField.clear();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 