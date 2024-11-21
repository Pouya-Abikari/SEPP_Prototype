package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.util.List;

public class MenuController {

    @FXML
    private VBox productContainer;

    @FXML
    private Label basketCountLabel;

    @FXML
    private Button checkoutButton;

    private int basketCount = 0;

    private List<Product> products = List.of(
            new Product("Apple", "Fresh red apples", 1.0, "apple.png"),
            new Product("Banana", "Ripe yellow bananas", 0.5, "banana.png"),
            new Product("Carrot", "Organic carrots", 0.8, "carrot.png")
    );

    @FXML
    public void initialize() {
        loadProducts();
        checkoutButton.setOnAction(event -> handleCheckout());
    }

    private void loadProducts() {
        for (Product product : products) {
            productContainer.getChildren().add(createProductCard(product));
        }
    }

    private HBox createProductCard(Product product) {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 10;");
        card.setPrefWidth(360);

        // Product Image
        //ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(product.getImagePath())));
        //imageView.setFitHeight(50);
        //imageView.setFitWidth(50);

        // Product Details
        VBox details = new VBox(5);
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label descriptionLabel = new Label(product.getDescription());
        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
        details.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);

        // Add to Basket Button
        Button addToBasketButton = new Button("Add to Basket");
        addToBasketButton.setOnAction(event -> addToBasket(product));

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
