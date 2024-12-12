package se_prototype.se_prototype;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import se_prototype.se_prototype.Model.Product;
import se_prototype.se_prototype.Model.User;
import se_prototype.se_prototype.Model.UserCart;

public class CartController {

    @FXML
    private CheckBox buyAfterTimeout;
    @FXML
    private ScrollPane group_cart_choose;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label serviceFeeLabel;
    @FXML
    private Label yourServiceFeeLabel_group;
    @FXML
    private Label yourDeliveryFeeLabel_group;
    @FXML
    private Label yourSubtotalLabel_group;
    @FXML
    private Label yourTotalCostLabel_group;
    @FXML
    private Label timerLabel;
    @FXML
    private StackPane screen;
    @FXML
    private ScrollPane group_cart;
    @FXML
    private VBox groupUsersContainer;
    @FXML
    private Label error_payment_method;
    @FXML
    private Button joinGroupOrderButton;
    @FXML
    private Button solo_order_button;
    @FXML
    private Hyperlink change_location;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Button addMoreItems_empty;
    @FXML
    private ImageView empty_cart;
    @FXML
    private Pane loadingOverlay;
    @FXML
    private VBox empty_cart_label;
    @FXML
    private HBox HBox_discount;
    @FXML
    private Label totalSavingsLabel;
    @FXML
    private Label deliveryChargeLabel;
    @FXML
    private VBox productContainer;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label totalItemsLabel;
    @FXML
    private Button homeButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button cartButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button addMoreItems;
    @FXML
    private ScrollPane productScrollPane;
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    @FXML
    private StackPane paymentIconContainer;
    @FXML
    private StackPane toggleSwitch;
    @FXML
    private Circle toggleThumb;
    @FXML
    private Label soloLabel;
    @FXML
    private Label groupLabel;
    @FXML
    private Label subtotalLabel_group;
    @FXML
    private Label serviceFeeLabel_group;
    @FXML
    private Label estimatedDeliveryTimeLabel_group;
    @FXML
    private Label totalItemsLabel_group;
    @FXML
    private Label totalPriceLabel_group;
    @FXML
    private Label totalSavingsLabel_group;
    @FXML
    private Label deliveryChargeLabel_group;
    @FXML
    private HBox HBox_discount_group;
    @FXML
    private Button startGroupOrderButton;
    @FXML
    private ImageView timerIcon;
    @FXML
    private ImageView peopleIcon;
    @FXML
    private ImageView notificationIcon;
    @FXML
    private ImageView cartIcon;
    @FXML
    private ImageView itemsIcon;

    private boolean isSoloCart = true;
    private long timeRemaining; // 1 hour in seconds (3600)
    private final String CART_FILE = "src/main/resources/cart.txt";
    private final List<UserCart> userCarts = new ArrayList<>();
    private final String saveFile = "src/main/resources/group_cart_users.txt";
    private final List<Product> allProducts = new ArrayList<>();
    private boolean isGroupCartTimerRunning = false;
    private static final String TIMER_STATE_FILE = "src/main/resources/timer_state.txt";
    private ScheduledExecutorService timerService;
    private static boolean isInitialized = false;
    private static int totalUsers;
    private String id;
    private static final String TIMER_FILE = "src/main/resources/timer_state.txt";

