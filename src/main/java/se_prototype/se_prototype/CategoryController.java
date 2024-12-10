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
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class  CategoryController {

    @FXML
    private Label categoryTitle;
    @FXML
    private GridPane productGrid;
    @FXML
    private MenuController menuController;
    @FXML
    private Button cartButton;
    @FXML
    private Label cartItemCountLabel;

    private int cartItemCount = 0;
    private String userFile;

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
        List<String[]> cartItems = readCartFile();
        cartItemCount = cartItems.stream()
                .mapToInt(cartItem -> Integer.parseInt(cartItem[5])) // Quantity of each item
                .sum();
        cartItemCountLabel.setText(String.valueOf(cartItemCount)); // Set the label to show count
    }

    private void updateCartCount() {
        List<String[]> cartItems = readCartFile();
        cartItemCount = cartItems.stream()
                .mapToInt(cartItem -> Integer.parseInt(cartItem[5]))
                .sum();
        cartItemCountLabel.setText(String.valueOf(cartItemCount));
    }
    @FXML
    private void goToCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cart.fxml"));
            Scene scene = new Scene(loader.load(), 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
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
       addToBagButton.setOnAction(event -> addToCart(product));

       productCard.getChildren().addAll(imageContainer, productName, priceBox, addToBagButton);

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
            cartItems.add(new String[]{
                    product.getName(),
                    product.getDescription(),
                    String.valueOf(product.getPrice()),
                    product.getImageUrl(),
                    String.valueOf(product.getDiscount()),
                    String.valueOf(1)
            });
        }

        writeCartFile(cartItems);
        updateCartCount();
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
    private void setup_Images() {
        Image homeImg = new Image(getClass().getResourceAsStream("/finalCart.png"));
        ImageView homeImgView = new ImageView(homeImg);
        homeImgView.setFitWidth(30);
        homeImgView.setFitHeight(30);
        cartButton.setGraphic(homeImgView);
    }

    public void setUserFile(String userFile) {
        this.userFile = userFile;
        System.out.println("User file set to: " + userFile);
    }

    @FXML
    private void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Scene scene = new Scene(loader.load() , 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            MenuController menuController = loader.getController();
            menuController.setUserFile(userFile);
            Stage stage = (Stage) productGrid.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load menu.fxml");
            e.printStackTrace();
        }
    }
}
