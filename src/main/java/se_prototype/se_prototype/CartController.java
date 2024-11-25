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
    private VBox menuIcon;

    private final String CART_FILE = "src/main/resources/cart.txt";

    @FXML
    public void initialize() {
        loadCartFromFile();
        updateCartSummary();
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
        productBox.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8;");
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

    @FXML
    private void navigateToMenuPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/menu.fxml"));
            Scene signupScene = new Scene(loader.load(), 400, 711);
            Stage stage = (Stage) menuIcon.getScene().getWindow();
            stage.setScene(signupScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/home_screen.fxml"));
            Scene signupScene = new Scene(loader.load(), 400, 711);
            Stage stage = (Stage) menuIcon.getScene().getWindow();
            stage.setScene(signupScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/settings.fxml"));
            Scene signupScene = new Scene(loader.load(), 400, 711);
            Stage stage = (Stage) menuIcon.getScene().getWindow();
            stage.setScene(signupScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
