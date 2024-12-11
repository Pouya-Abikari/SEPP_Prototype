package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import se_prototype.se_prototype.Model.Product;
import se_prototype.se_prototype.Model.UserCart;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class  CategoryController {

    @FXML
    private Label categoryTitle;

    @FXML
    private GridPane productGrid;

    private MenuController menuController;
    @FXML
    private Button cartButton;

    @FXML
    private Label cartItemCountLabel;

    private final String CART_FILE = "src/main/resources/cart.txt";

    private int cartItemCount = 0;
    private String id;


    public void initializeCategory(String categoryName, List<Product> products, MenuController menuController) {
        this.menuController = menuController;
        categoryTitle.setText(categoryName);
        initializeCartCount();

        setup_Images();
        int column = 0;
        int row = 0;
        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productGrid.add(productCard, column, row);

            column++;
            if (column == 2) {
                column = 0;
                row++;
            }
        }
    }

    private void initializeCartCount() {
        List<UserCart> userCarts = loadCartsFromFile();
        Optional<UserCart> currentUserCart = userCarts.stream()
                .filter(cart -> cart.getEmail().equals(id))
                .findFirst();

        cartItemCount = currentUserCart.map(cart -> cart.getCartItems().stream()
                .mapToInt(Product::getQuantity)
                .sum()).orElse(0);

        cartItemCountLabel.setText(String.valueOf(cartItemCount));
    }

    private void updateCartCount() {
        initializeCartCount();
    }

    @FXML
    private void goToCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cart.fxml"));
            Scene scene = new Scene(loader.load(), 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            CartController controller = loader.getController();
            controller.getID(id);
            Stage stage = (Stage) cartButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load cart.fxml");
            e.printStackTrace();
        }
    }


    private VBox createProductCard(Product product) {
       VBox productCard = new VBox();
       productCard.setSpacing(2);
       productCard.setAlignment(javafx.geometry.Pos.CENTER); // Center alignment
       productCard.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #D9D9D9; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");

       productCard.setPrefWidth(140);
       productCard.setPrefHeight(140);
       productCard.setMaxWidth(140);
       productCard.setMaxHeight(140);

       StackPane imageContainer = new StackPane();
       imageContainer.setPrefSize(80, 80);

       ImageView productImage = new ImageView(new Image(product.getImageUrl()));
       productImage.setFitHeight(80);
       productImage.setFitWidth(80);
       productImage.setPreserveRatio(true);
       productImage.setStyle("-fx-border-radius: 8;");

       if (product.getDiscount() > 0) {
           Label badge = new Label((int) product.getDiscount() + "% OFF");
           badge.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4; -fx-border-radius: 50;");
           StackPane.setAlignment(badge, Pos.TOP_LEFT);
           StackPane.setMargin(badge, new Insets(5, 0, 0, 5));
           imageContainer.getChildren().addAll(productImage, badge);
       } else {
           imageContainer.getChildren().add(productImage);
       }

       Label productName = new Label(product.getName());
       productName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #37474F;");
       productName.setAlignment(javafx.geometry.Pos.CENTER);
       productName.setWrapText(true);

       HBox priceBox = new HBox();
       priceBox.setSpacing(5);
       priceBox.setAlignment(Pos.CENTER);

       Label oldPrice = null;
       if (product.getDiscount() > 0) {
           oldPrice = new Label("$" + String.format("%.2f", product.getPrice()));
           oldPrice.setStyle("-fx-font-size: 10px; -fx-text-fill: #757575; -fx-strikethrough: true;");
       }

       Label discountedPrice = new Label("$" + String.format("%.2f", product.getPrice() - (product.getPrice() * product.getDiscount() / 100)));
       discountedPrice.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #FF5722;");

       if (product.getDiscount() > 0) {
           priceBox.getChildren().addAll(oldPrice, discountedPrice); // Add both prices
       } else {
           priceBox.getChildren().add(discountedPrice); // Add only discounted price
       }

       Button addToBagButton = new Button("Add to Bag");
       addToBagButton.setStyle("-fx-background-color: #5EC401; -fx-text-fill: white; -fx-padding: 5 10;");
       addToBagButton.setPrefHeight(30);
       addToBagButton.setOnAction(event -> addToCart(product, id));

       productCard.getChildren().addAll(imageContainer, productName, priceBox, addToBagButton);

       return productCard;
   }


    private void addToCart(Product product, String userEmail) {
        // Load existing carts from the file
        List<UserCart> userCarts = loadCartsFromFile();

        // Find or create the cart for the specified user
        UserCart userCart = userCarts.stream()
                .filter(cart -> cart.getEmail().equals(userEmail))
                .findFirst()
                .orElseGet(() -> {
                    UserCart newCart = new UserCart(userEmail, new ArrayList<>());
                    userCarts.add(newCart);
                    return newCart;
                });

        // Check if the product already exists in the user's cart
        List<Product> cartItems = userCart.getCartItems();
        Optional<Product> existingProduct = cartItems.stream()
                .filter(item -> item.getName().equals(product.getName()))
                .findFirst();

        if (existingProduct.isPresent()) {
            // Update the quantity of the existing product
            Product existing = existingProduct.get();
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            // Add the new product to the user's cart
            product.setQuantity(1); // Set initial quantity to 1
            cartItems.add(product);
        }

        // Save the updated carts back to the file
        saveCartsToFile(userCarts);
        updateCartCount();
        System.out.println("Added to cart: " + product.getName());
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
                String[] parts = line.split(";", 2); // Split into email and cart items
                if (parts.length == 2) {
                    String email = parts[0];
                    String[] productsData = parts[1].split(";");
                    List<Product> products = new ArrayList<>();

                    for (String productData : productsData) {
                        String[] productParts = productData.split(",");
                        if (productParts.length >= 6) {
                            products.add(new Product(
                                    productParts[0], // Name
                                    productParts[1], // Description
                                    Double.parseDouble(productParts[2]), // Price
                                    productParts[3], // Image URL
                                    Double.parseDouble(productParts[4]), // Discount
                                    Integer.parseInt(productParts[5]) // Quantity
                            ));
                        }
                    }

                    userCarts.add(new UserCart(email, products));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userCarts;
    }

    /**
     * Saves all carts to the cart file.
     *
     * @param userCarts The list of UserCart objects to save.
     */
    private void saveCartsToFile(List<UserCart> userCarts) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CART_FILE))) {
            for (UserCart userCart : userCarts) {
                StringBuilder line = new StringBuilder();

                // Handle null email gracefully
                String email = userCart.getEmail() != null ? userCart.getEmail() : "Unknown";
                line.append(email);

                for (Product product : userCart.getCartItems()) {
                    String name = product.getName() != null ? product.getName() : "Unnamed";
                    String description = product.getDescription() != null ? product.getDescription() : "No Description";
                    String imageUrl = product.getImageUrl() != null ? product.getImageUrl() : "No Image";
                    double price = product.getPrice();
                    double discount = product.getDiscount();
                    int quantity = product.getQuantity();

                    line.append(";")
                            .append(name).append(",")
                            .append(description).append(",")
                            .append(price).append(",")
                            .append(imageUrl).append(",")
                            .append(discount).append(",")
                            .append(quantity);
                }

                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String[]> readCartFile() {
        List<String[]> cartItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/cart.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                cartItems.add(line.split(","));
            }
        } catch (IOException e) {
            System.err.println("Error reading cart file: " + e.getMessage());
        }
        return cartItems;
    }

    private void writeCartFile(List<String[]> cartItems) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/cart.txt"))) {
            for (String[] cartItem : cartItems) {
                writer.write(String.join(",", cartItem));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to cart file: " + e.getMessage());
        }
    }

    public void getID(String id) {
        this.id = id;
        System.out.println("ID: " + id);
    }

    @FXML
    private void setup_Images() {
        Image homeImg = new Image(getClass().getResourceAsStream("/finalCart.png"));
        ImageView homeImgView = new ImageView(homeImg);
        homeImgView.setFitWidth(30);
        homeImgView.setFitHeight(30);
        cartButton.setGraphic(homeImgView);
    }

    @FXML
    private void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Scene scene = new Scene(loader.load() , 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            MenuController controller = loader.getController();
            controller.getID(id);
            Stage stage = (Stage) productGrid.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load menu.fxml");
            e.printStackTrace();
        }
    }
}
