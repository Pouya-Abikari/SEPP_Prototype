package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ForgotPwController {

    @FXML
    private Button submitButton;
    @FXML
    private Hyperlink backButton;
    @FXML
    private TextField emailField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;

    @FXML
    public void initialize() {
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
        successLabel.setManaged(false);
        successLabel.setVisible(false);
    }

    @FXML
    private void handleGoBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loader.load());
            loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("SSH APP");
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load the login screen.");
        }
    }

    @FXML
    private void handleSubmit() {
        String email = emailField.getText();
        if (email == null || email.isEmpty()) {
            showError("Please enter an email.");
            return;
        }
        if (!isValidEmail(email)) {
            showError("Invalid email format.");
            return;
        }

        // Check if the email exists in users.txt
        if (checkEmailExists(email)) {
            showSuccess("A reset link has been sent to your email.");
        } else {
            showError("Email not found.");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    private boolean checkEmailExists(String email) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/users.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[1].equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Email not found
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