    @FXML
    public void initialize() {
        if (!isInitialized) {
            //clearAppFileOnInitialization(); // Clear files only once at the start of the app
            if (Objects.equals(id, "test@example.com")) {
                //timer for 2 minutes
                timeRemaining = 60; // 1 minutes in seconds
            } else {
                timeRemaining = 3600; // 1 hour in seconds
            }
            saveTimerState(timeRemaining); // Save the timer state
            isInitialized = true; // Set the flag to true to prevent future runs
        }

        if (id == null) {
            System.out.println("User ID is not set yet. Skipping cart and user-specific initialization.");
            // Set default values for UI elements
            empty_cart_label.setVisible(true);
            empty_cart_label.setManaged(true);
            productScrollPane.setVisible(false);
            productScrollPane.setManaged(false);
            toggleSwitch.setVisible(false);
            toggleSwitch.setManaged(false);
            group_cart.setVisible(false);
            group_cart.setManaged(false);

            return; // Exit early to avoid errors
        }

        // Load the timer state from the file
        setupImages();
        initializeLoadingOverlay();
        payment_icon_and_method();
        loadAllProducts();
        //loadLoggedInUserCart(id);
        watchFiles();
        loadUserData();
        //startRandomUserUpdate();
        loadCartFromFile();
        updateCartSummary();

        // Initialize UI setup
        group_cart.setVisible(false);
        group_cart.setManaged(false);
        soloLabel.setTranslateX(-22.5);
        toggleThumb.setStyle("-fx-fill: #959595;");
        animateToggleThumb(-30);
        group_cart.setVvalue(0);

        // Update UI to reflect the latest data
        updateGroupCartUI();
        loadGroupCart();
        updateDeliverySummary();

        paymentMethodComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null && (newValue.equals("Cash") || newValue.equals("Card"))) {
                solo_order_button.setDisable(false);
                error_payment_method.setVisible(false);
                error_payment_method.setManaged(false);
            } else {
                solo_order_button.setDisable(true);
                error_payment_method.setVisible(true);
                error_payment_method.setManaged(true);
            }
        });

        // Set up button actions
        homeButton.setOnAction(event -> switchToPage("home_screen.fxml", "Home"));
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings"));
        addMoreItems.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        addMoreItems_empty.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        change_location.setOnAction(event -> switchToPage("location.fxml", "Location"));

        // Set up the toggle switch
        timerIcon.setImage(new Image(getClass().getResourceAsStream("/icons/timer.png")));
        peopleIcon.setImage(new Image(getClass().getResourceAsStream("/icons/people.png")));
        notificationIcon.setImage(new Image(getClass().getResourceAsStream("/icons/notification.png")));
        cartIcon.setImage(new Image(getClass().getResourceAsStream("/icons/cart.png")));
        itemsIcon.setImage(new Image(getClass().getResourceAsStream("/icons/items.png")));
        startGroupOrderButton.setOnAction(event -> handleCreateGroupCart());
        joinGroupOrderButton.setOnAction(event -> handleJoinGroupCart());
    }

    private void updateTimerLabel(long timeRemaining) {
        long minutes = timeRemaining / 60;
        long seconds = timeRemaining % 60;
        String timeText = String.format("Time Remaining: %02d:%02d", minutes, seconds);
        timerLabel.setText(timeText); // Update the timer label
    }

    private void watchFiles() {
        File cartFile = new File(CART_FILE);
        File usersFile = new File("src/main/resources/users.txt");
        AtomicLong cartFileLastModified = new AtomicLong(cartFile.lastModified());
        AtomicLong usersFileLastModified = new AtomicLong(usersFile.lastModified());

        Thread watcherThread = new Thread(() -> {
            while (true) {
                boolean cartFileChanged = cartFile.lastModified() > cartFileLastModified.get();
                boolean usersFileChanged = usersFile.lastModified() > usersFileLastModified.get();

                if (cartFileChanged) {
                    cartFileLastModified.set(cartFile.lastModified());
                    Platform.runLater(() -> {
                        System.out.println("Detected changes in cart.txt. Reloading group cart...");
                        loadGroupCart();
                        updateGroupCartUI();
                    });
                }

                if (usersFileChanged) {
                    usersFileLastModified.set(usersFile.lastModified());
                    Platform.runLater(() -> {
                        System.out.println("Detected changes in users.txt. Reloading user data...");
                        loadUserData();
                        updateGroupCartUI();
                    });
                }

                try {
                    Thread.sleep(1000); // Poll every second
                } catch (InterruptedException e) {
                    System.err.println("File watcher interrupted: " + e.getMessage());
                    break;
                }
            }
        });

        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    private void showGroupCart() {
        // Show the group_cart screen
        Platform.runLater(() -> {
            group_cart_choose.setVisible(false);
            group_cart_choose.setManaged(false);
            group_cart.setVisible(true);
            group_cart.setManaged(true);
        });
    }

    private void saveTimerState(long timeRemaining) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TIMER_STATE_FILE))) {
            writer.write(String.valueOf(timeRemaining));
        } catch (IOException e) {
            System.err.println("Error saving timer state: " + e.getMessage());
        }
    }

    private long loadTimerStateFromFile() {
        File file = new File(TIMER_STATE_FILE);
        if (!file.exists()) return 3600; // Default to 1 hour if the file doesn't exist

        // Load the timer state from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            return line != null ? Long.parseLong(line) : 3600;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading timer state, defaulting to 1 hour: " + e.getMessage());
            return 3600;
        }
    }

    private void createNewGroupCart() {
        clearFile(saveFile); // Clear any existing group cart data
        overrideYouCart();   // Add "You" as the first user in the group cart
        if (Objects.equals(id, "test@example.com")) {
            //timer for 2 minutes
            timeRemaining = 60; // 2 minutes in seconds
        } else {
            timeRemaining = 3600; // 1 hour in seconds
        }
        saveTimerState(timeRemaining); // Save the timer state
        startGroupCartTimer(); // Start the timer
        System.out.println("New group cart created. Timer reset to 1 hour.");

        String usersFilePath = "src/main/resources/users.txt";
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCSVLine(line); // Parse the CSV line

                if (parts.size() >= 8 && parts.get(1).equals(id)) { // Check if it's the logged-in user
                    parts.set(6, "1"); // Set `currentOrderID` to 1
                    line = formatUserLine(parts); // Format the line with proper quotes
                }
                updatedLines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading users.txt: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error Creating Group Cart", "An error occurred while updating user data.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to users.txt: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error Creating Group Cart", "Failed to save user data.");
        }

        System.out.println("New group cart created. User's currentOrderID updated to 1.");
    }

    /**
     * Formats the user's data into a properly quoted CSV line.
     * @param parts The parsed CSV parts of the user line.
     * @return A properly formatted CSV line.
     */
    private String formatUserLine(List<String> parts) {
        StringBuilder formattedLine = new StringBuilder();

        formattedLine.append(parts.get(0)).append(","); // Name
        formattedLine.append(parts.get(1)).append(","); // Email
        formattedLine.append(parts.get(2)).append(","); // Password
        formattedLine.append("\"").append(parts.get(3)).append("\","); // Addresses
        formattedLine.append("\"").append(parts.get(4)).append("\","); // Current Address
        formattedLine.append("\"").append(parts.get(5)).append("\","); // Order IDs
        formattedLine.append(parts.get(6)).append(","); // Current Order ID
        formattedLine.append(parts.get(7)); // Error Case

        return formattedLine.toString();
    }

    private void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void handleCreateGroupCart() {
        if (isGroupCartActive()) {
            // Show error dialog if a group cart is already active
            showErrorDialog("Group Cart Already Active", "You cannot create a new group cart while one is active. Please join the existing group cart.");
            return;
        }

        // Proceed with creating a new group cart
        createNewGroupCart();
        loadGroupCart();

        // Ensure the group cart UI is shown and properly initialized
        Platform.runLater(() -> {
            group_cart.setVisible(true);
            group_cart.setManaged(true);

            group_cart_choose.setVisible(false);
            group_cart_choose.setManaged(false);

            productScrollPane.setVisible(false);
            productScrollPane.setManaged(false);

            group_cart.setVvalue(0); // Reset scroll position for group cart view
        });
    }

    private boolean isGroupCartActive() {
        String usersFilePath = "src/main/resources/users.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCSVLine(line);
                if (parts.size() >= 8) {
                    int currentOrder = Integer.parseInt(parts.get(6).trim());
                    if (currentOrder > 0) {
                        return true; // Active group cart exists
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return false; // No active group cart
    }

    private void handleJoinGroupCart() {
        if (!isGroupCartActive()) {
            // Show error dialog if no active group cart exists
            showErrorDialog("No Active Group Cart", "There is no active group cart to join. Please create one.");
            return;
        }

        // Update the currentOrder value for the logged-in user in users.txt
        String usersFilePath = "src/main/resources/users.txt";
        try {
            List<String> lines = new ArrayList<>();
            int activeOrderId = getActiveOrderId(); // Method to get the active order ID from users.txt

            // Read users.txt and update the logged-in user's currentOrder
            try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    List<String> parts = parseCSVLine(line);
                    if (parts.size() >= 8 && parts.get(1).equals(id)) {
                        // Update currentOrder for the logged-in user
                        parts.set(6, String.valueOf(activeOrderId));
                        line = formatCSVLine(parts); // Format line to preserve quotes
                    }
                    lines.add(line);
                }
            }

            // Write updated data back to users.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            // Load the group cart UI
            loadGroupCart();
            showGroupCart();
            // timer
            timeRemaining = loadTimerStateFromFile();
            startGroupCartTimer();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error Joining Group Cart", "An error occurred while trying to join the group cart.");
        }
    }

    /**
     * Formats the parts of a CSV line while preserving quoted fields.
     *
     * @param parts The parsed fields of the CSV line.
     * @return A properly formatted CSV line with preserved quotes.
     */
    private String formatCSVLine(List<String> parts) {
        StringBuilder formattedLine = new StringBuilder();

        for (int i = 0; i < parts.size(); i++) {
            String field = parts.get(i);
            // Add quotes around fields containing commas, quotes, or newlines
            if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
                field = "\"" + field.replace("\"", "\"\"") + "\""; // Escape inner quotes
            }
            formattedLine.append(field);
            if (i < parts.size() - 1) {
                formattedLine.append(","); // Add comma separator
            }
        }

        return formattedLine.toString();
    }

    private void showConfirmationDialog(String overrideGroupCart, String s) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(overrideGroupCart);
            alert.setHeaderText(null);
            alert.setContentText(s);

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeYes) {
                createNewGroupCart();
                showGroupCart();
            }
        });
    }

    private int getActiveOrderId() {
        String usersFilePath = "src/main/resources/users.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCSVLine(line);
                if (parts.size() >= 8) {
                    int currentOrder = Integer.parseInt(parts.get(6).trim());
                    if (currentOrder > 0) {
                        return currentOrder; // Return the active order ID
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0; // Default value if no active order is found
    }

    private boolean checkForExistingGroupCart() {
        File file = new File(saveFile); // The file that stores group cart data
        return file.exists() && file.length() > 0; // Check if the file exists and is not empty
    }

    private void updateDeliverySummary() {
        int totalItems = 0;
        double subtotal = 0;
        double deliveryCharge = 0; // Default delivery charge
        double serviceFee = 0; // Example service fee
        double totalDiscount = 0;
        double yourSubtotal = 0;
        double yourTotalCost = 0;
        int noOfUsersInCart = 0;

        // Filter users in group cart (currentOrderID == 1)
        List<UserCart> groupCartUsers = userCarts.stream()
                .filter(userCart -> hasActiveOrder(userCart.getEmail())) // Only users in active group cart
                .toList();

        // Calculate totals from all user carts
        for (UserCart userCart : groupCartUsers) {
            if (userCart.getCartItems() != null && !userCart.getCartItems().isEmpty()) {
                for (Product item : userCart.getCartItems()) {
                    int quantity = item.getQuantity();
                    double price = item.getPrice();
                    double discountedPrice = price - (price * item.getDiscount() / 100);

                    totalItems += quantity;
                    subtotal += discountedPrice * quantity;
                }

                // If this is the logged-in user, calculate their subtotal
                if (userCart.getEmail().equals(id)) {
                    yourSubtotal = userCart.getCartItems().stream()
                            .mapToDouble(item -> (item.getPrice() - (item.getPrice() * item.getDiscount() / 100)) * item.getQuantity())
                            .sum();
                    totalDiscount += userCart.getCartItems().stream()
                            .mapToDouble(item -> (item.getPrice() * item.getDiscount() / 100) * item.getQuantity())
                            .sum();
                }

                noOfUsersInCart++;
            }
        }

        // Calculate delivery charge and service fee once
        if (subtotal > 25 && subtotal <= 50) {
            deliveryCharge = 10.0;
        } else if (subtotal > 50) {
            deliveryCharge = 20.0;
        } else {
            deliveryCharge = 5.0;
        }

        serviceFee = totalItems > 25 ? 5.0 : 2.5;

        // Calculate per-user charges
        double deliveryPerUser = noOfUsersInCart > 0 ? (deliveryCharge / noOfUsersInCart) : 0.0;
        double serviceFeePerUser = noOfUsersInCart > 0 ? (serviceFee / noOfUsersInCart) : 0.0;

        // Calculate total cost for the logged-in user
        yourTotalCost = yourSubtotal + deliveryPerUser + serviceFeePerUser;

        // Format and set label values
        double finalSubtotal = subtotal;
        int finalTotalItems = totalItems;
        double finalTotalDiscount = totalDiscount;
        double finalYourSubtotal = yourSubtotal;
        double finalYourTotalCost = yourTotalCost;
        double finalDeliveryCharge = deliveryCharge;
        double finalServiceFee = serviceFee;
        Platform.runLater(() -> {
            subtotalLabel_group.setText(String.format("$%.2f", finalSubtotal));
            deliveryChargeLabel_group.setText(String.format("$%.2f", finalDeliveryCharge));
            serviceFeeLabel_group.setText(String.format("$%.2f", finalServiceFee));
            totalPriceLabel_group.setText(String.format("$%.2f", finalSubtotal + finalDeliveryCharge + finalServiceFee));
            totalItemsLabel_group.setText(String.valueOf(finalTotalItems));
            totalSavingsLabel_group.setText(String.format("-$%.2f", finalTotalDiscount));

            // Your Total Cost Details
            yourSubtotalLabel_group.setText(String.format("$%.2f", finalYourSubtotal));
            yourDeliveryFeeLabel_group.setText(String.format("$%.2f", deliveryPerUser));
            yourServiceFeeLabel_group.setText(String.format("$%.2f", serviceFeePerUser));
            yourTotalCostLabel_group.setText(String.format("$%.2f", finalYourTotalCost));

            // Show or hide the discount section
            if (finalTotalDiscount > 0) {
                HBox_discount_group.setVisible(true);
                HBox_discount_group.setManaged(true);
            } else {
                HBox_discount_group.setVisible(false);
                HBox_discount_group.setManaged(false);
            }

            // Set an example delivery time (can be dynamic based on other logic)
            estimatedDeliveryTimeLabel_group.setText("30-45 min");
        });
    }

    private void loadAllProducts() {
        // File containing all products
        String allItemsFile = "src/main/resources/search.txt";
        File file = new File(allItemsFile);
        if (!file.exists()) {
            System.err.println("Product file not found: " + allItemsFile);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String name = parts[0];
                    String description = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    String imageUrl = parts[3];
                    double discount = Double.parseDouble(parts[4]);
                    int quantity = Integer.parseInt(parts[5]);
                    allProducts.add(new Product(name, description, price, imageUrl, discount, quantity));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startRandomUserUpdate() {
        Random random = new Random();
        List<String> addedUsers = new ArrayList<>();

        new Thread(() -> {
            while (userCarts.size() < totalUsers) {
                try {
                    Thread.sleep(15000); // Wait for 15 seconds before adding a new user
                    Platform.runLater(() -> {
                        if (userCarts.size() < totalUsers) {
                            String userName = "User " + (userCarts.size() + 1);
                            if (!addedUsers.contains(userName)) {
                                addRandomUser();
                                updateGroupCartUI();
                                addedUsers.add(userName);
                            }
                        } else {
                            System.out.println("Maximum number of users reached. No new users will be added.");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addRandomUser() {
        if (allProducts.isEmpty() || userCarts.size() >= 10) return;

        Random random = new Random();
        Collections.shuffle(allProducts); // Shuffle to ensure randomness

        // Generate random user data
        String userName = "User " + (userCarts.size() + 1);
        List<Product> cartItems = new ArrayList<>();

        int numItems = random.nextInt(3) + 1; // Each user has 1-3 items
        for (int i = 0; i < numItems; i++) {
            Product randomProduct = allProducts.get(random.nextInt(allProducts.size()));
            cartItems.add(new Product(
                    randomProduct.getName(),
                    randomProduct.getDescription(),
                    randomProduct.getPrice(),
                    randomProduct.getImageUrl(),
                    randomProduct.getDiscount(),
                    random.nextInt(3) + 1 // Random quantity (1-3)
            ));
        }

        userCarts.add(new UserCart(userName, cartItems));
    }

    private void updateGroupCartUI() {
        groupUsersContainer.getChildren().clear();

        // Filter users based on criteria: non-empty cart and currentOrderID == 1
        List<UserCart> validUserCarts = new ArrayList<>(userCarts.stream()
                .filter(userCart -> {
                    // Check if user cart is non-empty
                    boolean hasItems = userCart.getCartItems() != null && !userCart.getCartItems().isEmpty();

                    // Check if user exists in users.txt with currentOrderID == 1
                    boolean hasActiveOrder = hasActiveOrder(userCart.getEmail());

                    return hasItems && hasActiveOrder;
                })
                .toList());

        // Reorder the list so the logged-in user appears first
        validUserCarts.sort((cart1, cart2) -> {
            if (cart1.getEmail().equals(id)) return -1; // Logged-in user comes first
            if (cart2.getEmail().equals(id)) return 1;
            return 0;
        });

        for (UserCart userCart : validUserCarts) {
            if (userCart == null || userCart.getEmail() == null || userCart.getCartItems().isEmpty()) {
                continue;
            }
            // Main container for each user's cart
            VBox userCartBox = new VBox();
            userCartBox.setStyle("-fx-background-color: #FFFFFF; " +
                    "-fx-border-radius: 12; " +
                    "-fx-padding: 10; " +
                    "-fx-background-radius: 12;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0, 0, 2);" +
                    "-fx-border-color: transparent;");
            userCartBox.setSpacing(8);

            // User header section (image + name)
            HBox userHeader = new HBox();
            userHeader.setSpacing(8);
            userHeader.setAlignment(Pos.CENTER_LEFT);

            // User profile image
            ImageView userImage = new ImageView(new Image("current_user_picture.png"));
            userImage.setFitWidth(40); // Reduced size for compact design
            userImage.setFitHeight(40);
            userImage.setPreserveRatio(true);
            userImage.setStyle("-fx-border-radius: 20;"); // Circular profile image

            // User name label
            Label userNameLabel = new Label(userCart.getEmail());
            userNameLabel.setStyle("-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-text-fill: #5EC401;");

            // Calculate total price for the user's cart
            double userTotalPrice = userCart.getCartItems().stream()
                    .mapToDouble(item -> (item.getPrice() - (item.getPrice() * item.getDiscount() / 100)) * item.getQuantity())
                    .sum();

            // Total cart price label
            Label totalCartPriceLabel = new Label(String.format("Total: $%.2f", userTotalPrice));
            totalCartPriceLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #37474F;");

            // Spacer to align total price to the right
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            userHeader.getChildren().addAll(userImage, userNameLabel, spacer, totalCartPriceLabel);

            // Container for cart items
            VBox userCartItems = new VBox();
            userCartItems.setSpacing(8);

            for (Product item : userCart.getCartItems()) {
                // Item container
                HBox itemBox = new HBox();
                itemBox.setSpacing(10);
                itemBox.setAlignment(Pos.CENTER_LEFT);
                itemBox.setPrefHeight(45);
                itemBox.setStyle("-fx-background-color: #F8F8F8; " +
                        "-fx-border-radius: 8; " +
                        "-fx-padding: 8;" +
                        "-fx-background-radius: 8;");

                // Item image
                ImageView itemImage = new ImageView(new Image(item.getImageUrl()));
                itemImage.setFitWidth(35); // Max width for consistency
                itemImage.setFitHeight(35); // Ensure consistent height
                itemImage.setPreserveRatio(true);
                itemImage.setStyle("-fx-border-radius: 8;");

                // Container for text to avoid overlapping
                VBox textContainer = new VBox();
                textContainer.setSpacing(5);
                textContainer.setAlignment(Pos.CENTER_LEFT);
                textContainer.setMaxWidth(180);

                // Item name
                Label itemLabel = new Label(item.getName());
                itemLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #37474F;");
                itemLabel.setWrapText(true);

                // Add name and discount to text container
                HBox nameAndDiscountBox = new HBox(5); // 5 is the spacing between name and discount
                nameAndDiscountBox.setAlignment(Pos.CENTER_LEFT);
                nameAndDiscountBox.getChildren().add(itemLabel);

                if (item.hasDiscount()) {
                    Label discountLabel = new Label(item.getDiscountBadge());
                    discountLabel.setStyle("-fx-font-size: 10px; " +
                            "-fx-text-fill: #FF6B6B; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-color: #FFECEC; " +
                            "-fx-padding: 2 6; " +
                            "-fx-border-radius: 4;");
                    nameAndDiscountBox.getChildren().add(discountLabel);
                }
                textContainer.getChildren().add(nameAndDiscountBox);

                Label quantityLabel = new Label("Quantity: " + item.getQuantity());
                quantityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");
                textContainer.getChildren().add(quantityLabel);

                // Right-aligned price details
                VBox priceContainer = new VBox();
                priceContainer.setAlignment(Pos.CENTER_RIGHT);
                priceContainer.setSpacing(1);

                // Discount badge (if applicable)
                if (item.hasDiscount()) {
                    Label oldPriceLabel = new Label("$" + String.format("%.2f", item.getPrice()));
                    oldPriceLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #757575; -fx-strikethrough: true;");
                    priceContainer.getChildren().add(oldPriceLabel);
                }

                // Discounted Price
                Label discountedPriceLabel = new Label(item.getDiscountPrice((int) item.getDiscount()));
                discountedPriceLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #FF6B6B;");
                priceContainer.getChildren().add(discountedPriceLabel);

                // Total price for the quantity
                double discountedPricePerItem = item.getPrice() - (item.getPrice() * item.getDiscount() / 100);
                double totalPriceForItem = discountedPricePerItem * item.getQuantity();

                Label totalPriceLabel = new Label("Total: $" + String.format("%.2f", totalPriceForItem));
                totalPriceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #37474F;");
                priceContainer.getChildren().add(totalPriceLabel);

                // Spacer for alignment
                Region spacer_2 = new Region();
                HBox.setHgrow(spacer_2, Priority.ALWAYS);

                itemBox.getChildren().addAll(itemImage, textContainer, spacer_2, priceContainer);
                userCartItems.getChildren().add(itemBox);
            }

            userCartBox.getChildren().addAll(userHeader, userCartItems);
            groupUsersContainer.getChildren().add(userCartBox);
        }

        updateDeliverySummary();
    }

    private void loadUserData() {
        userCarts.clear(); // Reset the list to avoid duplicates
        File file = new File(CART_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    userCarts.add(UserCart.fromString(line));
                } catch (Exception e) {
                    System.err.println("Failed to parse user cart: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasActiveOrder(String email) {
        String usersFilePath = "src/main/resources/users.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCSVLine(line);
                if (parts.size() >= 8 && parts.get(1).equals(email)) {
                    // Check if currentOrderID is 1
                    int currentOrderID = Integer.parseInt(parts.get(6).trim());
                    return currentOrderID == 1;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error checking user order: " + e.getMessage());
        }
        return false; // Default to false if no match is found
    }

    @FXML
    private void handleToggleSwitch() {
        isSoloCart = !isSoloCart;

        // Show the loading overlay and apply blur effect
        applyBlurredBackgroundEffect(true);
        loadingOverlay.setVisible(true);

        // Capture the state for toggle transition
        Timeline delay = new Timeline(
                new KeyFrame(
                        Duration.millis(175), // Match the loading overlay duration
                        ev -> {
                            if (isSoloCart) {
                                // Solo Cart View
                                loadCartFromFile();
                                updateCartSummary();
                                soloLabel.setVisible(true);
                                toggleThumb.setStyle("-fx-fill: #959595;");
                                groupLabel.setVisible(false);
                                animateToggleThumb(-30);
                                group_cart.setVisible(false);
                                group_cart.setManaged(false);
                                group_cart_choose.setVisible(false);
                                group_cart_choose.setManaged(false);
                                productScrollPane.setVisible(true);
                                productScrollPane.setManaged(true);
                                stopGroupCartTimer();
                            } else {
                                // Group Cart View
                                updateLoggedInUserCartInGroup();
                                updateDeliverySummary();
                                soloLabel.setVisible(false);
                                groupLabel.setVisible(true);
                                toggleThumb.setStyle("-fx-fill: #5EC401;");
                                animateToggleThumb(30);
                                // Check if there is an active group order
                                if (isUserInActiveGroupOrder()) {
                                    group_cart.setVisible(true);
                                    group_cart.setManaged(true);
                                    group_cart_choose.setVisible(false);
                                    group_cart_choose.setManaged(false);
                                    productScrollPane.setVisible(false);
                                    productScrollPane.setManaged(false);
                                    group_cart.setVvalue(0);
                                    screen.requestFocus();
                                    // Resume the existing timer
                                    timeRemaining = loadTimerStateFromFile();
                                    if (!isGroupCartTimerRunning) {
                                        startGroupCartTimer(); // Start or resume the timer
                                    }
                                } else {
                                    // Show the choice to join or create a group order
                                    group_cart_choose.setVisible(true);
                                    group_cart_choose.setManaged(true);
                                    group_cart.setVisible(false);
                                    group_cart.setManaged(false);
                                    productScrollPane.setVisible(false);
                                    productScrollPane.setManaged(false);
                                }
                            }

                            // Fade out the loading overlay
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), loadingOverlay);
                            fadeOut.setFromValue(1);
                            fadeOut.setToValue(0);
                            fadeOut.setOnFinished(event2 -> {
                                loadingOverlay.setVisible(false);
                                applyBlurredBackgroundEffect(false);
                            });
                            fadeOut.play();
                        }
                )
        );

        // Start the fade-in animation for the overlay
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), loadingOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(ev -> delay.play());
        fadeIn.play();
    }

    private boolean isUserInActiveGroupOrder() {
        // Example logic to check if the user has an active group order
        User currentUser = getCurrentUser(); // Replace with your method to get the current user
        return currentUser != null && currentUser.getCurrentOrderID() > 0;
    }

    private void startGroupCartTimer() {
        stopGroupCartTimer(); // Stop any existing timer
        if (!isGroupCartTimerRunning) {
            timeRemaining = loadTimerStateFromFile(); // Load saved time
        }

        isGroupCartTimerRunning = true;
        timerService = Executors.newSingleThreadScheduledExecutor();

        timerService.scheduleAtFixedRate(() -> {
            timeRemaining--;
            saveTimerState(timeRemaining); // Save updated timer value

            Platform.runLater(() -> updateTimerLabel(timeRemaining));

            if (timeRemaining <= 0) {
                Platform.runLater(() -> {
                    stopGroupCartTimer();

                    if (buyAfterTimeout.isSelected()) {
                        // Save order to `orders.txt`
                        saveOrderToHistory();
                        resetUserToSoloCart();
                        deleteUserFromCartFile(id);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Purchase Confirmation");
                        alert.setHeaderText(null);
                        alert.setContentText("Time's up! Your order has been saved to history.");
                        alert.showAndWait();

                        System.out.println("Order saved to history as 'Buy After Timeout' is enabled.");
                        switchToPage("cart.fxml", "Cart");
                    } else {
                        // Reset user to solo cart
                        resetUserToSoloCart();

                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Session Expired");
                        alert.setHeaderText(null);
                        alert.setContentText("Time's up! You have been moved to the solo cart.");
                        alert.showAndWait();

                        System.out.println("User moved to solo cart as 'Buy After Timeout' is disabled.");
                        switchToPage("cart.fxml", "Solo Cart");
                    }
                });
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void deleteUserFromCartFile(String id) {
        String cartFilePath = "src/main/resources/cart.txt";
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(cartFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCSVLine(line);
                if (parts.size() >= 2 && parts.get(0).equals(id)) {
                    // Skip the line for the user to be removed
                    continue;
                }
                updatedLines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading cart data: " + e.getMessage());
        }

        // Write the updated lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cartFilePath))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating cart data: " + e.getMessage());
        }
    }

    /**
     * Saves the current order to the `orders.txt` file.
     */
    private void saveOrderToHistory() {
        String ordersFilePath = "src/main/resources/orders.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ordersFilePath, true))) {
            // Retrieve user cart items
            List<String[]> cartItems = readCartFile(id);

            if (cartItems.isEmpty()) {
                System.out.println("No items in the cart to save.");
                return;
            }

            // Format order details in the same structure as cart.txt
            StringBuilder orderDetails = new StringBuilder();

            // Add user ID
            orderDetails.append(id).append(";");

            for (String[] productData : cartItems) {
                // Product format: Name, Description, Price, ImageUrl, Discount, Quantity
                String formattedItem = String.join(",", productData);
                orderDetails.append(formattedItem).append(";");
            }

            // Remove the trailing semicolon
            if (orderDetails.charAt(orderDetails.length() - 1) == ';') {
                orderDetails.setLength(orderDetails.length() - 1);
            }

            // Write the formatted order to the file
            writer.write(orderDetails.toString());
            writer.newLine();

            System.out.println("Order saved to history file: " + ordersFilePath);
        } catch (IOException e) {
            System.err.println("Failed to save order to history: " + e.getMessage());
        }
    }

    /**
     * Resets the user to the solo cart by updating `users.txt`.
     */
    private void resetUserToSoloCart() {
        String usersFilePath = "src/main/resources/users.txt";
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCSVLine(line);
                if (parts.size() >= 8 && parts.get(1).equals(id)) { // Check if it's the logged-in user
                    // Create a User object to modify the `log` and `currentOrderID`
                    User user = new User(
                            parts.get(0), // Name
                            parts.get(1), // Email
                            parts.get(2), // Password
                            parts.get(3).replace("\"", "").split(";"), // Addresses
                            parts.get(4).replace("\"", ""), // Current Address
                            Arrays.stream(parts.get(5).replace("\"", "").split(";"))
                                    .mapToInt(Integer::parseInt)
                                    .toArray(), // Order IDs
                            0, // Set `currentOrderID` to 0
                            1 // Set `log` field to 0 (logged out)
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

    private String formatUserLine(User user) {
        StringBuilder userLine = new StringBuilder();

        userLine.append(user.getName()).append(","); // Name
        userLine.append(user.getEmail()).append(","); // Email
        userLine.append(user.getPassword()).append(","); // Password

        // Format addresses with quotes
        userLine.append("\"").append(String.join(";", user.getAddresses())).append("\"").append(",");

        // Format current address with quotes
        userLine.append("\"").append(user.getCurrentAddress()).append("\"").append(",");

        // Format order IDs with quotes
        userLine.append("\"").append(Arrays.stream(user.getOrderIDs())
                .mapToObj(String::valueOf)
                .reduce((a, b) -> a + ";" + b)
                .orElse("")).append("\"").append(",");

        // Append current order ID and error case
        userLine.append(user.getCurrentOrderID()).append(",");
        userLine.append(user.getLog());

        return userLine.toString();
    }

    private void stopGroupCartTimer() {
        if (timerService != null && !timerService.isShutdown()) {
            timerService.shutdown();
            saveTimerState(timeRemaining); // Save the current timer value when stopping
            isGroupCartTimerRunning = false;
        }
    }

    private void notifyGroupCartExpiration() {
        showErrorDialog("Session Expired", "The group cart session has expired.");
        handleHostLeaving(); // End the session for all users
    }

    private void handleHostLeaving() {
        // Update `users.txt` to set all users' `in_group_cart` to 0
        String usersFilePath = "src/main/resources/users.txt";
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCSVLine(line);
                if (parts.size() >= 8) {
                    parts.set(6, "0"); // Set `in_group_cart` to 0
                    updatedLines.add(formatCSVLine(parts));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users.txt: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating users.txt: " + e.getMessage());
        }

        // Notify users and redirect them to the solo cart page
        Platform.runLater(() -> {
            showErrorDialog("Host Left", "The host has closed the group cart. Returning to solo cart.");
            switchToPage("cart.fxml", "Solo Cart");
        });
    }

    private void updateTimerLabel() {
        long minutes = timeRemaining / 60;
        long seconds = timeRemaining % 60;
        String timeText = String.format("Time Remaining: %02d:%02d", minutes, seconds);

        Platform.runLater(() -> timerLabel.setText(timeText)); // Ensure UI updates on the JavaFX thread
    }

    private void loadTimerState() {
        File file = new File(TIMER_STATE_FILE);
        if (!file.exists() || file.length() == 0) {
            // If the file doesn't exist or is empty, set a default timer value
            timeRemaining = 3600; // Default to 1 hour in seconds
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                timeRemaining = Long.parseLong(line);
            } else {
                // Handle empty or null content by setting a default value
                timeRemaining = 3600; // Default to 1 hour in seconds
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading timer state, resetting to default: " + e.getMessage());
            timeRemaining = 3600; // Default to 1 hour in seconds
        }
    }

    private void updateLoggedInUserCartInGroup() {
        List<UserCart> loadedCarts = loadCartsFromFile();

        // Update "You" cart
        UserCart youCart = loadedCarts.stream()
                .filter(cart -> cart.getEmail() != null && cart.getEmail().equals(id))
                .findFirst()
                .orElse(new UserCart(id, new ArrayList<>()));

        // Ensure we only remove and update carts with valid email
        userCarts.removeIf(cart -> cart.getEmail() != null && cart.getEmail().equals(id));
        userCarts.add(0, youCart);

        updateGroupCartUI(); // Refresh UI
    }

    private void overrideYouCart() {
        List<UserCart> loadedCarts = loadCartsFromFile();

        // Replace or add the logged-in user's cart
        UserCart youCart = loadedCarts.stream()
                .filter(cart -> cart.getEmail() != null && cart.getEmail().equals(id))
                .findFirst()
                .orElse(new UserCart(id, new ArrayList<>()));

        // Ensure no UserCart with a null email causes issues
        userCarts.removeIf(cart -> cart.getEmail() != null && cart.getEmail().equals(id));
        userCarts.add(0, youCart); // Add the logged-in user's cart to the top

        updateGroupCartUI(); // Refresh UI
    }

    private void animateToggleThumb(double toX) {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(toggleThumb);
        transition.setToX(toX);
        transition.setDuration(Duration.millis(200)); // Adjust animation duration
        transition.play();
    }

    private void loadGroupCart() {
        userCarts.clear(); // Clear the existing group cart
        loadUserData(); // Load users from `cart.txt`
        overrideYouCart(); // Ensure the logged-in user's cart is at the top
        updateGroupCartUI(); // Refresh the group cart UI
        updateDeliverySummary(); // Update the delivery summary
    }

    private void loadCartFromFile() {
        if (id == null) {
            return;
        }
        // Capture the current scroll position
        double scrollOffset = productScrollPane.getVvalue();

        // Clear and reload products
        productContainer.getChildren().clear();

        try {
            // Load user carts from the cart file
            List<UserCart> userCarts = loadCartsFromFile();

            // Find the cart for the specified user
            Optional<UserCart> userCartOptional = userCarts.stream()
                    .filter(cart -> cart.getEmail().equals(id))
                    .findFirst();

            if (userCartOptional.isPresent()) {
                UserCart userCart = userCartOptional.get();

                // Iterate through the user's cart items and create product nodes
                for (Product product : userCart.getCartItems()) {
                    productContainer.getChildren().add(createProductNode(
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            product.getImageUrl(),
                            product.getDiscount(),
                            product.getQuantity()
                    ));
                }
            } else {
                System.out.println("No cart found for user: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error loading cart from file: " + e.getMessage());
            e.printStackTrace();
        }

        // Restore scroll position after layout updates
        Platform.runLater(() -> {
            productScrollPane.layout();
            productScrollPane.setVvalue(scrollOffset);
        });
        updateCartSummary();
    }

    /**
     * Loads all carts from the cart file.
     *
     * @return A list of UserCart objects.
     */
    private List<UserCart> loadCartsFromFile() {
        List<UserCart> userCarts = new ArrayList<>();
        File file = new File(CART_FILE);

        if (!file.exists()) {
            return userCarts; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                UserCart cart = UserCart.fromString(line);
                if (cart != null && cart.getEmail() != null) { // Validate the cart and email
                    userCarts.add(cart);
                } else {
                    System.err.println("Skipped invalid UserCart: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userCarts;
    }

    private List<String[]> readCartFile(String userId) {
        List<String[]> products = new ArrayList<>();
        File cartFile = new File(CART_FILE);

        if (!cartFile.exists()) {
            return products; // File doesn't exist, return an empty list
        }

        boolean userExists = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(cartFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] segments = line.split(";", 2); // Split into email and items
                if (segments.length >= 1) {
                    String email = segments[0].trim();

                    // Check if the line belongs to the user
                    if (email.equals(userId)) {
                        userExists = true;

                        // If there are no items, return an empty list
                        if (segments.length == 1 || segments[1].trim().isEmpty()) {
                            return products;
                        }

                        // Parse the items if they exist
                        String[] productEntries = segments[1].split(";");
                        for (String productEntry : productEntries) {
                            String[] productDetails = productEntry.split(",");
                            if (productDetails.length >= 6) { // Ensure product format is valid
                                products.add(productDetails);
                            }
                        }
                        return products; // Return user-specific products
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading cart file: " + e.getMessage());
        }

        // If the user ID is not found in the file, treat it as an empty cart
        if (!userExists) {
            System.out.println("User ID not found in cart file. Treating as empty cart.");
        }

        return products; // Return empty list if user ID is not found or items are empty
    }

    private void payment_icon_and_method() {
        // Set default icon to "Choose Method"
        ImageView paymentIconImage = new ImageView(new Image(getClass().getResourceAsStream("/bottomPartSymbols/choose_method.png")));
        paymentIconImage.setFitWidth(24);
        paymentIconImage.setFitHeight(24);

        // Add the ImageView to the container
        paymentIconContainer.getChildren().clear();
        paymentIconContainer.getChildren().add(paymentIconImage);

        // Add a listener to the ComboBox to dynamically update the icon
        paymentMethodComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            String iconPath;
            switch (newValue) {
                case "Cash":
                    iconPath = "/bottomPartSymbols/cash.png";
                    break;

                case "Card":
                    iconPath = "/bottomPartSymbols/card.png";
                    break;

                default: // "Choose Method" or other
                    iconPath = "/bottomPartSymbols/choose_method.png";
                    break;
            }

            // Update the icon in the container
            paymentIconImage.setImage(new Image(getClass().getResourceAsStream(iconPath)));
        });
    }

    private void updateQuantity(String name, int delta, Label quantityLabel) {
        List<String[]> products = readCartFile(id);
        for (String[] productData : products) {
            if (productData[0].equals(name)) {
                int quantity = Integer.parseInt(productData[5]) + delta;
                if (quantity > 0) {
                    productData[5] = String.valueOf(quantity);
                    quantityLabel.setText(String.valueOf(quantity));
                }
            }
        }
        writeCartFile(id, products);
        updateCartSummary();
    }

    private void updateCartSummary() {
        // Read the user's cart from the file using their email
        List<String[]> products = readCartFile(id); // Pass the logged-in user's email (id) to filter the cart
        // If the cart is empty, show the empty cart label and hide product list
        if (products.isEmpty()) {
            empty_cart_label.setVisible(true);
            empty_cart_label.setManaged(true);
            productScrollPane.setVisible(false);
            productScrollPane.setManaged(false);
            toggleSwitch.setVisible(false);
            toggleSwitch.setManaged(false);
            return;
        } else {
            empty_cart_label.setVisible(false);
            empty_cart_label.setManaged(false);
            productScrollPane.setVisible(true);
            productScrollPane.setManaged(true);
            toggleSwitch.setVisible(true);
            toggleSwitch.setManaged(true);
        }

        int totalItems = 0;
        double subtotal = 0;
        double totalSavings = 0;
        double deliveryCharge = 0.0; // Default delivery charge
        double serviceFee = 0.0; // Default service fee


        // Log details of products in the cart
        for (String[] productData : products) {
            System.out.println("Product: " + Arrays.toString(productData));
        }

        // Calculate totals
        for (String[] productData : products) {
            int quantity = Integer.parseInt(productData[5]); // Quantity is at index 5
            double price = Double.parseDouble(productData[2]); // Price is at index 2
            double discount = Double.parseDouble(productData[4]); // Discount is at index 4

            totalItems += quantity;
            double discountedPrice = price - (price * discount / 100);
            subtotal += quantity * discountedPrice;
            totalSavings += quantity * (price - discountedPrice);
        }

        // Adjust delivery charge based on subtotal
        if (subtotal > 25 && subtotal <= 50) {
            deliveryCharge = 10.0;
        } else if (subtotal > 50) {
            deliveryCharge = 20.0;
        } else {
            deliveryCharge = 5.0;
        }

        // Adjust service fee based on total items
        serviceFee = totalItems > 25 ? 5.0 : 2.5;

        // Calculate total price
        double totalPrice = subtotal + deliveryCharge + serviceFee;

        // Update labels
        totalItemsLabel.setText("Total Items: " + totalItems);
        totalPriceLabel.setText("$" + String.format("%.2f", totalPrice));
        deliveryChargeLabel.setText("$" + String.format("%.2f", deliveryCharge));
        totalSavingsLabel.setText(totalSavings > 0
                ? "Total Savings: $" + String.format("%.2f", totalSavings)
                : "");

        // Update subtotal, delivery charge, and service fee labels
        subtotalLabel.setText(String.format("$%.2f", subtotal));
        deliveryChargeLabel.setText(String.format("$%.2f", deliveryCharge));
        serviceFeeLabel.setText(String.format("$%.2f", serviceFee));
        totalPriceLabel.setText(String.format("$%.2f", totalPrice));
        totalItemsLabel.setText("Total Items: " + totalItems);

        // Show or hide the discount label based on savings
        if (totalSavings > 0) {
            totalSavingsLabel.setText("Total Savings: $" + String.format("%.2f", totalSavings));
            HBox_discount.setVisible(true);
            HBox_discount.setManaged(true);
        } else {
            totalSavingsLabel.setText("");
            HBox_discount.setVisible(false);
            HBox_discount.setManaged(false);
        }
    }

    // Helper method to write the cart file lines and handle deletion
    private void writeCartFile(String userId, List<String[]> cartItems) {
        File cartFile = new File(CART_FILE);
        Map<String, String> cartData = new LinkedHashMap<>();

        // Read the current cart file to preserve other users' data
        if (cartFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(cartFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] segments = line.split(";", 2);
                    if (segments.length >= 2) {
                        String email = segments[0].trim();
                        String items = segments[1].trim();
                        cartData.put(email, items);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading cart file: " + e.getMessage());
            }
        }

        // Prepare the updated cart for the current user
        StringBuilder userCartItems = new StringBuilder();
        for (String[] cartItem : cartItems) {
            userCartItems.append(String.join(",", cartItem)).append(";");
        }

        // Update or add the user's cart
        cartData.put(userId, userCartItems.toString().trim());

        // Write the updated data back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cartFile))) {
            for (Map.Entry<String, String> entry : cartData.entrySet()) {
                String line = entry.getKey() + ";" + entry.getValue();
                if (!line.endsWith(";")) {
                    line += ";"; // Ensure semicolon at the end
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to cart file: " + e.getMessage());
        }

        // Track product files for deletion
        Set<String> existingFiles = new HashSet<>();
        for (String cartItemsStr : cartData.values()) {
            if (!cartItemsStr.isEmpty()) {
                String[] productEntries = cartItemsStr.split(";");
                for (String productEntry : productEntries) {
                    String[] productDetails = productEntry.split(",");
                    if (productDetails.length >= 4) { // Ensure valid product format
                        String productFilePath = productDetails[3]; // Assuming image/file path is at index 3
                        existingFiles.add(productFilePath);
                    }
                }
            }
        }

        // Delete unused product files
        deleteUnusedProductFiles(existingFiles);
    }

    // Method to delete unused product files
    private void deleteUnusedProductFiles(Set<String> existingFiles) {
        File productDir = new File("src/main/resources/products"); // Adjust the directory path as needed
        if (!productDir.exists() || !productDir.isDirectory()) {
            return;
        }

        File[] files = productDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!existingFiles.contains(file.getAbsolutePath())) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        System.out.println("Deleted unused product file: " + file.getName());
                    } else {
                        System.err.println("Failed to delete file: " + file.getName());
                    }
                }
            }
        }
    }

    private HBox createProductNode(String name, String description, double price, String imageUrl, double discount, int quantity) {
        HBox productBox = new HBox(10);
        productBox.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 4);");
        productBox.setAlignment(Pos.CENTER_LEFT);

        // Product Image with Badge
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(60, 60);

        ImageView productImage = new ImageView(new Image(imageUrl));
        productImage.setFitWidth(60);
        productImage.setFitHeight(60);
        productImage.setPreserveRatio(true);
        productImage.setStyle("-fx-border-radius: 8;");

        if (discount > 0) {
            Label badge = new Label((int) discount + "% OFF");
            badge.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4; -fx-border-radius: 50;");
            StackPane.setAlignment(badge, Pos.TOP_LEFT);
            StackPane.setMargin(badge, new Insets(5, 0, 0, 5));
            imageContainer.getChildren().addAll(productImage, badge);
        } else {
            imageContainer.getChildren().add(productImage);
        }

        // Product Details
        VBox productDetails = new VBox(3);
        productDetails.setAlignment(Pos.CENTER_LEFT);

        Label productName = new Label(name);
        productName.setStyle("-fx-font-size: 14px; -fx-text-fill: #37474F; -fx-font-weight: bold;");

        // Old Price Label
        Label oldPrice = null;
        if (discount > 0) {
            oldPrice = new Label("$" + String.format("%.2f", price));
            oldPrice.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575; -fx-strikethrough: true;");
        }

        // Discounted Price
        Label productDiscountPrice = new Label("$" + String.format("%.2f", price - (price * discount / 100)));
        productDiscountPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF6B6B;");

        // Total Price Label
        Label totalPriceLabel = new Label("(Total: $" + String.format("%.2f", (price - (price * discount / 100)) * quantity) + ")");
        totalPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #37474F;");

        // Add components to product details
        productDetails.getChildren().add(productName);
        if (oldPrice != null) {
            productDetails.getChildren().add(oldPrice);
        }
        productDetails.getChildren().addAll(productDiscountPrice, totalPriceLabel);

        // Quantity Controls
        HBox quantityControls = new HBox(5);
        quantityControls.setAlignment(Pos.CENTER);

        Button decreaseButton = new Button("-");
        decreaseButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-border-radius: 8;");
        decreaseButton.setPrefSize(30, 30);

        Label quantityLabel = new Label(String.valueOf(quantity));
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #37474F;");

        Button increaseButton = new Button("+");
        increaseButton.setStyle("-fx-background-color: #5EC401; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-border-radius: 8;");
        increaseButton.setPrefSize(30, 30);

        // Inside the decreaseButton action
        decreaseButton.setOnAction(e -> {
            // Show the loading overlay and blur background
            applyBlurredBackgroundEffect(true);
            loadingOverlay.setVisible(true);

            // Capture the current scroll position
            double scrollOffset = productScrollPane.getVvalue();

            if (Integer.parseInt(quantityLabel.getText()) > 1) {
                int newQuantity = Integer.parseInt(quantityLabel.getText()) - 1;
                quantityLabel.setText(String.valueOf(newQuantity));
                totalPriceLabel.setText("(Total: $" + String.format("%.2f", (price - (price * discount / 100)) * newQuantity) + ")");
                updateQuantity(name, -1, quantityLabel);
            } else if (Integer.parseInt(quantityLabel.getText()) == 1) {
                productContainer.getChildren().remove(productBox);
                List<String[]> products = readCartFile(id);
                products.removeIf(productData -> productData[0].equals(name));
                writeCartFile(id, products);
                updateCartSummary();
            }

            // Create the fade-in transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(100), loadingOverlay);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(event -> {
                // Use a timeline for a delay
                Timeline delay = new Timeline(
                        new KeyFrame(
                                Duration.millis(200), // 0.5-second delay while the overlay is fully visible
                                ev -> {
                                    // Restore the scroll position
                                    productScrollPane.layout();
                                    productScrollPane.setVvalue(scrollOffset);

                                    // Create the fade-out transition
                                    FadeTransition fadeOut = new FadeTransition(Duration.millis(100), loadingOverlay);
                                    fadeOut.setFromValue(1);
                                    fadeOut.setToValue(0);
                                    fadeOut.setOnFinished(ev2 -> {
                                        // Hide the overlay completely after fading out
                                        loadingOverlay.setVisible(false);
                                        applyBlurredBackgroundEffect(false);
                                    });
                                    fadeOut.play();
                                }
                        )
                );
                delay.play();
            });

            // Make sure the overlay is visible and start the fade-in animation
            loadingOverlay.setVisible(true);
            fadeIn.play();
        });

        increaseButton.setOnAction(e -> {
            // Show the loading overlay and blur background
            applyBlurredBackgroundEffect(true);
            loadingOverlay.setVisible(true);

            int newQuantity = Integer.parseInt(quantityLabel.getText()) + 1;
            quantityLabel.setText(String.valueOf(newQuantity));
            totalPriceLabel.setText("(Total: $" + String.format("%.2f", (price - (price * discount / 100)) * newQuantity) + ")");
            updateQuantity(name, 1, quantityLabel);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(100), loadingOverlay);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(event -> {
                // Use a timeline for a delay
                Timeline delay = new Timeline(
                        new KeyFrame(
                                Duration.millis(200), // 0.5-second delay while the overlay is fully visible
                                ev -> {

                                    // Create the fade-out transition
                                    FadeTransition fadeOut = new FadeTransition(Duration.millis(100), loadingOverlay);
                                    fadeOut.setFromValue(1);
                                    fadeOut.setToValue(0);
                                    fadeOut.setOnFinished(ev2 -> {
                                        // Hide the overlay completely after fading out
                                        loadingOverlay.setVisible(false);
                                        applyBlurredBackgroundEffect(false);
                                    });
                                    fadeOut.play();
                                }
                        )
                );
                delay.play();
            });

            // Make sure the overlay is visible and start the fade-in animation
            loadingOverlay.setVisible(true);
            fadeIn.play();

        });

        quantityControls.getChildren().addAll(decreaseButton, quantityLabel, increaseButton);

        // Spacer for Alignment
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add elements to the product box
        productBox.getChildren().addAll(imageContainer, productDetails, spacer, quantityControls);

        return productBox;
    }

    private Timeline getTimeline(double scrollOffset) {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(50), // Adjust delay as needed
                        actionEvent -> {
                            productScrollPane.layout();
                            productScrollPane.setVvalue(scrollOffset);
                        }
                )
        );
        timeline.setCycleCount(1);
        return timeline;
    }

    private void setupImages() {
        try {
            // home page button
            Image homeImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/homePageButton.png"));
            ImageView homeImgView = new ImageView(homeImg);
            homeImgView.setFitWidth(30);
            homeImgView.setFitHeight(30);
            homeButton.setGraphic(homeImgView);

            // Menu Button
            Image menuImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/menuPageButton.png"));
            ImageView menuImgView = new ImageView(menuImg);
            menuImgView.setFitWidth(35);
            menuImgView.setFitHeight(35);
            menuButton.setGraphic(menuImgView);

            // Cart Button
            Image cartImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/cartPageButtonClicked.png"));
            ImageView cartImgView = new ImageView(cartImg);
            cartImgView.setFitWidth(35);
            cartImgView.setFitHeight(35);
            cartButton.setGraphic(cartImgView);

            // Settings Button
            Image settingsImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/settingPageButton.png"));
            ImageView settingsImgView = new ImageView(settingsImg);
            settingsImgView.setFitWidth(35);
            settingsImgView.setFitHeight(35);
            settingsButton.setGraphic(settingsImgView);

            // Image for empty_cart
            Image emptyCartImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/empty_cart.png"));
            ImageView emptyCartImgView = new ImageView(emptyCartImg);
            emptyCartImgView.setFitWidth(200);
            emptyCartImgView.setFitHeight(200);
            empty_cart.setImage(emptyCartImg);

        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyBlurredBackgroundEffect(boolean enable) {
        // Access the rootPane's parent (StackPane)
        StackPane parentPane = (StackPane) rootPane.getParent();

        // Check if the overlay already exists
        Rectangle blurOverlay = (Rectangle) parentPane.lookup("#blurOverlay");
        if (blurOverlay == null) {
            // Create a new Rectangle for the blur overlay if it doesn't exist
            blurOverlay = new Rectangle();
            blurOverlay.setId("blurOverlay"); // Set an ID to find it later
            blurOverlay.widthProperty().bind(parentPane.widthProperty());
            blurOverlay.heightProperty().bind(parentPane.heightProperty());
            blurOverlay.setFill(Color.rgb(144, 238, 144, 0.4)); // Light green color with transparency
            parentPane.getChildren().add(blurOverlay); // Add to parent
        }

        if (enable) {
            // Enable blur and show overlay
            GaussianBlur blurEffect = new GaussianBlur(10); // Adjust the radius as needed for more/less blur
            rootPane.setEffect(blurEffect);
            blurOverlay.setVisible(true);
            loadingOverlay.toFront(); // Ensure loadingOverlay appears on top
            loadingOverlay.setVisible(true);
        } else {
            // Remove blur and hide overlay
            rootPane.setEffect(null);
            blurOverlay.setVisible(false);
            loadingOverlay.setVisible(false);
        }
    }

    private void initializeLoadingOverlay() {
        // Set the style and initial visibility of the loading overlay
        loadingOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        loadingOverlay.setVisible(false);

        // Ensure the loadingOverlay spans the full parent size
        loadingOverlay.prefWidthProperty().bind(rootPane.widthProperty());
        loadingOverlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Create a VBox to fill the screen
        VBox fullScreenBox = new VBox();
        fullScreenBox.setAlignment(Pos.CENTER); // Center all content within this VBox
        fullScreenBox.prefWidthProperty().bind(loadingOverlay.widthProperty());
        fullScreenBox.prefHeightProperty().bind(loadingOverlay.heightProperty());

        // Create a VBox for the overlay content
        VBox overlayContent = new VBox(10);
        overlayContent.setAlignment(Pos.CENTER);

        // Add a spinner (ProgressIndicator)
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setStyle("-fx-progress-color: white;");

        // Add a loading label
        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Add spinner and label to the overlay content
        overlayContent.getChildren().addAll(spinner, loadingLabel);

        // Add the overlay content to the full-screen VBox
        fullScreenBox.getChildren().add(overlayContent);

        // Clear any existing children in the loadingOverlay and add the full-screen VBox
        loadingOverlay.getChildren().clear();
        loadingOverlay.getChildren().add(fullScreenBox);

        // Ensure the overlay is aligned within the StackPane
        StackPane.setAlignment(loadingOverlay, Pos.CENTER);
    }

    private void clearFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            // Write nothing to the file to clear its content
            writer.write("");
            System.out.println("File cleared: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to clear file: " + filePath);
            e.printStackTrace();
        }
    }

    private void clearAppFileOnInitialization() {
        // List of files to clear on app initialization
        String[] filesToClear = {
                "src/main/resources/cart.txt"
        };

        for (String file : filesToClear) {
            clearFile(file);
        }
    }

    private User getCurrentUser() {
        // Define the file path where user data is stored
        String usersFilePath = "src/main/resources/users.txt";

        // Try to read the file and locate the user by ID (email)
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;

            // Iterate through each line in the file
            while ((line = reader.readLine()) != null) {
                // Parse the line to extract user information
                List<String> parts = parseCSVLine(line);

                // Ensure the parsed data contains the expected fields
                if (parts.size() >= 8) { // Adjust the field count based on your CSV structure
                    String email = parts.get(1).trim(); // Email is the unique identifier (2nd field)

                    // If the email matches the given ID, create and return a User object
                    if (email.equals(id)) {
                        String name = parts.get(0).trim();
                        String password = parts.get(2).trim();

                        // Parse addresses field (semicolon-separated)
                        String[] addresses = Arrays.stream(parts.get(3).replace("\"", "").split(";"))
                                .map(String::trim)
                                .toArray(String[]::new);

                        // Parse current address
                        String currentAddress = parts.get(4).replace("\"", "").trim();

                        // Parse order IDs (semicolon-separated)
                        int[] orderIDs = Arrays.stream(parts.get(5).split(";"))
                                .map(String::trim)
                                .mapToInt(Integer::parseInt)
                                .toArray();

                        // Parse the last two integer fields
                        int currentOrderID = Integer.parseInt(parts.get(6).trim());
                        int errorCase = Integer.parseInt(parts.get(7).trim());

                        // Return the constructed User object
                        return new User(name, email, password, addresses, currentAddress, orderIDs, currentOrderID, errorCase);
                    }
                } else {
                    System.err.println("Invalid user data format: " + line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading user data: " + e.getMessage());
            e.printStackTrace();
        }

        // Return null if no user matches the given ID
        System.err.println("User with ID " + id + " not found.");
        return null;
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

        for (char c : line.toCharArray()) {
            if (c == '"' && (currentField.isEmpty() || currentField.charAt(currentField.length() - 1) != '\\')) {
                // Toggle inQuotes state when encountering unescaped quotes
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                // Add field to result if we encounter a comma outside quotes
                result.add(currentField.toString());
                currentField.setLength(0); // Clear the current field
            } else {
                // Append character to current field
                currentField.append(c);
            }
        }
        // Add the last field
        result.add(currentField.toString());

        return result;
    }

    public void getID(String id) {
        this.id = id;
        System.out.println("ID: " + id);
    }

    private void switchToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            switch (fxmlFile) {
                case "home_screen.fxml":
                    HomeScreenController homeController = loader.getController();
                    homeController.getID(id);
                    homeController.initialize();
                    break;
                case "menu.fxml":
                    MenuController menuController = loader.getController();
                    menuController.getID(id);
                    break;
                case "settings.fxml":
                    SettingsController settingsController = loader.getController();
                    settingsController.getID(id);
                    break;
                case "location.fxml":
                    LocationController locationController = loader.getController();
                    locationController.getID(id);
                    locationController.setPreviousPage("cart.fxml");
                    break;
                case "cart.fxml":
                    CartController cartController = loader.getController();
                    cartController.getID(id);
                    cartController.initialize();
                    break;
            }
            Stage stage = (Stage) homeButton.getScene().getWindow(); // Get the current stage
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
