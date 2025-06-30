package controllers;

import DataBase.InletsDataBase;
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
import models.Inlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InletsController implements Initializable {

    private static final Logger logger = LogManager.getLogger(InletsController.class);

    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private TableView<Inlet> inletsTable;
    @FXML private TableColumn<Inlet, Integer> idColumn;
    @FXML private TableColumn<Inlet, String> nameColumn;
    @FXML private TableColumn<Inlet, String> contactColumn;
    @FXML private TableColumn<Inlet, Integer> productCountColumn;
    @FXML private TableColumn<Inlet, Void> actionColumn;

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

        // Setup action column with update button
        setupActionColumn();

        // Set the table's items
        inletsTable.setItems(inletsList);

        // Load initial data
        refreshInlets();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<Inlet, Void>() {
            private final Button updateButton = new Button("Update");
            {
                updateButton.setOnAction(event -> {
                    Inlet inlet = getTableView().getItems().get(getIndex());
                    openUpdateDialog(inlet);
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

    private void openUpdateDialog(Inlet inlet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateInletDialog.fxml"));
            Parent root = loader.load();

            UpdateInletDialogController controller = loader.getController();
            controller.setInlet(inlet);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(inletsTable.getScene().getWindow());
            dialogStage.setTitle("Update Inlet");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            // Refresh the table if update was successful
            if (controller.isUpdateSuccessful()) {
                refreshInlets();
            }

        } catch (IOException e) {
            showAlert("Error", "Failed to open update dialog: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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
        logger.info("Inlets List: {}", inletsList);
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