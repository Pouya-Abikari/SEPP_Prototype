package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se_prototype.se_prototype.Model.Product;
import java.io.IOException;
import java.util.List;

public class CartController {

    @FXML
    private VBox productContainer;

    @FXML
    private VBox menuIcon;

    private final List<Product> products = List.of(
            new Product("Tomatoes", "Fresh tomatoes from Fresh Farms", 1.20, "tomatoes.png"),
            new Product("Celery", "Crunchy celery from Fresh Farms", 0.95, "celery.png"),
            new Product("Onion", "Organic onions from Green Growers", 0.85, "onion.png"),
            new Product("Potatoes", "Fresh potatoes from Green Growers", 0.65, "potatoes.png"),
            new Product("Lettuce", "Crisp lettuce from Green Growers", 1.10, "lettuce.png"),
            new Product("Bananas", "Sweet bananas from Tropical Harvest", 0.70, "bananas.png"),
            new Product("Apples", "Juicy apples from Tropical Harvest", 1.50, "apples.png"),
            new Product("Oranges", "Fresh oranges from Citrus Direct", 1.30, "oranges.png"),
            new Product("Grapes", "Sweet grapes from Citrus Direct", 2.00, "grapes.png"),
            new Product("Milk", "Fresh milk from Dairyland Supply", 1.25, "milk.png"),
            new Product("Cheese", "Delicious cheese from Dairyland Supply", 2.50, "cheese.png"),
            new Product("Butter", "Creamy butter from Dairyland Supply", 1.75, "butter.png"),
            new Product("Yogurt", "Healthy yogurt from Healthy Creamery", 0.80, "yogurt.png"),
            new Product("Eggs", "Farm fresh eggs from Healthy Creamery", 3.00, "eggs.png"),
            new Product("Chicken Breast", "Tender chicken breast from Farm Fresh Meats", 4.00, "chicken_breast.png"),
            new Product("Ground Beef", "Premium ground beef from Farm Fresh Meats", 5.50, "ground_beef.png"),
            new Product("Pork Chops", "Juicy pork chops from Butcher's Best", 4.75, "pork_chops.png"),
            new Product("Salmon Fillet", "Fresh salmon fillet from Ocean's Harvest", 10.00, "salmon_fillet.png"),
            new Product("Shrimp", "Delicious shrimp from Ocean's Harvest", 12.00, "shrimp.png"),
            new Product("Bread", "Freshly baked bread from Baker's Delight", 2.00, "bread.png"),
            new Product("Croissant", "Buttery croissants from Baker's Delight", 1.50, "croissant.png"),
            new Product("Cereal", "Nutritious cereal from Pantry Provisions", 3.25, "cereal.png"),
            new Product("Pasta", "High-quality pasta from Pantry Provisions", 1.80, "pasta.png"),
            new Product("Olive Oil", "Premium olive oil from Gourmet Supplies", 6.50, "olive_oil.png"),
            new Product("Coffee Beans", "Rich coffee beans from Gourmet Supplies", 8.00, "coffee_beans.png")
    );

    @FXML
    public void initialize() {
        loadProducts();
    }

    private void loadProducts() {
        productContainer.getChildren().clear();

        for (Product product : products) {
            HBox productBox = createProductNode(product);
            productContainer.getChildren().add(productBox);
        }
    }

    private HBox createProductNode(Product product) {
        HBox productBox = new HBox(10);
        productBox.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-radius: 8; -fx-border-color: #D9D9D9;");
        productBox.setSpacing(10);

        // Product Image
        ImageView productImage = new ImageView(new Image(product.getImageUrl()));
        productImage.setFitWidth(60);
        productImage.setFitHeight(60);
        productImage.setPreserveRatio(true);

        // Product Details
        VBox productDetails = new VBox(5);
        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-size: 14px; -fx-text-fill: #37474F;");
        Label productPrice = new Label();
        productPrice.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575; -fx-strikethrough: true;");
        Label productDiscountPrice = new Label(product.getDiscountPrice(20));
        productDiscountPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF6B6B;");
        productDetails.getChildren().addAll(productName, productPrice, productDiscountPrice);

        // Quantity Controls
        HBox quantityControls = new HBox(5);
        Button decreaseButton = new Button("-");
        decreaseButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 8;");
        Label quantityLabel = new Label("1");
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #37474F;");
        Button increaseButton = new Button("+");
        increaseButton.setStyle("-fx-background-color: #5EC401; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 8;");
        quantityControls.getChildren().addAll(decreaseButton, quantityLabel, increaseButton);

        Region spacer = new Region();
        spacer.setMinWidth(Region.USE_COMPUTED_SIZE);

        productBox.getChildren().addAll(productImage, productDetails, spacer, quantityControls);

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
}
