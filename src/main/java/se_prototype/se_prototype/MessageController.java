package se_prototype.se_prototype;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;

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

    private final Map<String, String> predefinedReplies = new HashMap<>();
    private List<String> participants;
    private Map<String, String> participantColors;
    private Map<String, List<String>> predefinedRepliesWithVariations;

    private void initializeParticipants() {
        participants = Arrays.asList("Katia", "Pouya", "Nazanin", "Amir");
    }

    private void initializeParticipantColors() {
        participantColors = new HashMap<>();
        participantColors.put("Katia", "#98DDC2");
        participantColors.put("Pouya", "#6084B5");
        participantColors.put("Nazanin", "#F3DEC0");
        participantColors.put("Amir", "#FBB03C");
    }

    @FXML
    public void initialize() {
        initializePredefinedReplies();
        initializeParticipants();
        initializeParticipantColors();
        setUpImages();

        // Set alignment of chatContainer
        chatContainer.setStyle("-fx-padding: 10 10 10 10; -fx-alignment: bottom-left;");
        chatContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0); // Automatically scroll to the bottom when new messages are added
        });

        // Send button action
        sendButton.setOnAction(event -> sendMessage());

        // Set the back button action
        backButton.setOnAction(event -> goBack());
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        cartButton.setOnAction(event -> switchToPage("cart.fxml", "Cart"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings"));
    }

    private void initializePredefinedReplies() {
        predefinedRepliesWithVariations = new HashMap<>();
        predefinedRepliesWithVariations.put("hi".toLowerCase(), Arrays.asList("Hi!", "Hello!", "Hey there!"));
        predefinedRepliesWithVariations.put("hello".toLowerCase(), Arrays.asList("Hello!", "Hi!", "Greetings!"));
        predefinedRepliesWithVariations.put("how are you".toLowerCase(), Arrays.asList("I'm good, thank you!", "Doing great!", "I'm fine, you?"));
        predefinedRepliesWithVariations.put("what is your name".toLowerCase(), Arrays.asList("I'm Cassie.", "Your friendly assistant.", "Cassie here!"));
        predefinedRepliesWithVariations.put("bye".toLowerCase(), Arrays.asList("Goodbye! Have a nice day!", "See you later!", "Take care!"));
    }

    private void sendMessage() {
        String userMessage = messageField.getText().trim();

        if (!userMessage.isEmpty()) {
            addMessage(userMessage, "You");

            Random random = new Random();
            int numberOfResponders = random.nextInt(participants.size()) + 1; // Randomly decide how many respond
            List<String> shuffledParticipants = new ArrayList<>(participants);
            Collections.shuffle(shuffledParticipants);

            for (int i = 0; i < numberOfResponders; i++) {
                int delayIndex = i; // Make a final variable to capture the current value of i
                String participant = shuffledParticipants.get(delayIndex);
                String botReply = getAutomatedReply(userMessage); // Generate a reply

                // Add a delay before each message
                new Thread(() -> {
                    try {
                        Thread.sleep(500 * (delayIndex + 1)); // 0.5-second delay between responses
                        Platform.runLater(() -> addMessage(botReply, participant));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            // Clear the message field
            messageField.clear();
        }
    }



    private void addMessage(String message, String sender) {
        // Create a container for the message and name
        VBox messageContainer = new VBox();
        messageContainer.setSpacing(12); // Space between message bubble and name

        // Create the message text and TextFlow
        Text text = new Text(message);
        text.setWrappingWidth(0); // Disable wrapping to calculate precise width
        text.setStyle("-fx-font-size: 14;");

        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-padding: 10; -fx-background-radius: 15; -fx-font-size: 14;");

        // Calculate exact text width and adjust the max width
        double textWidth = text.getBoundsInLocal().getWidth();
        textFlow.setMaxWidth(textWidth + 30); // Add padding to the bubble


        // Set background color based on sender
        String color = participantColors.getOrDefault(sender, "#3498DB"); // Default blue for others
        if (sender.equals("You")) {
            textFlow.setStyle(textFlow.getStyle() + " -fx-background-color: #4FC3D0; -fx-text-fill: white;");
        } else {
            textFlow.setStyle(textFlow.getStyle() + " -fx-background-color: " + color + "; -fx-text-fill: white;");
        }

        // Create a label for the sender's name
        Label nameLabel = new Label(sender);
        nameLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");

        // Add the message bubble and name label to the messageContainer
        messageContainer.getChildren().addAll(textFlow, nameLabel);

        // Align the messageContainer based on the sender
        if (sender.equals("You")) {
            messageContainer.setAlignment(Pos.BASELINE_RIGHT);
        } else {
            messageContainer.setAlignment(Pos.BASELINE_LEFT);
        }

        // Add the message container to the chat container
        chatContainer.getChildren().add(messageContainer);

        // Ensure the scroll pane stays scrolled to the bottom
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }

    private String getAutomatedReply(String userMessage) {
        String lowerCaseMessage = userMessage.toLowerCase(); // Convert to lowercase for case-insensitive matching
        List<String> possibleReplies = predefinedRepliesWithVariations.get(lowerCaseMessage);
        if (possibleReplies != null) {
            // Randomly pick one of the possible replies
            return possibleReplies.get(new Random().nextInt(possibleReplies.size()));
        }
        return "Sorry, didn't get that";
    }

    private void setUpImages(){

        // Back arrow image
        Image backImg = new Image(getClass().getResourceAsStream("/backArrow.png"));
        ImageView backImgView = new ImageView(backImg);
        backImgView.setFitWidth(20);
        backImgView.setFitHeight(20);
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
            Scene scene = new Scene(loader.load(), 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
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
