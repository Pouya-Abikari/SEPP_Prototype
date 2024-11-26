package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import se_prototype.se_prototype.Model.Product;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryController {

    @FXML
    private Label categoryTitle;

    @FXML
    private GridPane productGrid;

    private MenuController menuController;

    public void initializeCategory(String categoryName, List<Product> products, MenuController menuController) {
        this.menuController = menuController;
        categoryTitle.setText(categoryName);

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

    private VBox createProductCard(Product product) {
        VBox productCard = new VBox();
        productCard.setSpacing(2);
        productCard.setAlignment(javafx.geometry.Pos.CENTER); // Center alignment
        productCard.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #D9D9D9; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        productCard.setPrefWidth(140);
        productCard.setPrefHeight(140);
        productCard.setMaxWidth(140);
        productCard.setMaxHeight(140);

        ImageView productImage = new ImageView(new Image(product.getImageUrl()));
        productImage.setFitHeight(80);
        productImage.setFitWidth(80);
        productImage.setPreserveRatio(true);

        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #37474F;");
        productName.setAlignment(javafx.geometry.Pos.CENTER); // Center text

        Label productPrice = new Label("$" + product.getPrice());
        productPrice.setStyle("-fx-text-fill: #FF5722; -fx-font-size: 12px;");
        productPrice.setAlignment(javafx.geometry.Pos.CENTER);

        Button addToBagButton = new Button("Add to Bag");
        addToBagButton.setStyle("-fx-background-color: #5EC401; -fx-text-fill: white; -fx-padding: 5 10;");

        productName.setWrapText(true);
        productPrice.setPrefHeight(20);
        addToBagButton.setPrefHeight(30);
        addToBagButton.setOnAction(event -> addToCart(product));

        productCard.getChildren().addAll(productImage, productName, productPrice, addToBagButton);
        return productCard;

    }

    private void addToCart(Product product) {
        List<String[]> cartItems = readCartFile();
        boolean productExists = false;

        for (String[] cartItem : cartItems) {
            if (cartItem[0].equals(product.getName())) {
                int currentQuantity = Integer.parseInt(cartItem[5]);
                cartItem[5] = String.valueOf(currentQuantity + 1);
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            // Add a new product to the cart
            cartItems.add(new String[]{
                    product.getName(),
                    product.getDescription(),
                    String.valueOf(product.getPrice()),
                    product.getImageUrl(),
                    String.valueOf(product.getDiscount()),
                    String.valueOf(1) // Default quantity is 1
            });
        }

        writeCartFile(cartItems); // Write the updated cart back to the file
        System.out.println("Added to cart: " + product.getName());
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



    @FXML
    private void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Scene scene = new Scene(loader.load() , 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) productGrid.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load menu.fxml");
            e.printStackTrace();
        }
    }
}
