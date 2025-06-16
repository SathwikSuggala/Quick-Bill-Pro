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
            String query = "SELECT * FROM users WHERE name = ? AND email = ?";
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
