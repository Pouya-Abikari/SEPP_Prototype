package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
    private String id = "jane.smith@example.com";


    public void initializeCategory(String categoryName, List<Product> products, MenuController menuController) {
        this.menuController = menuController;
        categoryTitle.setText(categoryName);
        cartItemCount = 0; // Reset count
        initializeCartCount(); // Re-initialize cart count
        productGrid.getChildren().clear(); // Clear previous products

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
        List<String[]> cartItems = readCartFile();
        cartItemCount = cartItems.stream()
                .filter(cartItem -> cartItem.length > 1 && !cartItem[1].isEmpty()) // Ensure items exist
                .mapToInt(cartItem -> {
                    return List.of(cartItem[1].split(";")).stream()
                            .filter(item -> !item.isEmpty())
                            .mapToInt(item -> {
                                String[] details = item.split(",");
                                if (details.length > 5) {
                                    return Integer.parseInt(details[5]); // Quantity
                                }
                                return 0; // Default to 0 if data is invalid
                            }).sum();
                }).sum();
        cartItemCountLabel.setText(String.valueOf(cartItemCount)); // Set the label to show count
    }


    private void updateCartCount() {
        List<String[]> cartItems = readCartFile();
        cartItemCount = cartItems.stream()
                .mapToInt(cartItem -> {
                    if (cartItem.length > 1 && !cartItem[1].isEmpty()) {
                        // Sum up quantities from the items part
                        return (int) List.of(cartItem[1].split(";")).stream()
                                .filter(item -> !item.isEmpty())
                                .mapToInt(item -> {
                                    String[] details = item.split(",");
                                    return Integer.parseInt(details[5]); // Quantity
                                }).sum();
                    }
                    return 0; // No items for this user
                }).sum();
        cartItemCountLabel.setText(String.valueOf(cartItemCount));
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
        // Read the current cart data
        List<String[]> cartData = readCartFile();
        boolean userExists = false;

        // Iterate through each entry to find the user's cart
        for (int i = 0; i < cartData.size(); i++) {
            String[] entry = cartData.get(i);
            String email = entry[0]; // First element is the email
            String items = entry.length > 1 ? entry[1] : ""; // Second element is the items string

            if (email.equals(userEmail)) {
                // User found, process their cart
                userExists = true;
                List<String> updatedItems = new ArrayList<>();
                boolean productExists = false;

                for (String item : items.split(";")) {
                    if (item.isEmpty()) continue; // Skip empty items
                    String[] itemDetails = item.split(",");
                    if (itemDetails[0].equals(product.getName())) {
                        // Update quantity if product exists
                        int currentQuantity = Integer.parseInt(itemDetails[5]);
                        itemDetails[5] = String.valueOf(currentQuantity + 1);
                        updatedItems.add(String.join(",", itemDetails));
                        productExists = true;
                    } else {
                        // Add existing items back
                        updatedItems.add(item);
                    }
                }

                // Add the new product if it doesn't already exist
                if (!productExists) {
                    updatedItems.add(formatProduct(product));
                }

                // Update the user's cart data with a semicolon at the end
                cartData.set(i, new String[]{userEmail, String.join(";", updatedItems)});
                break;
            }
        }

        // If the user does not already exist in the cart file, add them with the product
        if (!userExists) {
            cartData.add(new String[]{userEmail, formatProduct(product)});
        }

        // Write the updated cart data back to the file
        writeCartFile(cartData);

        // Update the cart count and log the action
        updateCartCount();
        System.out.println("Added to cart: " + product.getName());
    }



    /**
     * Helper method to format a product into a string for writing to the cart file.
     */
    private String formatProduct(Product product) {
        return String.join(",",
                product.getName(),
                product.getDescription(),
                String.valueOf(product.getPrice()),
                product.getImageUrl(),
                String.valueOf(product.getDiscount()),
                "1" // Default quantity is 1 when adding a new product
        );
    }



    // Helper method to read the cart file as lines
    private List<String[]> readCartFile() {
        List<String[]> cartItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CART_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into email and items
                String[] parts = line.split(";", 2);
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    cartItems.add(new String[]{parts[0], ""}); // No items
                } else {
                    // Remove the trailing semicolon before adding to the list
                    String items = parts[1].endsWith(";") ? parts[1].substring(0, parts[1].length() - 1) : parts[1];
                    cartItems.add(new String[]{parts[0], items});
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading cart file: " + e.getMessage());
        }
        return cartItems;
    }





    // Helper method to write the cart file lines
    private void writeCartFile(List<String[]> cartItems) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CART_FILE))) {
            for (String[] cartItem : cartItems) {
                String email = cartItem[0];
                String items = cartItem.length > 1 ? cartItem[1] : "";
                String line = email + ";" + items.trim();
                if (!line.endsWith(";")) {
                    line += ";"; // Ensure semicolon at the end
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to cart file: " + e.getMessage());
        }
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

    public void getID(String id) {
        this.id = id;
        System.out.println("ID: " + id);
    }
}
