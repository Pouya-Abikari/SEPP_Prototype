package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.image.Image;
import se_prototype.se_prototype.Model.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;


public class MenuController {

    @FXML
    private TextField searchField;

    @FXML
    private Label locationText;

    @FXML
    private GridPane menuGrid;

    @FXML
    private HBox locationContainer;

    public void initialize() {
        StackPane svgIcon = createSvgIcon();
        locationContainer.getChildren().add(0, svgIcon);
        locationContainer.setOnMouseClicked(this::onLocationBoxClick);
        List<Product> products = List.of(
                new Product("Fruits & Vegetables", "Fresh fruits and veggies", 5.0, "mango.png", 0 , 1),
                new Product("Breakfast", "Start your day right", 10.0, "breakfast.png", 0, 1),
                new Product("Beverages", "Quench your thirst", 3.0, "beverages.png", 0,1),
                new Product("Meat & Fish", "Freshly sourced", 15.0, "meat.png",0,1),
                new Product("Snacks", "Delicious treats", 2.0, "snacks.png",0,1),
                new Product("Dairy", "Milk and dairy products", 4.0, "milk.png",0,1),
                new Product("Frozen Foods", "Quick and convenient meals", 7.0, "milk.png",0,1),
                new Product("Bakery", "Freshly baked goods", 6.0, "milk.png",0,1),
                new Product("Household Supplies", "Everyday essentials", 12.0, "milk.png",0,1),
                new Product("Personal Care", "Care for yourself", 8.0, "milk.png",0,1)
        );

        populateMenu(products);
    }
    private void populateMenu(List<Product> products) {
        int column = 0;
        int row = 0;

        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productCard.setOnMouseClicked(event -> navigateToCategory(product.getName()));
            menuGrid.add(productCard, column, row);
            column++;
            if (column == 2) {
                column = 0;
                row++;
            }
        }
        menuGrid.setAlignment(javafx.geometry.Pos.CENTER);
    }


    private VBox createProductCard(Product product) {
        VBox productCard = new VBox();
        productCard.setSpacing(5);
        productCard.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #D9D9D9; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        productCard.setAlignment(javafx.geometry.Pos.CENTER);

        productCard.setPrefWidth(140);
        productCard.setPrefHeight(140);

        ImageView productImage = new ImageView(new Image(product.getImageUrl()));
        productImage.setFitHeight(80);
        productImage.setFitWidth(80);
        productImage.setPreserveRatio(true);

        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #37474F;");

        productCard.getChildren().addAll(productImage, productName);

        return productCard;
    }

    private StackPane createSvgIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M12 2C8.13 2 5 5.13 5 9C5 13.17 9.42 18.92 11.24 21.11C11.64 21.59 12.37 21.59 12.77 21.11C14.58 18.92 19 13.17 19 9C19 5.13 15.87 2 12 2ZM7 9C7 6.24 9.24 4 12 4C14.76 4 17 6.24 17 9C17 11.88 14.12 16.19 12 18.88C9.92 16.21 7 11.85 7 9ZM9.5 9C9.5 10.38 10.62 11.5 12 11.5C13.38 11.5 14.5 10.38 14.5 9C14.5 7.62 13.38 6.5 12 6.5C10.62 6.5 9.5 7.62 9.5 9Z");
        svgPath.setFill(Color.web("#5EC401")); // Set the icon's color
        StackPane svgContainer = new StackPane(svgPath);
        svgContainer.setPrefSize(32, 32); // Adjusted size for the layout
        svgContainer.setStyle("-fx-padding: 5;");

        return svgContainer;
    }

    @FXML
    private void onLocationBoxClick(MouseEvent event) {
        System.out.println("Location box clicked!");
    }

    private void navigateToCategory(String categoryName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("category.fxml"));
            Parent categoryPage = loader.load();

            CategoryController controller = loader.getController();
            List<Product> products = fetchProductsForCategory(categoryName); // Fetch products for this category
            controller.initializeCategory(categoryName, products, this);

            Stage stage = (Stage) menuGrid.getScene().getWindow();
            stage.getScene().setRoot(categoryPage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Product> fetchProductsForCategory(String categoryName) {
        List<Product> products = new ArrayList<>();
        String fileName = null;
        switch (categoryName) {
            case "Fruits & Vegetables":
                fileName = "fruits.txt";
                break;
            case "Beverages":
                fileName = "beverages.txt";
                break;
            case "Breakfast":
                fileName = "breakfast.txt";
                break;
            case "Dairy":
                fileName = "dairy.txt";
                break;
            case "Meat & Fish":
                fileName = "meat.txt";
                break;
            case "Snacks":
                fileName = "snacks.txt";
                break;
            case "Frozen Foods":
                fileName = "frozen_foods.txt";
                break;
            case "Bakery":
                fileName = "bakery.txt";
                break;
            case "Household Supplies":
                fileName = "household_supplies.txt";
                break;
            case "Personal Care":
                fileName = "personal_care.txt";
                break;
            default:
                System.out.println("No file mapped for category: " + categoryName);
                return products;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(fileName)
        ))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String name = parts[0];
                    String description = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    String imageUrl = parts[3];
                    double discount = parts.length > 4 ? Double.parseDouble(parts[4]) : 0;
                    int quantity = parts.length > 5 ? Integer.parseInt(parts[5]) : 1;
                    products.add(new Product(name, description, price, imageUrl, discount, quantity));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading products from file: " + fileName);
            e.printStackTrace();
        }

        return products;
    }


}

