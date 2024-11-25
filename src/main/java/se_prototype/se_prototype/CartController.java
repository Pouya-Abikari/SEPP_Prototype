package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CartController {

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

    private final String CART_FILE = "src/main/resources/cart.txt";

    @FXML
    public void initialize() {
        loadCartFromFile();
        updateCartSummary();

        setupImages();

        // Set up button actions
        homeButton.setOnAction(event -> switchToPage("home_screen.fxml", "Home"));
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings"));
    }

    private void loadCartFromFile() {
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
        for (String[] productData : products) {
            int quantity = Integer.parseInt(productData[5]);
            double price = Double.parseDouble(productData[2]);
            double discount = Double.parseDouble(productData[4]);

            totalItems += quantity;
            totalPrice += quantity * (price - (price * discount / 100));
        }
        totalItemsLabel.setText("Total Items: " + totalItems);
        totalPriceLabel.setText("Total Price: $" + String.format("%.2f", totalPrice));
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

        // Update the quantity and total price on button clicks
        decreaseButton.setOnAction(e -> {
            if (Integer.parseInt(quantityLabel.getText()) > 1) {
                int newQuantity = Integer.parseInt(quantityLabel.getText()) - 1;
                quantityLabel.setText(String.valueOf(newQuantity));
                totalPriceLabel.setText("(Total: $" + String.format("%.2f", (price - (price * discount / 100)) * newQuantity) + ")");
                updateQuantity(name, -1, quantityLabel);
            }
        });

        increaseButton.setOnAction(e -> {
            int newQuantity = Integer.parseInt(quantityLabel.getText()) + 1;
            quantityLabel.setText(String.valueOf(newQuantity));
            totalPriceLabel.setText("(Total: $" + String.format("%.2f", (price - (price * discount / 100)) * newQuantity) + ")");
            updateQuantity(name, 1, quantityLabel);
        });

        quantityControls.getChildren().addAll(decreaseButton, quantityLabel, increaseButton);

        // Spacer for Alignment
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add elements to the product box
        productBox.getChildren().addAll(imageContainer, productDetails, spacer, quantityControls);

        return productBox;
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



        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
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
