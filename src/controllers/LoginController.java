package controllers;

import DataBase.DBConnection;
import DataBase.InventoryDBSetup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void onLoginClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        //InventoryDBSetup.createDatabaseIfNotExists();
        InventoryDBSetup.createTablesIfNotExist();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE name = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);  // Use hashed password in real apps

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Close the login window
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.close();

                // Open the home page
                openHomePage();
            } else {
                showAlert("Login Failed", "Invalid credentials.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Something went wrong!");
        }
    }

    @FXML
    private void onCreateAccountClicked() {
        // Prompt for pass code
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Pass Code Required");
        dialog.setHeaderText("Enter the pass code to create an account");
        dialog.setContentText("Pass Code:");
        
        // Show dialog and capture input
        dialog.showAndWait().ifPresent(passCode -> {
            if ("9290872634".equals(passCode)) {
                // Pass code correct, show create account form
                openCreateAccountForm();
            } else {
                showAlert("Access Denied", "Incorrect pass code.");
            }
        });
    }

    private void openHomePage() {
        try {
            // Use the correct path resolution for the FXML file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/home.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            
            // Set the stage to full screen
            stage.setMaximized(true);
            stage.setTitle("QuickBillPro - Home");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open home page!");
        }
    }

    private void openCreateAccountForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/create_account.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Create Account");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open create account form!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
