package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.util.List;

public class MenuController {

    @FXML
    public ImageView basket;

    @FXML
    private VBox productContainer;

    @FXML
    private Label basketCountLabel;

    @FXML
    private Button checkoutButton;

    private int basketCount = 0;

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
        setupBasketImage();
        //checkoutButton.setOnAction(event -> handleCheckout());
    }

    private void loadProducts() {
        for (Product product : products) {
            HBox productCard = createProductCard(product); // Create a product card
            productContainer.getChildren().add(productCard); // Add it to the productContainer VBox
        }
    }

    private HBox createProductCard(Product product) {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 10;");
        card.setPrefWidth(360);

        // Product details container
        VBox details = new VBox(5);
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label descriptionLabel = new Label(product.getDescription());
        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
        details.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);

        // Add to Basket button
        Button addToBasketButton = new Button("Add to Basket");
        addToBasketButton.setOnAction(event -> addToBasket(product));

        // Combine details and button into the card
        card.getChildren().addAll(details, addToBasketButton);
        return card;
    }

    private void addToBasket(Product product) {
        basketCount++;
        basketCountLabel.setText(String.valueOf(basketCount));
        System.out.println(product.getName() + " added to basket.");
    }

    private void handleCheckout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Checkout");
        alert.setHeaderText("Basket Summary");
        alert.setContentText("You have " + basketCount + " items in your basket.");
        alert.showAndWait();
    }

    @FXML
    private void setupBasketImage() {
        try {
            // Correct the resource path
            Image image = new Image(getClass().getResourceAsStream("/basket.png")); // Adjust path if img.png is in a subfolder
            basket.setImage(image);
            basket.setFitWidth(30); // Adjust as needed
            basket.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Simple Product Class
    static class Product {
        private final String name;
        private final String description;
        private final double price;
        private final String imagePath;

        public Product(String name, String description, double price, String imagePath) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.imagePath = imagePath;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public double getPrice() {
            return price;
        }

        public String getImagePath() {
            return imagePath;
        }
    }
}
