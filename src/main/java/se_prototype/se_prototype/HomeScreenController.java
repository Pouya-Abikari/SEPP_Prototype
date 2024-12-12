package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HomeScreenController {

    @FXML
    private Button messageButton;

    @FXML
    private Button notificationButton;

    @FXML
    private ImageView mainPageImage;

    @FXML
    private ImageView tutorialImage;

    @FXML
    private ImageView  christmasImage;

    @FXML
    private ImageView moviePizzaImage;

    @FXML
    private ImageView  errorImage;

    @FXML
    private Button homeButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button cartButton;
    @FXML
    private Button settingsButton;

    @FXML
    private Label nameLabel;

    private String id;

    @FXML
    public void initialize() {

        setupImages();
        loadCurrentUserName();

        // Set up button actions
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        cartButton.setOnAction(event -> switchToPage("cart.fxml", "Cart"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings"));
        notificationButton.setOnAction(event -> switchToPage("notifications.fxml", "Notifications"));
    }

    private void loadCurrentUserName() {
        // Path to the file storing the current user's information
        String currentUserFile = "src/main/resources/current_user.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(currentUserFile))) {
            String userInfo = reader.readLine(); // Read the first line of the file
            if (userInfo != null && !userInfo.trim().isEmpty()) {
                // Split the userInfo by commas to extract the first name
                String[] userDetails = userInfo.split(",");
                if (userDetails.length > 0) {
                    String fullName = userDetails[0].trim(); // Extract and trim the full name
                    String firstName = extractFirstWord(fullName); // Get the first word of the full name
                    nameLabel.setText(firstName); // Set the first name to the label
                } else {
                    nameLabel.setText("User"); // Fallback to default
                }
            } else {
                nameLabel.setText("User"); // Fallback in case of empty or null file
            }
        } catch (IOException e) {
            System.err.println("Error reading current user file: " + e.getMessage());
            nameLabel.setText("User"); // Fallback in case of error
        }
    }

    private String extractFirstWord(String text) {
        // Split the text by spaces and return the first word
        String[] words = text.split("\\s+"); // Split by whitespace
        return words.length > 0 ? words[0] : text; // Return the first word or the original text if no spaces
    }

    private void setupImages() {
        try {
            // Load and set the message button image
            Image messageImg = new Image(getClass().getResourceAsStream("/messageButton.png"));
            ImageView messageImgView = new ImageView(messageImg);
            messageImgView.setFitWidth(33);
            messageImgView.setFitHeight(33);
            messageButton.setGraphic(messageImgView);
            messageButton.setOnMouseClicked(event -> switchToPage("message.fxml", "Message"));

            // Load and set the notification button image
            Image notificationImg = new Image(getClass().getResourceAsStream("/notificationButton.png"));
            ImageView notificationImgView = new ImageView(notificationImg);
            notificationImgView.setFitWidth(30);
            notificationImgView.setFitHeight(30);
            notificationButton.setGraphic(notificationImgView);
            notificationButton.setOnMouseClicked(event -> switchToPage("notifications.fxml", "Notifications"));

            // Load the main page illustration
            Image mainImg = new Image(this.getClass().getResourceAsStream("/illustrationMainPageV2.png"));
            mainPageImage.setImage(mainImg);
            mainPageImage.setFitWidth(300.0);
            mainPageImage.setPreserveRatio(true);


            // Load the tutorial image
            Image tutorialImg = new Image(this.getClass().getResourceAsStream("/tutorialNew.png"));
            tutorialImage.setImage(tutorialImg);
            tutorialImage.setFitWidth(300.0);
            tutorialImage.setPreserveRatio(true);

            // Load the xmas promo image
            Image xmasImg = new Image(this.getClass().getResourceAsStream("/xmas.png"));
            christmasImage.setImage(xmasImg);
            christmasImage.setFitWidth(300.0);
            christmasImage.setPreserveRatio(true);

            // Load the movie pizza image
            Image pizzaImg = new Image(this.getClass().getResourceAsStream("/pizzaNight.png"));
            moviePizzaImage.setImage(pizzaImg);
            moviePizzaImage.setFitWidth(300.0);
            moviePizzaImage.setPreserveRatio(true);

            // Load the error pizza image
            Image errorImg = new Image(this.getClass().getResourceAsStream("/siteUnavailable.png"));
            errorImage.setImage(errorImg);
            errorImage.setFitWidth(300.0);
            errorImage.setPreserveRatio(true);

            // home page button
            Image homeImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/homePageButtonClicked.png"));
            ImageView homeImgView = new ImageView(homeImg);
            homeImgView.setFitWidth(30);
            homeImgView.setFitHeight(30);
            homeButton.setGraphic(homeImgView);

            // Menu Button
            Image menuImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/menuPageButton.png"));
            ImageView menuImgView = new ImageView(menuImg);
            menuImgView.setFitWidth(35);
            menuImgView.setFitHeight(35);
            menuButton.setGraphic(menuImgView);

            // Cart Button
            Image cartImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/cartPageButton.png"));
            ImageView cartImgView = new ImageView(cartImg);
            cartImgView.setFitWidth(20);
            cartImgView.setFitHeight(20);
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

    public void getID(String id) {
        this.id = id;
        System.out.println("ID: " + id);
    }

    private void switchToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 400, 711);
            switch (fxmlFile) {
                case "menu.fxml":
                    MenuController menuController = loader.getController();
                    menuController.getID(id);
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
                case "notifications.fxml":
                    NotificationsController notificationsController = loader.getController();
                    notificationsController.getID(id);
                    break;
                case "message.fxml":
                    MessageController messageController = loader.getController();
                    messageController.getID(id);
                    break;
            }
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) homeButton.getScene().getWindow(); // Get the current stage
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
