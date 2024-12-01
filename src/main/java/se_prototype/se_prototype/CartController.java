package se_prototype.se_prototype;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CartController {

    @FXML
    private Button start_group_order_button;
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

    private final String CART_FILE = "src/main/resources/cart.txt";

    @FXML
    public void initialize() {
        setupImages();
        loadCartFromFile();
        updateCartSummary();
        initializeLoadingOverlay();
        payment_icon_and_method();

        // Set up button actions
        homeButton.setOnAction(event -> switchToPage("home_screen.fxml", "Home"));
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings"));
        addMoreItems.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        addMoreItems_empty.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        change_location.setOnAction(event -> switchToPage("location.fxml", "Location"));
        start_group_order_button.setOnAction(event -> switchToPage("start_group_order.fxml", "Start Group Order"));
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
        double totalPrice = 0;
        double totalSavings = 0;
        double deliveryCharge = 10.0;

        // If the cart is empty, show the empty cart label and hide product list
        if (products.isEmpty()) {
            empty_cart_label.setVisible(true);
            empty_cart_label.setManaged(true);
            productScrollPane.setVisible(false);
            productScrollPane.setManaged(false);
            return;
        } else {
            empty_cart_label.setVisible(false);
            empty_cart_label.setManaged(false);
            productScrollPane.setVisible(true);
            productScrollPane.setManaged(true);
        }

        for (String[] productData : products) {
            int quantity = Integer.parseInt(productData[5]);
            double price = Double.parseDouble(productData[2]);
            double discount = Double.parseDouble(productData[4]);

            totalItems += quantity;
            double discountedPrice = price - (price * discount / 100);
            totalPrice += quantity * discountedPrice;
            totalSavings += quantity * (price - discountedPrice);
        }

        // Adjust delivery charge dynamically
        if (totalPrice > 50) {
            deliveryCharge = 5.0;
        }

        // Update labels
        totalItemsLabel.setText("Total Items: " + totalItems);
        totalPriceLabel.setText("$" + String.format("%.2f", totalPrice));
        if (totalSavings > 0) {
            totalSavingsLabel.setText("Total Savings: $" + String.format("%.2f", totalSavings));
            HBox_discount.setVisible(true);
            HBox_discount.setManaged(true);
        } else {
            totalSavingsLabel.setText("");
            HBox_discount.setVisible(false);
            HBox_discount.setManaged(false);
        }
        deliveryChargeLabel.setText("$" + String.format("%.2f", deliveryCharge));
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
                        new javafx.animation.KeyFrame(
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
                        new javafx.animation.KeyFrame(
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
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.millis(50), // Adjust delay as needed
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

    private void switchToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 400, 711);
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
