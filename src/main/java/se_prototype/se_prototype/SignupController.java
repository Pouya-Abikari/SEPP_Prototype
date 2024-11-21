package se_prototype.se_prototype;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class SignupController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField reenterPasswordField;

    @FXML
    private Button signupButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Add action listeners to buttons in case FXML event handlers are missing
        signupButton.setOnAction(this::handleSignup);
        backButton.setOnAction(this::switchToLogin);
    }

    @FXML
    public void handleSignup(ActionEvent event) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String reenterPassword = reenterPasswordField.getText();

        // Check for empty fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || reenterPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill out all fields.");
            return;
        }

        // Validate email
        if (!email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address containing '@'.");
            return;
        }

        // Check if passwords match
        if (!password.equals(reenterPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match. Please try again.");
            return;
        }

        // Simulate a successful signup
        showAlert(Alert.AlertType.INFORMATION, "Signup Successful", "Welcome, " + firstName + "!");

        // Navigate to the home screen
        navigateToHomeScreen();
    }

    @FXML
    public void switchToLogin(ActionEvent event) {
        // Navigate to the login page when the "Back to Login" button is clicked
        navigateToLogin();
    }

    private void navigateToHomeScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home_screen.fxml"));
            Scene homeScreenScene = new Scene(fxmlLoader.load(), 400, 711);
            Stage stage = (Stage) signupButton.getScene().getWindow(); // Use signupButton to get the stage
            stage.setScene(homeScreenScene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load the home screen.");
            e.printStackTrace();
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load(), 400, 711);
            Stage stage = (Stage) signupButton.getScene().getWindow(); // Use signupButton to get the stage
            stage.setScene(loginScene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load the login page.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
