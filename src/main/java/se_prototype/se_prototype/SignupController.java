package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import se_prototype.se_prototype.Model.User;

import java.io.*;

public class SignupController {

    @FXML
    private ImageView togglePasswordIcon_2;
    @FXML
    private ImageView togglePasswordIcon_1;
    @FXML
    private TextField visibleConfirmPasswordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private Hyperlink loginButton;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button signupButton;

    private boolean isPasswordVisible_1 = false;
    private boolean isPasswordVisible_2 = false;

    private final String USER_FILE = "src/main/resources/users.txt";

    @FXML
    public void initialize() {
        setupPasswordToggle();
        errorLabel.setManaged(false);
        errorLabel.setVisible(false); // Hide the error label initially
        signupButton.setOnAction(event -> handleSignup());
        loginButton.setOnAction(event -> switchToLogin());
    }
    @FXML
    public void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showError("Invalid email format.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.trim().equals(confirmPassword.trim())) {
            showError("Password and confirm Password must be the same.");
            return;
        }

        if (isUserExists(email)) {
            showError("A user with this email already exists.");
            return;
        }
        switchToSuccessPage();
        // Create a new user and save it to the file
        User newUser = new User(name, email, password, null, null, null, 0, 0);
        saveUserToFile(newUser);
        saveCurrentUser(newUser);
    }

    private void setupPasswordToggle() {
        togglePasswordIcon_1.setImage(new Image(getClass().getResourceAsStream("/hidden.png")));
        togglePasswordIcon_2.setImage(new Image(getClass().getResourceAsStream("/hidden.png")));

        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
        visibleConfirmPasswordField.setManaged(false);
        visibleConfirmPasswordField.setVisible(false);
    }



    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/login.fxml"));
            Scene loginScene = new Scene(loader.load(), 400, 711);
            loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) loginButton.getScene().getWindow(); // Get the current stage
            stage.setScene(loginScene); // Set the login page scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load the login page.");
        }
    }

    private void switchToSuccessPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/signup_successful.fxml"));
            Scene successScene = new Scene(loader.load(), 400, 711);
            successScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(successScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load the success page.");
        }
    }


    private boolean isUserExists(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length > 1 && userDetails[1].equalsIgnoreCase(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to check existing users.");
        }
        return false;
    }


    private void saveUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            // Ensure that addresses and order IDs are properly formatted as empty arrays if not present
            String formattedAddresses = (user.getAddresses() != null && user.getAddresses().length > 0)
                    ? String.join(";", user.getAddresses())
                    : "";  // If no addresses, leave it empty (not [])

            String currentAddress = (user.getCurrentAddress() != null) ? user.getCurrentAddress() : "";  // If null, set empty

            // Format order IDs as a string of integers, ensure they're in the correct format (e.g., 101;102;103)
            String formattedOrderIDs = (user.getOrderID() != null && user.getOrderID().length > 0)
                    ? String.join(";", intArrayToStringArray(user.getOrderID()))
                    : "";  // If no orders, leave it empty (not [])

            // Format the user information into the specified format
            String userString = String.format(
                    "%s,%s,%s,\"%s\",\"%s\",\"%s\",%d,%d",
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    formattedAddresses,
                    currentAddress,
                    formattedOrderIDs,
                    user.getCurrentOrderID(),
                    user.getErrorCase()
            );

            // Append the user information to the file
            writer.write(userString);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to save user.");
        }
    }

    private void saveCurrentUser(User user) {
        String CURRENT_USER_FILE = "src/main/resources/current_user.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CURRENT_USER_FILE, false))) {
            String userString = user.getName() + "," +
                    user.getEmail() + "," +
                    user.getPassword() + "," +
                    "[\"" + String.join(";", user.getAddresses()) + "\"]," +
                    "\"" + user.getCurrentAddress() + "\"," +
                    "[" + String.join(",", intArrayToStringArray(user.getOrderID())) + "]," +
                    user.getCurrentOrderID() + "," +
                    user.getErrorCase();
            writer.write(userString);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to save current user.");
        }
    }


    private String[] intArrayToStringArray(int[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    @FXML
    private void togglePasswordVisibility_1() {
        if (isPasswordVisible_1) {
            // Hide password
            togglePasswordIcon_1.setImage(new Image(getClass().getResourceAsStream("/hidden.png")));
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setManaged(true);
            passwordField.setVisible(true);
            visiblePasswordField.setManaged(false);
            visiblePasswordField.setVisible(false);
        } else {
            // Show password
            togglePasswordIcon_1.setImage(new Image(getClass().getResourceAsStream("/eye.png")));
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setManaged(true);
            visiblePasswordField.setVisible(true);
            passwordField.setManaged(false);
            passwordField.setVisible(false);
        }
        isPasswordVisible_1 = !isPasswordVisible_1;
    }

    @FXML
    private void togglePasswordVisibility_2() {
        if (isPasswordVisible_2) {
            // Hide confirm password
            togglePasswordIcon_2.setImage(new Image(getClass().getResourceAsStream("/hidden.png")));
            confirmPasswordField.setText(visibleConfirmPasswordField.getText());
            confirmPasswordField.setManaged(true);
            confirmPasswordField.setVisible(true);
            visibleConfirmPasswordField.setManaged(false);
            visibleConfirmPasswordField.setVisible(false);
        } else {
            // Show confirm password
            togglePasswordIcon_2.setImage(new Image(getClass().getResourceAsStream("/eye.png")));
            visibleConfirmPasswordField.setText(confirmPasswordField.getText());
            visibleConfirmPasswordField.setManaged(true);
            visibleConfirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(false);
            confirmPasswordField.setVisible(false);
        }
        isPasswordVisible_2 = !isPasswordVisible_2;
    }

    /*
    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/login.fxml"));
            Scene loginScene = new Scene(loader.load(), 400, 711);
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to navigate to login screen.");
        }
    }*/

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}