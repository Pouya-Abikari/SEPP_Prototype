package se_prototype.se_prototype;

import javafx.application.Platform;
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
    private String userFile;

    @FXML
    public void initialize() {
        setupPasswordToggle();
        setupLogo();

        // Clear the user file when the application starts
        emptyCurrentUserFile();

        // Determine the user file for this session
        userFile = determineUserFile();
        if (userFile == null) {
            showErrorAndExit("Both user files are already in use. Please try again later.");
        }

        // Register a shutdown hook to clear the file
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                clearFile(userFile);
                System.out.println("File cleared on shutdown: " + userFile);
            } catch (IOException e) {
                System.err.println("Failed to clear file on shutdown: " + userFile);
                e.printStackTrace();
            }
        }));

        // Hide the error label initially
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);

        // Set login button action
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

    private void emptyCurrentUserFile() {
        if (userFile == null || userFile.isEmpty()) {
            System.err.println("User file is not initialized or is invalid.");
            return; // Safely exit without throwing an exception
        }

        File file = new File(userFile);
        if (!file.exists()) {
            System.out.println("User file does not exist. Nothing to clear.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(""); // Clear the content of the user file
            System.out.println("Successfully cleared user file: " + userFile);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Failed to clear the user file: " + userFile);
        }
    }

    private String determineUserFile() {
        String[] userFiles = {"src/main/resources/current_user_1.txt", "src/main/resources/current_user_2.txt"};

        try {
            for (String userFilePath : userFiles) {
                File userFile = new File(userFilePath);

                if (isFileEmpty(userFile)) {
                    createFileIfNotExists(userFile);
                    writeFile(userFile, "1"); // Write "1" to mark it as in use
                    System.out.println("User file set to: " + userFilePath); // Log relative path
                    return userFilePath; // Return relative path instead of absolute path
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error determining user file: " + e.getMessage());
        }

        // Both files are in use
        System.err.println("Both user files are already in use.");
        showErrorAndExit("Both user files are in use. Please try again later.");
        return null;
    }

    private void createFileIfNotExists(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private boolean isFileEmpty(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine() == null;
        } catch (IOException e) {
            return true; // Treat as empty if unreadable
        }
    }

    private void writeFile(File file, String content) throws IOException {
        if (!file.exists()) {
            System.out.println("File does not exist. Creating file: " + file.getAbsolutePath());
            file.createNewFile();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(content);
            writer.flush(); // Ensure the content is written immediately
            System.out.println("Successfully wrote to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + file.getAbsolutePath());
            throw e;
        }
    }

    private void showErrorAndExit(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            Platform.exit();
        });
    }

    private User authenticateUser(String email, String password) {
        String USER_FILE = "src/main/resources/users.txt";
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, false))) { // Overwrite file
            writer.write(user.getName() + "," +
                    user.getEmail() + "," +
                    user.getPassword() + "," +
                    "[\"" + String.join("\",\"", user.getAddresses()) + "\"]," + // Serialize addresses
                    "\"" + user.getCurrentAddress() + "\"," + // Serialize current address
                    "[" + String.join(",", intArrayToStringArray(user.getOrderID())) + "]," +
                    user.getCurrentOrderID() + "," +
                    user.getErrorCase());
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
            HomeScreenController homeScreenController = loader.getController();
            homeScreenController.setUserFile(userFile);
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

    private void clearFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write(""); // Write nothing to empty the file
        }
    }

    private void showErrorAlert(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}