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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private String id;

    private boolean isPasswordVisible = false;

    private final String USER_FILE = "src/main/resources/users.txt";

    @FXML
    public void initialize() {
        setupPasswordToggle();
        setupLogo();
        errorLabel.setManaged(false);
        errorLabel.setVisible(false); // Hide the error label initially
        loginButton.setOnAction(event -> handleLogin());

        // Add a listener to handle window close event
        Platform.runLater(() -> {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("Window is closing. Cleaning up resources...");
                resetCurrentOrderID(); // Reset the current order ID for this user
                terminateCurrentProcess(); // Terminate only the current process
            });
        });
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

    private User authenticateUser(String email, String password) {
        boolean emailExists = false; // Track if the email exists in the file

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse the line to extract user data
                String[] userData = parseLine(line);

                if (userData[1].equals(email)) {
                    emailExists = true; // Email was found

                    // Check if the password matches
                    if (userData[2].equals(password)) {
                        // Check if the user is already logged in
                        int logStatus = Integer.parseInt(userData[7]); // Assuming the log field is at index 7
                        if (logStatus == 1) {
                            showErrorAlert("This account is already logged in on another device.");
                            return null;
                        }

                        // Return the user object if all checks pass
                        return createUserFromData(userData);
                    } else {
                        showErrorAlert("Invalid password. Please try again.");
                        return null; // Exit early if the password is incorrect
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        if (!emailExists) {
            showErrorAlert("Email not found. Please check your email or sign up.");
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
            id = user.getEmail();
            System.out.println("Welcome, " + user.getName());
            updateLogStatus(id); // Set the log status to 1 (logged in)
            navigateToHomeScreen(); // Navigate to the home screen
        }
    }

    private void updateLogStatus(String userId) {
        String usersFilePath = "src/main/resources/users.txt";
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCSVLine(line);
                if (parts.size() >= 8 && parts.get(1).equals(userId)) {
                    // Create a User object to modify the `log` field
                    User user = new User(
                            parts.get(0), // Name
                            parts.get(1), // Email
                            parts.get(2), // Password
                            parts.get(3).replace("\"", "").split(";"), // Addresses
                            parts.get(4).replace("\"", ""), // Current Address
                            Arrays.stream(parts.get(5).replace("\"", "").split(";"))
                                    .mapToInt(Integer::parseInt)
                                    .toArray(), // Order IDs
                            Integer.parseInt(parts.get(6)), // Current Order ID
                            1 // Set the `log` field to the specified value
                    );

                    updatedLines.add(formatUserLine(user)); // Use helper to format
                } else {
                    updatedLines.add(line); // Keep other users unchanged
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user data: " + e.getMessage());
        }

        // Write the updated lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating user data: " + e.getMessage());
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
            HomeScreenController controller = loader.getController();
            controller.getID(id);
            controller.initialize();
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

    private String formatUserLine(User user) {
        StringBuilder userLine = new StringBuilder();

        userLine.append(user.getName()).append(","); // Name
        userLine.append(user.getEmail()).append(","); // Email
        userLine.append(user.getPassword()).append(","); // Password

        // Format addresses with quotes
        userLine.append("\"").append(String.join(";", user.getAddresses())).append("\"").append(",");

        // Format current address with quotes
        userLine.append("\"").append(user.getCurrentAddress()).append("\"").append(",");

        // Format order IDs without adding extra quotes
        userLine.append("\"").append(String.join(";", Arrays.stream(user.getOrderIDs())
                .mapToObj(String::valueOf)
                .toArray(String[]::new))).append("\"").append(",");

        // Append current order ID and log field
        userLine.append(user.getCurrentOrderID()).append(",");
        userLine.append(user.getLog());

        return userLine.toString();
    }

    private void terminateCurrentProcess() {
        try {
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            String currentPID = processName.split("@")[0];

            String command = System.getProperty("os.name").toLowerCase().contains("win")
                    ? "taskkill /PID " + currentPID + " /F" // For Windows
                    : "kill -9 " + currentPID;              // For Linux/macOS

            Runtime.getRuntime().exec(command);
            System.out.println("Terminated process with PID: " + currentPID);
        } catch (IOException e) {
            System.err.println("Failed to terminate current process: " + e.getMessage());
        }
    }

    private void resetCurrentOrderID() {
        String usersFilePath = "src/main/resources/users.txt";
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCSVLine(line);

                if (parts.size() >= 8 && parts.get(1).equals(id)) { // Check if it's the logged-in user
                    System.out.println("Updating currentOrderID for user: " + parts.get(1));

                    // Update the user data
                    parts.set(6, "0"); // Set currentOrderID to 0
                    parts.set(7, "0"); // Set log field to 0
                    updatedLines.add(formatCSVLine(parts)); // Format the updated line
                } else {
                    updatedLines.add(line); // Keep other users unchanged
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user data: " + e.getMessage());
        }

        // Write the updated lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing user data: " + e.getMessage());
        }
    }

    private String formatCSVLine(List<String> parts) {
        StringBuilder formattedLine = new StringBuilder();

        for (int i = 0; i < parts.size(); i++) {
            String field = parts.get(i);

            // Only quote fields if necessary
            if (field.contains(",") || field.contains(";") || field.contains("\"")) {
                field = "\"" + field.replace("\"", "\"\"") + "\""; // Escape inner quotes
            }

            formattedLine.append(field);
            if (i < parts.size() - 1) {
                formattedLine.append(","); // Add comma between fields
            }
        }

        return formattedLine.toString();
    }


    /**
     * Parses a CSV line into fields, handling quoted fields with commas.
     *
     * @param line The input CSV line.
     * @return A list of parsed fields.
     */
    private List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"' && (i == 0 || line.charAt(i - 1) != '\\')) {
                // Toggle the inQuotes state when encountering an unescaped quote
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                // If we encounter a comma outside quotes, finalize the current field
                result.add(currentField.toString().trim());
                currentField.setLength(0); // Clear the buffer for the next field
            } else {
                // Add the character to the current field
                currentField.append(c);
            }
        }

        // Add the last field
        result.add(currentField.toString().trim());

        // Remove surrounding quotes from fields where applicable
        for (int i = 0; i < result.size(); i++) {
            String field = result.get(i);
            if (field.startsWith("\"") && field.endsWith("\"")) {
                result.set(i, field.substring(1, field.length() - 1).replace("\\\"", "\""));
            }
        }

        return result;
    }

    private void addWindowCloseListener() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            System.out.println("Window is closing. Performing cleanup...");
            resetCurrentOrderID(); // Reset the user's currentOrderID and log fields
            terminateCurrentProcess(); // Terminate only this instance
        });
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
