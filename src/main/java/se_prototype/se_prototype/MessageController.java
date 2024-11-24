package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class MessageController {

    @FXML
    private Button homeButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button cartButton;
    @FXML
    private Button settingsButton;


    @FXML
    private Button backButton;

    @FXML
    private Button sendButton;

    @FXML
    private TextField messageField;

    @FXML
    private VBox chatContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {
        setUpImages();

        // Set alignment of chatContainer
        chatContainer.setStyle("-fx-alignment: bottom-left;");
        chatContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            // Automatically scroll to the bottom when new messages are added
            scrollPane.setVvalue(1.0);
        });

        // Send button action
        sendButton.setOnAction(event -> sendMessage());

        // Set the back button action
        backButton.setOnAction(event -> goBack());



        // Set up button actions
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        cartButton.setOnAction(event -> switchToPage("cart.fxml", "Cart"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings"));
    }

    private void sendMessage() {
        String userMessage = messageField.getText().trim();

        if (!userMessage.isEmpty()) {
            // Add user's message to chat container
            addMessage(userMessage, "user");

            // Automated reply
            String botReply = getAutomatedReply(userMessage);
            addMessage(botReply, "bot");

            // Clear the message field
            messageField.clear();
        }
    }

    private void addMessage(String message, String sender) {
        HBox messageBox = new HBox();
        messageBox.setSpacing(10);

        TextFlow textFlow = new TextFlow(new Text(message));
        textFlow.setStyle("-fx-padding: 10; -fx-background-radius: 15; -fx-font-size: 14;");

        if (sender.equals("user")) {
            textFlow.setStyle(textFlow.getStyle() + " -fx-background-color: #5EC401; -fx-text-fill: white;");
            messageBox.getChildren().addAll(textFlow);
            messageBox.setStyle("-fx-alignment: BASELINE_RIGHT;");
        } else {
            textFlow.setStyle(textFlow.getStyle() + " -fx-background-color: #3498DB; -fx-text-fill: white;");
            messageBox.getChildren().addAll(textFlow);
            messageBox.setStyle("-fx-alignment: BASELINE_LEFT;");
        }

        chatContainer.getChildren().add(messageBox);

    }

    private String getAutomatedReply(String userMessage) {
        return "You said: " + userMessage; // Simple echo reply. Customize as needed.
    }

    private void setUpImages(){

        // Back arrow image
        Image backImg = new Image(getClass().getResourceAsStream("/backArrow.png"));
        ImageView backImgView = new ImageView(backImg);
        backImgView.setFitWidth(30);
        backImgView.setFitHeight(30);
        backButton.setGraphic(backImgView);

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

    }

    private void switchToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) homeButton.getScene().getWindow(); // Get the current stage
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home_screen.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) backButton.getScene().getWindow(); // Get the current stage
            stage.setScene(scene);
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load home.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
