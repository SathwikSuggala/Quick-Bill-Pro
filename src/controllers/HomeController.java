package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class HomeController {
    
    @FXML
    private BorderPane mainBorderPane;
    
    @FXML
    private void onDashboardClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent dashboardView = loader.load();
            mainBorderPane.setCenter(dashboardView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load dashboard page!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onBillingClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/billing.fxml"));
            Parent billingView = loader.load();
            mainBorderPane.setCenter(billingView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load billing page!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCreditManagementClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/credit_management.fxml"));
            Parent creditManagementView = loader.load();
            mainBorderPane.setCenter(creditManagementView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load credit management page!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onInletsClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/inlets.fxml"));
            Parent inletsView = loader.load();
            mainBorderPane.setCenter(inletsView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load inlets page!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onOutletsClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/outlets.fxml"));
            Parent outletsView = loader.load();
            mainBorderPane.setCenter(outletsView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load outlets page!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onProductsClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/products.fxml"));
            Parent productsView = loader.load();
            mainBorderPane.setCenter(productsView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load products page!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onReportsClicked() {
        // TODO: Implement reports view
        showAlert("Reports", "Reports view will be implemented soon!");
    }

    @FXML
    private void onSettingsClicked() {
        // TODO: Implement settings view
        showAlert("Settings", "Settings view will be implemented soon!");
    }

    private void showAlert(String title, String content) {
        showAlert(title, content, Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 