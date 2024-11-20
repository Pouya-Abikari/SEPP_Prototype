package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private ImageView logo;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private TextField usernameField;

    @FXML
    private CheckBox showPasswordCheckBox;

    private final String correctEmail = "johndoe@mail.com";
    private final String correctPassword = "password1";

    @FXML
    private void handleLogin() {
        String enteredEmail = usernameField.getText();
        String enteredPassword = passwordField.getText();
        String enteredVisiblePassword = visiblePasswordField.getText();

        if (enteredEmail.equals(correctEmail) && enteredPassword.equals(correctPassword) || enteredEmail.equals(correctEmail) && enteredVisiblePassword.equals(correctPassword)) {
            // Login successful, navigate to the home_screen
            navigateToHomeScreen();
        } else {
            // Show an error message
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid email or password!", ButtonType.OK);
            alert.setHeaderText("Error");
            alert.showAndWait();
        }
    }

    private void navigateToHomeScreen() {
        try {
            // Load the home_screen FXML using the correct path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/home_screen.fxml"));

            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found at: /se_prototype/se_prototype/home_screen.fxml");
            }

            Scene homeScene = new Scene(loader.load(),400, 711);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(homeScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load the home screen.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize() {
        setupLogo();
    }

    @FXML
    private void setupLogo() {
        try {
            // Correct the resource path
            Image image = new Image(getClass().getResourceAsStream("/img.png")); // Adjust path if img.png is in a subfolder
            logo.setImage(image);
            logo.setFitWidth(300); // Adjust as needed
            logo.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void togglePasswordVisibility() {
        // Toggle password visibility
        boolean showPassword = showPasswordCheckBox.isSelected();
        if (showPassword) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }

    @FXML
    private void switchToSignup() {
        // Switch to the signup view
        System.out.println("Switching to signup view");
    }

    @FXML
    private void forgotPassword() {
        // Handle forgot password functionality
        System.out.println("Forgot password clicked");
    }
}