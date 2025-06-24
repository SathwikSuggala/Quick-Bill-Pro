package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class HomeController {
    
    @FXML
    private BorderPane mainBorderPane;
    
    @FXML
    private StackPane mainContent;

    @FXML
    private void onDashboardClicked() {
        loadPage("/views/dashboard.fxml");
    }

    @FXML
    private void onBillingClicked() {
        loadPage("/views/billing.fxml");
    }

    @FXML
    private void onCreditManagementClicked() {
        loadPage("/views/credit_management.fxml");
    }

    @FXML
    private void onInletsClicked() {
        loadPage("/views/inlets.fxml");
    }

    @FXML
    private void onOutletsClicked() {
        loadPage("/views/outlets.fxml");
    }

    @FXML
    private void onProductsClicked() {
        loadPage("/views/products.fxml");
    }

    @FXML
    private void onReportsClicked() {
        loadPage("/views/reports.fxml");
    }

    @FXML
    private void onPaymentsClicked() {
        loadPage("/views/payments_view.fxml");
    }

    public void initialize() {
        loadPage("/views/dashboard.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            if (mainContent != null) {
                mainContent.getChildren().clear();
                mainContent.getChildren().add(FXMLLoader.load(getClass().getResource(fxmlPath)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 