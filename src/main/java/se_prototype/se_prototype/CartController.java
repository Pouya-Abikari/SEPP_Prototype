package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import se_prototype.se_prototype.Model.Product;

import java.util.List;

public class CartController {

    @FXML
    private VBox productContainer;

    // Example Product Data
    private final List<Product> products = List.of(
            new Product("Arla DANO Full Cream Milk Powder Instant", "$3.50", 2.80, "basket.png"),
            new Product("Nestle Nido Full Cream Milk Powder Instant", "$7.50", 6.00, "basket.png")
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
}