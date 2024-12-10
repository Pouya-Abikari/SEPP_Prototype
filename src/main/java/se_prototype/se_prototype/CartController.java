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
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import se_prototype.se_prototype.Model.Product;
import se_prototype.se_prototype.Model.UserCart;

public class CartController {

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
    private Button start_group_order_button;
    @FXML
    private Button join_group_order_button;
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
    private SVGPath paymentIcon;
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

    private boolean isSoloCart = true;
    private long timeRemaining = 3600; // 1 hour in seconds
    private final String CART_FILE = "src/main/resources/cart.txt";
    private final List<UserCart> userCarts = new ArrayList<>();
    private final String saveFile = "src/main/resources/group_cart_users.txt";
    private final List<Product> allProducts = new ArrayList<>();
    private boolean isGroupCartTimerRunning = false;
    private static final String TIMER_STATE_FILE = "src/main/resources/timer_state.txt";
    private ScheduledExecutorService timerService;
    private static boolean isInitialized = false;
    private static int totalUsers;

    @FXML
    public void initialize() {
        if (!isInitialized) {
            clearAppFilesOnInitialization(); // Clear files only once at the start of the app
            int additionalUsers = new Random().nextInt(9) + 1; // 1-9 range
            totalUsers = 1 + additionalUsers; // 1 logged-in user + additional random users
            System.out.println("Total users (including logged-in user): " + totalUsers);
            isInitialized = true; // Set the flag to true to prevent future runs
        }
        loadTimerState();
        setupImages();
        loadCartFromFile();
        updateCartSummary();
        initializeLoadingOverlay();
        payment_icon_and_method();
        loadAllProducts();
        loadLoggedInUserCart();
        loadUserData();
        startRandomUserUpdate();

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
                start_group_order_button.setDisable(false);
                join_group_order_button.setDisable(false);
                solo_order_button.setDisable(false);
                error_payment_method.setVisible(false);
                error_payment_method.setManaged(false);
            } else {
                start_group_order_button.setDisable(true);
                join_group_order_button.setDisable(true);
                solo_order_button.setDisable(true);
                error_payment_method.setVisible(true);
                error_payment_method.setManaged(true);
            }
        });

        // Set up button actions
        homeButton.setOnAction(event -> switchToPage("home_screen.fxml", "Home", null));
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu", null));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings", null));
        addMoreItems.setOnAction(event -> switchToPage("menu.fxml", "Menu", null));
        addMoreItems_empty.setOnAction(event -> switchToPage("menu.fxml", "Menu", null));
        change_location.setOnAction(event -> switchToPage("location.fxml", "Location", "Cart"));
        start_group_order_button.setOnAction(event -> switchToPage("start_group_order.fxml", "Start Group Order", null));
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

        // Calculate totals from all user carts
        for (UserCart userCart : userCarts) {
            if (userCart.getCartItems() != null && !userCart.getCartItems().isEmpty()) {
                for (Product item : userCart.getCartItems()) {
                    int quantity = item.getQuantity();
                    double price = item.getPrice();
                    double discountedPrice = price - (price * item.getDiscount() / 100);

                    totalItems += quantity;
                    subtotal += discountedPrice * quantity;
                }

                // If this is the logged-in user, calculate their subtotal
                if (userCart.getUserName().equals("You")) {
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

    private void loadLoggedInUserCart() {
        File file = new File(CART_FILE);
        if (!file.exists()) {
            System.err.println("Cart file not found: " + CART_FILE);
            return;
        }

        List<Product> cartItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    cartItems.add(new Product(
                            parts[0],
                            parts[1],
                            Double.parseDouble(parts[2]),
                            parts[3],
                            Double.parseDouble(parts[4]),
                            Integer.parseInt(parts[5])
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Replace or add the "You" cart
        userCarts.removeIf(cart -> cart.getUserName().equals("You"));
        UserCart loggedInUserCart = new UserCart("You", cartItems);
        userCarts.add(0, loggedInUserCart);
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
                                saveUserData();
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

        for (UserCart userCart : userCarts) {
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
            Label userNameLabel = new Label(userCart.getUserName());
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

    private void saveUserData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            for (UserCart userCart : userCarts) {
                writer.write(userCart.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        File file = new File(saveFile);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                UserCart userCart = UserCart.fromString(line);

                // Skip adding "You" since it's already added
                if (userCart.getUserName().equals("You")) {
                    continue;
                }

                userCarts.add(userCart);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        Duration.millis(200), // Match the loading overlay duration
                        ev -> {
                            if (isSoloCart) {
                                // Solo Cart View
                                soloLabel.setVisible(true);
                                toggleThumb.setStyle("-fx-fill: #959595;");
                                groupLabel.setVisible(false);
                                animateToggleThumb(-30);
                                group_cart.setVisible(false);
                                group_cart.setManaged(false);
                                productScrollPane.setVisible(true);
                                productScrollPane.setManaged(true);
                            } else {
                                // Group Cart View
                                updateLoggedInUserCartInGroup();
                                updateDeliverySummary();
                                soloLabel.setVisible(false);
                                groupLabel.setVisible(true);
                                toggleThumb.setStyle("-fx-fill: #5EC401;");
                                animateToggleThumb(30);
                                group_cart.setVisible(true);
                                group_cart.setManaged(true);
                                productScrollPane.setVisible(false);
                                productScrollPane.setManaged(false);
                                startGroupCartTimer(); // Start the timer for group mode
                                group_cart.setVvalue(0);
                                screen.requestFocus();
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

    private void startGroupCartTimer() {
        if (isGroupCartTimerRunning) {
            return; // Timer is already running
        }

        isGroupCartTimerRunning = true;
        timerService = Executors.newSingleThreadScheduledExecutor();

        timerService.scheduleAtFixedRate(() -> {
            timeRemaining--;

            if (timeRemaining <= 0) {
                Platform.runLater(() -> {
                    // Handle timer completion (e.g., show a dialog or disable features)
                    System.out.println("Timer completed. Group cart session expired.");
                    stopGroupCartTimer(); // Stop the timer once expired
                });
            } else {
                updateTimerLabel();
            }

        }, 0, 1, TimeUnit.SECONDS);
    }

    private void updateTimerLabel() {
        long minutes = timeRemaining / 60;
        long seconds = timeRemaining % 60;
        String timeText = String.format("Time Remaining: %02d:%02d", minutes, seconds);

        Platform.runLater(() -> timerLabel.setText(timeText)); // Ensure UI updates on the JavaFX thread
    }

    private void stopGroupCartTimer() {
        if (timerService != null) {
            timerService.shutdown();
            isGroupCartTimerRunning = false;
        }
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
        Platform.runLater(() -> {
            // Load updated "You" cart from `cart.txt`
            List<Product> updatedCartItems = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(CART_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        updatedCartItems.add(new Product(
                                parts[0],
                                parts[1],
                                Double.parseDouble(parts[2]),
                                parts[3],
                                Double.parseDouble(parts[4]),
                                Integer.parseInt(parts[5])
                        ));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Replace or add the "You" cart in `userCarts`
            UserCart youCart = new UserCart("You", updatedCartItems);
            userCarts.removeIf(cart -> cart.getUserName().equals("You"));
            userCarts.add(0, youCart);

            // Refresh the group cart UI
            updateGroupCartUI();
        });
    }

    private void overrideYouCart() {
        // Read the logged-in user's solo cart from CART_FILE
        File file = new File(CART_FILE);
        if (!file.exists()) {
            System.err.println("Cart file not found: " + CART_FILE);
            return;
        }

        List<Product> cartItems = new ArrayList<>();
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
                    cartItems.add(new Product(name, description, price, imageUrl, discount, quantity));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create or update the "You" cart in userCarts
        UserCart youCart = new UserCart("You", cartItems);
        boolean youExists = false;

        for (int i = 0; i < userCarts.size(); i++) {
            if (userCarts.get(i).getUserName().equals("You")) {
                userCarts.set(i, youCart); // Replace the existing "You" cart
                youExists = true;
                break;
            }
        }

        if (!youExists) {
            userCarts.add(0, youCart); // Add "You" as the first entry if it doesn't exist
        }

        saveUserData(); // Save the updated group cart to file
        updateGroupCartUI(); // Update the UI
    }

    private void animateToggleThumb(double toX) {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(toggleThumb);
        transition.setToX(toX);
        transition.setDuration(Duration.millis(200)); // Adjust animation duration
        transition.play();
    }

    private void loadGroupCart() {
        userCarts.clear(); // Clear existing group cart
        loadUserData(); // Load users from the group_cart_users.txt file
        overrideYouCart(); // Override "You" section with latest data from cart.txt
        updateGroupCartUI(); // Update the UI
        updateDeliverySummary(); // Update the delivery summary
    }

    private void loadCartFromFile() {
        // Capture the current scroll position using bounds
        double scrollOffset = productScrollPane.getVvalue();

        // Clear and reload products
        productContainer.getChildren().clear();
        List<String[]> products = readCartFile();
        for (String[] productData : products) {
            String name = productData[0];
            String description = productData[1];
            double price = Double.parseDouble(productData[2]);
            String imageUrl = productData[3];
            double discount = Double.parseDouble(productData[4]);
            int quantity = Integer.parseInt(productData[5]);
            productContainer.getChildren().add(createProductNode(name, description, price, imageUrl, discount, quantity));
        }

        // Restore scroll position after layout updates
        productScrollPane.layout();
        productScrollPane.setVvalue(scrollOffset);
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
        List<String[]> products = readCartFile();
        for (String[] productData : products) {
            if (productData[0].equals(name)) {
                int quantity = Integer.parseInt(productData[5]) + delta;
                if (quantity > 0) {
                    productData[5] = String.valueOf(quantity);
                    quantityLabel.setText(String.valueOf(quantity));
                }
            }
        }
        writeCartFile(products);
        updateCartSummary();
    }

    private void updateCartSummary() {
        List<String[]> products = readCartFile();
        int totalItems = 0;
        double subtotal = 0;
        double totalSavings = 0;
        double deliveryCharge = 0.0; // Default delivery charge
        double serviceFee = 0.0; // Default service fee

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

        // Calculate totals
        for (String[] productData : products) {
            int quantity = Integer.parseInt(productData[5]);
            double price = Double.parseDouble(productData[2]);
            double discount = Double.parseDouble(productData[4]);

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

        // Update labels
        subtotalLabel.setText(String.format("$%.2f", subtotal));
        deliveryChargeLabel.setText(String.format("$%.2f", deliveryCharge));
        serviceFeeLabel.setText(String.format("$%.2f", serviceFee));
        totalPriceLabel.setText(String.format("$%.2f", totalPrice));
        totalItemsLabel.setText("Total Items: " + totalItems);

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

    private List<String[]> readCartFile() {
        List<String[]> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CART_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                products.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    private void writeCartFile(List<String[]> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CART_FILE))) {
            for (String[] productData : products) {
                writer.write(String.join(",", productData));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                List<String[]> products = readCartFile();
                products.removeIf(productData -> productData[0].equals(name));
                writeCartFile(products);
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

    private void clearAppFilesOnInitialization() {
        // List of files to clear on app initialization
        String[] filesToClear = {
                "src/main/resources/cart.txt",
                "src/main/resources/group_cart_users.txt"
        };

        for (String file : filesToClear) {
            clearFile(file);
        }
    }

    private void switchToPage(String fxmlFile, String title, String previousPage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 400, 711);
            if ("location.fxml".equals(fxmlFile)) {
                LocationController controller = loader.getController();
                if (controller != null) {
                    controller.setPreviousPage(previousPage);
                }
            }
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
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
