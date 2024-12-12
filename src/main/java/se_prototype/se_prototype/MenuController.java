package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

import java.io.*;
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
    private Button homeButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button cartButton;
    @FXML
    private Button settingsButton;
    private String id;


    @FXML
    private HBox locationContainer;
    private List<Product> originalProducts;
    private List<Product> allProducts;

    public void initialize() {
        StackPane svgIcon = createSvgIcon();
        locationContainer.getChildren().add(0, svgIcon);
        locationContainer.setOnMouseClicked(this::onLocationBoxClick);
        List<Product> products = List.of(
                new Product("Fruits & Vegetables", "Fresh fruits and veggies", 5.0, "Fruits&Vegetables/mango.png", 0 , 1),
                new Product("Breakfast", "Start your day right", 10.0, "breakfast.png", 0, 1),
                new Product("Beverages", "Quench your thirst", 3.0, "beverages.png", 0,1),
                new Product("Meat & Fish", "Freshly sourced", 15.0, "meat.png",0,1),
                new Product("Snacks", "Delicious treats", 2.0, "snacks.png",0,1),
                new Product("Dairy", "Milk and dairy products", 4.0, "milk.png",0,1),
                new Product("Frozen Foods", "Quick and convenient meals", 7.0, "frozen_foods.png",0,1),
                new Product("Bakery", "Freshly baked goods", 6.0, "bakery.png",0,1),
                new Product("Household Supplies", "Everyday essentials", 12.0, "household_supplies.png",0,1),
                new Product("Personal Care", "Care for yourself", 8.0, "personal_care.png",0,1)
        );
        this.originalProducts = products;
        this.allProducts = fetchAllProducts();

        populateCategories(products);

        setupImages();

        homeButton.setOnAction(event -> switchToPage("home_screen.fxml", "Home" , null));
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu", "menu.fxml"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings", "settings.fxml"));
        cartButton.setOnAction(event -> switchToPage("cart.fxml", "Cart", "cart.fxml"));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterProducts(newValue));

    }

    private List<Product> fetchAllProducts() {
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/search.txt"))) {
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
        } catch (IOException e) {
            System.err.println("Error reading product file: " + e.getMessage());
        }

        return products;
    }


    @FXML
    private void filterProducts(String query) {
        menuGrid.getChildren().clear();

        if (query == null || query.isEmpty()) {
            populateCategories(originalProducts);
            return;
        }

        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().startsWith(query.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        int column = 0;
        int row = 0;
        for (Product product : filteredProducts) {
            VBox productCard = createProductCard(product);
            menuGrid.add(productCard, column, row);
            column++;
            if (column == 2) {
                column = 0;
                row++;
            }
        }
        if (filteredProducts.isEmpty()) {
            Label noResultsLabel = new Label("No results found.");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            menuGrid.add(noResultsLabel, 0, 0);
        }
    }
    private void populateCategories(List<Product> categories) {
        menuGrid.getChildren().clear();

        int column = 0;
        int row = 0;

        for (Product category : categories) {
            VBox categoryCard = createCategoryCard(category);
            categoryCard.setOnMouseClicked(event -> navigateToCategory(category.getName()));
            menuGrid.add(categoryCard, column, row);
            column++;
            if (column == 2) {
                column = 0;
                row++;
            }
        }
        menuGrid.setAlignment(Pos.CENTER);
    }


    private VBox createCategoryCard(Product product) {
        VBox categoryCard = new VBox();
        categoryCard.setSpacing(2);
        categoryCard.setAlignment(Pos.CENTER); // Center alignment
        categoryCard.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #D9D9D9; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        categoryCard.setPrefWidth(140);
        categoryCard.setPrefHeight(140);
        categoryCard.setMaxWidth(140);
        categoryCard.setMaxHeight(140);

        ImageView productImage = new ImageView(new Image(product.getImageUrl()));
        productImage.setFitHeight(80);
        productImage.setFitWidth(80);
        productImage.setPreserveRatio(true);

        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #37474F;");
        productName.setWrapText(true);
        productName.setAlignment(Pos.CENTER);

        categoryCard.getChildren().addAll(productImage, productName);

        return categoryCard;
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
        // Read the cart file
        List<String[]> cartItems = readCartFile();
        boolean userExists = false;

        // Iterate through existing cart data
        for (int i = 0; i < cartItems.size(); i++) {
            String[] cartEntry = cartItems.get(i);
            String email = cartEntry[0]; // User's email is the first element
            String items = cartEntry.length > 1 ? cartEntry[1] : ""; // Products are the second element

            if (email.equals(id)) {
                userExists = true; // User exists
                List<String> updatedProducts = new ArrayList<>();
                boolean productExists = false;

                // Process the user's existing products
                for (String item : items.split(";")) {
                    if (item.isEmpty()) continue; // Skip empty items
                    String[] itemDetails = item.split(",");
                    if (itemDetails[0].equals(product.getName())) {
                        // Update quantity if product exists
                        int currentQuantity = Integer.parseInt(itemDetails[5]);
                        itemDetails[5] = String.valueOf(currentQuantity + 1);
                        productExists = true;
                    }
                    updatedProducts.add(String.join(",", itemDetails));
                }

                // If the product doesn't exist, add it
                if (!productExists) {
                    updatedProducts.add(formatProduct(product));
                }

                // Update the user's cart entry
                cartItems.set(i, new String[]{email, String.join(";", updatedProducts)});
                break;
            }
        }

        // If the user doesn't already exist in the cart file, add them
        if (!userExists) {
            cartItems.add(new String[]{id, formatProduct(product)});
        }

        // Write updated cart data back to the file
        writeCartFile(cartItems);

        System.out.println("Added to cart: " + product.getName());
    }
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


    private List<String[]> readCartFile() {
        List<String[]> cartItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/cart.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    cartItems.add(new String[]{parts[0], ""}); // Add email with no products
                } else {
                    String items = parts[1].endsWith(";") ? parts[1].substring(0, parts[1].length() - 1) : parts[1];
                    cartItems.add(new String[]{parts[0], items});
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading cart file: " + e.getMessage());
        }
        return cartItems;
    }

    private void writeCartFile(List<String[]> cartItems) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/cart.txt"))) {
            for (String[] cartItem : cartItems) {
                String email = cartItem[0];
                String items = cartItem.length > 1 ? cartItem[1] : "";
                String line = email + ";" + items.trim();
                if (!line.endsWith(";")) {
                    line += ";"; // Ensure a semicolon at the end
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to cart file: " + e.getMessage());
        }
    }

    private void setupImages() {
        try {
            // home page button
            Image homeImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/homePageButton.png"));
            ImageView homeImgView = new ImageView(homeImg);
            homeImgView.setFitWidth(30);
            homeImgView.setFitHeight(30);
            homeButton.setGraphic(homeImgView);

            // Menu Button
            Image menuImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/menuPageButtonClicked.png"));
            ImageView menuImgView = new ImageView(menuImg);
            menuImgView.setFitWidth(35);
            menuImgView.setFitHeight(35);
            menuButton.setGraphic(menuImgView);

            // Cart Button
            Image cartImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/cartPageButton.png"));
            ImageView cartImgView = new ImageView(cartImg);
            cartImgView.setFitWidth(25);
            cartImgView.setFitHeight(25);
            cartButton.setGraphic(cartImgView);

            // Settings Button
            Image settingsImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/settingPageButton.png"));
            ImageView settingsImgView = new ImageView(settingsImg);
            settingsImgView.setFitWidth(35);
            settingsImgView.setFitHeight(35);
            settingsButton.setGraphic(settingsImgView);



        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchToPage(String fxmlFile, String title, String previousPage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            switch (fxmlFile) {
                case "home_screen.fxml":
                    HomeScreenController homeScreenController = loader.getController();
                    homeScreenController.getID(id);
                    homeScreenController.initialize();
                    break;
                case "cart.fxml":
                    CartController cartController = loader.getController();
                    cartController.getID(id);
                    cartController.initialize();
                    break;
                case "settings.fxml":
                    SettingsController settingsController = loader.getController();
                    settingsController.getID(id);
                    break;
                case "category.fxml":
                    CategoryController categoryController = loader.getController();
                    categoryController.getID(id);
                    break;
                case "location.fxml":
                    LocationController locationController = loader.getController();
                    locationController.setPreviousPage(previousPage);
                    locationController.getID(id);
                    break;
            }
            Stage stage = (Stage) homeButton.getScene().getWindow(); // Get the current stage
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
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

    public void getID(String id) {
        this.id = id;
        System.out.println("ID: " + id);
    }

    @FXML
    private void onLocationBoxClick(MouseEvent event) {
        switchToPage("location.fxml", "Location", "menu.fxml");    }

    private void navigateToCategory(String categoryName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("category.fxml"));
            Parent categoryPage = loader.load();
            CategoryController controller = loader.getController();
            controller.getID(id);
            List<Product> products = fetchProductsForCategory(categoryName); // Fetch products for this category
            controller.initializeCategory(categoryName, products, this);
            Scene scene = new Scene(categoryPage, 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) menuGrid.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load category.fxml");
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