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

public class LoginController {

    @FXML
    private Button loginButton;
    @FXML
    private ImageView logo;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private TextField usernameField;
    @FXML
    private ImageView showPasswordButton;
    @FXML
    private Label errorLabel;

    private boolean isPasswordVisible = false;

    private final String USER_FILE = "src/main/resources/users.txt";

    @FXML
    public void initialize() {
        setupPasswordToggle();
        emptyCurrentUser();
        setupLogo();
        errorLabel.setManaged(false);
        errorLabel.setVisible(false); // Hide the error label initially
        loginButton.setOnAction(event -> handleLogin());
    }

    private void setupLogo() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/logo.png"));
            logo.setImage(image);
            logo.setFitWidth(300);
            logo.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Error loading logo image: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Failed to load logo image.");
        }
    }

    private void emptyCurrentUser() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/current_user.txt", false))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Failed to empty the current user.");
        }
    }

    private User authenticateUser(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Use regex or manual parsing to handle the format
                String[] userData = parseLine(line);
                if (userData[1].equals(email) && userData[2].equals(password)) {
                    return createUserFromData(userData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] parseLine(String line) {
        // Use regex to split by commas, respecting quoted substrings
        String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        return line.split(regex);
    }

    private String[] parseAddresses(String addressesField) {
        // Ensure quotes are removed
        if (addressesField.startsWith("\"") && addressesField.endsWith("\"")) {
            addressesField = addressesField.substring(1, addressesField.length() - 1);
        } else {
            throw new IllegalArgumentException("Addresses field is not properly formatted: " + addressesField);
        }

        // Split by semicolon
        return addressesField.split(";");
    }

    private User createUserFromData(String[] data) {
        // Parse basic fields
        String name = data[0];
        String email = data[1];
        String password = data[2];

        // Parse addresses
        String[] addresses = parseAddresses(data[3]);

        // Parse current address
        String currentAddress = data[4].replace("\"", "").trim(); // Remove quotes from the current address

        // Parse order IDs
        int[] orderIDs = parseOrderIDs(data[5]);

        // Parse current order ID and error case
        int currentOrderID = Integer.parseInt(data[6].trim());
        int errorCase = Integer.parseInt(data[7].trim());

        return new User(name, email, password, addresses, currentAddress, orderIDs, currentOrderID, errorCase);
    }

    private int[] parseOrderIDs(String orderIDsField) {
        // Remove quotes and brackets
        if (orderIDsField.startsWith("\"") && orderIDsField.endsWith("\"")) {
            orderIDsField = orderIDsField.substring(1, orderIDsField.length() - 1);
        }

        if (orderIDsField.isEmpty()) {
            return new int[0]; // Handle empty order list
        }

        // Split by semicolon and parse integers
        String[] orderIDsArray = orderIDsField.split(";");
        int[] result = new int[orderIDsArray.length];
        for (int i = 0; i < orderIDsArray.length; i++) {
            try {
                result[i] = Integer.parseInt(orderIDsArray[i].trim());
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Invalid order ID format: " + orderIDsArray[i].trim());
            }
        }
        return result;
    }

    @FXML
    private void handleLogin() {
        String email = usernameField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();

        User user = authenticateUser(email, password);
        if (user != null) {
            saveCurrentUser(user); // Save the logged-in user to current_user.txt
            System.out.println("Welcome, " + user.getName());
            navigateToHomeScreen(); // Navigate to the home screen
        } else {
            showErrorAlert("Invalid email or password. Please try again.");
        }
    }

    private void saveCurrentUser(User user) {
        String CURRENT_USER_FILE = "src/main/resources/current_user.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CURRENT_USER_FILE, false))) { // Overwrite file
            // Format the user information into the specified format
            String formattedAddresses = (user.getAddresses() != null && user.getAddresses().length > 0)
                    ? String.join(";", user.getAddresses())
                    : "";  // If no addresses, leave it empty

            String currentAddress = (user.getCurrentAddress() != null) ? user.getCurrentAddress() : "";  // If null, set empty

            String formattedOrderIDs = (user.getOrderID() != null && user.getOrderID().length > 0)
                    ? String.join(";", intArrayToStringArray(user.getOrderID()))
                    : "";  // If no orders, leave it empty

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

            // Write the formatted string to the file
            writer.write(userString);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] intArrayToStringArray(int[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    private void navigateToHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/home_screen.fxml"));
            Scene homeScene = new Scene(loader.load(), 400, 711);
            homeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(homeScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Failed to load the home screen.");
        }
    }

    private void setupPasswordToggle() {
        showPasswordButton.setImage(new Image(getClass().getResourceAsStream("/hidden.png")));
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
    }

    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            showPasswordButton.setImage(new Image(getClass().getResourceAsStream("/hidden.png")));
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setManaged(true);
            passwordField.setVisible(true);
            visiblePasswordField.setManaged(false);
            visiblePasswordField.setVisible(false);
        } else {
            // Show password
            showPasswordButton.setImage(new Image(getClass().getResourceAsStream("/eye.png")));
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setManaged(true);
            visiblePasswordField.setVisible(true);
            passwordField.setManaged(false);
            passwordField.setVisible(false);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void switchToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/signup.fxml"));
            Scene signupScene = new Scene(loader.load(), 400, 711);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(signupScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Failed to load the sign-up screen.");
        }
    }

    @FXML
    private void forgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/forgot_pw.fxml")); // Corrected path
            Scene forgotPasswordScene = new Scene(loader.load(), 400, 711);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(forgotPasswordScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Failed to load the forgot password screen.");
        }
    }

    private void showErrorAlert(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}