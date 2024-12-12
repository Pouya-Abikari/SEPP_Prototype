package se_prototype.se_prototype;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.*;
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
    private String id;

    private final Map<String, String> predefinedReplies = new HashMap<>();
    private List<String> participants;
    private Map<String, String> participantColors;
    private Map<String, List<String>> predefinedRepliesWithVariations;
    private final String MESSAGE_FILE = "src/main/resources/messages.txt";
    private String currentUser;

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
        currentUser = getCurrentUser();
        initializeParticipants();
        initializeParticipantColors();
        initializePredefinedReplies();
        setUpImages();

        if (currentUser != null) {
            loadMessages();
        }

        HBox.setHgrow(messageField, Priority.ALWAYS);

        // Apply inline styles for rounded corners and padding
        messageField.setStyle(
                "-fx-background-radius: 15; " +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 10; " +
                        "-fx-border-color: #CCCCCC;"
        );

        sendButton.setStyle(
                "-fx-background-radius: 15; " +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 10; " +
                        "-fx-background-color: #5ec401; " +
                        "-fx-text-fill: white;"
        );


        // Set alignment of chatContainer
        chatContainer.setStyle("-fx-padding: 10 10 10 10; -fx-alignment: bottom-left;");
        chatContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0); // Automatically scroll to the bottom when new messages are added
        });

        // Send button action
        sendButton.setOnAction(event -> sendMessage());
        backButton.setOnAction(event -> goBack());

        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        cartButton.setOnAction(event -> switchToPage("cart.fxml", "Cart"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings"));
    }

    private void loadMessages() {
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("User: " + currentUser)) {
                    String[] parts = line.split("Messages: ");
                    if (parts.length > 1) {
                        String messages = parts[1];
                        List<String> messageList = parseMessages(messages);
                        for (String message : messageList) {
                            String[] messageParts = message.split("from: ");
                            if (messageParts.length == 2) {
                                addMessage(messageParts[0].trim(), messageParts[1].trim());
                            } else {
                                addMessage(message.trim(), "You");
                            }
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMessage(String sender, String message) {
        File file = new File(MESSAGE_FILE);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean userFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("User: " + currentUser)) {
                    userFound = true;
                    // Append the new message to this user's line
                    String updatedLine = line + ", " + message + " from: " + sender;
                    lines.add(updatedLine);
                } else {
                    lines.add(line);
                }
            }

            if (!userFound) {
                // Add a new entry if the user does not exist
                lines.add("User: " + currentUser + ", Messages: [" + message + " from: " + sender + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write all lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> parseMessages(String messages) {
        messages = messages.replace("[", "").replace("]", "");
        return Arrays.asList(messages.split(","));
    }

    private String getCurrentUser() {
        String currentUserFile = "src/main/resources/current_user.txt";
        File file = new File(currentUserFile);

        if (!file.exists()) {
            System.err.println("Current user file not found.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine(); // Assume the first line contains the username or email
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void initializePredefinedReplies() {
        predefinedRepliesWithVariations = new HashMap<>();
        predefinedRepliesWithVariations.put("hi".toLowerCase(), Arrays.asList("Hi!", "Hello!", "Hey there!"));
        predefinedRepliesWithVariations.put("hello".toLowerCase(), Arrays.asList("Hello!", "Hi!", "Greetings!"));
        predefinedRepliesWithVariations.put("how are you".toLowerCase(), Arrays.asList("I'm good, thank you!", "Doing great!", "I'm fine, you?"));
        predefinedRepliesWithVariations.put("what is your name?".toLowerCase(), Arrays.asList("I'm Cassie", "Your house mate here!", "Josh!"));
        predefinedRepliesWithVariations.put("bye".toLowerCase(), Arrays.asList("Goodbye! Have a nice day!", "See you later!", "Take care!"));
        predefinedRepliesWithVariations.put("should we start a shared group order?".toLowerCase(), Arrays.asList("Yes!", "For sure, lets do it!", "I will join", "Yes! Send the link."));
    }

    private void sendMessage() {
        String userMessage = messageField.getText().trim();

        if (!userMessage.isEmpty()) {
            addMessage(userMessage, "You");
            saveMessage("You", userMessage);

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
                        Platform.runLater(() -> {
                            addMessage(botReply, participant);
                            saveMessage(participant, botReply); // Save the bot's reply
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            if (userMessage.equalsIgnoreCase("should we start a shared group order?")) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1000 + (numberOfResponders * 500)); // Wait for all replies
                        Platform.runLater(() -> addClickableMessage("Click here to start group", "StartGroupOrderController", "start_group_order.fxml"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            // Clear the message field
            messageField.clear();
        }
    }

    private void addClickableMessage(String message, String controller, String fxmlFile) {
        // Create a container for the message and name
        VBox messageContainer = new VBox();
        messageContainer.setSpacing(0); // Space between message bubble and name

        // Label for the sender's name
        Label nameLabel = new Label("You");
        nameLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
        nameLabel.setPadding(new Insets(0, 0, 2, 0));

        // Text with a clickable hyperlink
        Text clickText = new Text("Click here");
        clickText.setStyle("-fx-font-size: 14; -fx-underline: true; -fx-text-fill: white;"); // Underlined green text
        clickText.setOnMouseClicked(event -> switchToPage(fxmlFile, "Start Group Order"));

        Text plainText = new Text(" to start group");
        plainText.setStyle("-fx-font-size: 14;");

        TextFlow textFlow = new TextFlow(clickText, plainText);
        textFlow.setStyle("-fx-padding: 10; -fx-background-radius: 15; -fx-background-color: #4FC3D0; -fx-text-fill: white;");
        textFlow.setMaxWidth(250); // Set maximum width for wrapping
        textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        textFlow.setMinWidth(Region.USE_PREF_SIZE);

        // Bind the wrapping width of the Text to ensure proper layout
        clickText.wrappingWidthProperty().bind(textFlow.widthProperty().subtract(10)); // Adjust for padding
        plainText.wrappingWidthProperty().bind(textFlow.widthProperty().subtract(10));

        // Add the name label and textFlow to the container
        messageContainer.getChildren().addAll(nameLabel, textFlow);

        // Align the container
        messageContainer.setAlignment(Pos.TOP_RIGHT);

        // Add to the chat container
        chatContainer.getChildren().add(messageContainer);

        // Ensure the scroll pane stays scrolled to the bottom
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }

    private void addMessage(String message, String sender) {
        // Create a container for the message and name
        VBox messageContainer = new VBox();
        messageContainer.setSpacing(0); // Space between message bubble and name

        Label nameLabel = new Label(sender);
        nameLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
        nameLabel.setPadding(new Insets(0, 0, 2, 0));

        Text text = new Text(message);
        text.setStyle("-fx-font-size: 14;");

        //double maxTextWidth = 250; // Maximum width of the message bubble
        //text.setWrappingWidth(maxTextWidth);

        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-padding: 10; -fx-background-radius: 15; -fx-font-size: 14;");

        double maxBubbleWidth = 250; // Maximum width for the text bubble
        textFlow.setMaxWidth(maxBubbleWidth);
        textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);

        text.wrappingWidthProperty().bind(textFlow.widthProperty().subtract(20)); // Account for padding


        // Calculate exact text width and adjust the max width
        //double textWidth = text.getBoundsInLocal().getWidth();
        //textFlow.setMaxWidth(textWidth + 30); // Add padding to the bubble


        // Set background color based on sender
        String color = participantColors.getOrDefault(sender, "#3498DB"); // Default blue for others
        if (sender.equals("You")) {
            textFlow.setStyle(textFlow.getStyle() + " -fx-background-color: #4FC3D0; -fx-text-fill: white;");
        } else {
            textFlow.setStyle(textFlow.getStyle() + " -fx-background-color: " + color + "; -fx-text-fill: white;");
        }

        messageContainer.getChildren().addAll(nameLabel, textFlow);

        // Adjust alignment based on sender
        if (sender.equals("You")) {
            messageContainer.setAlignment(Pos.TOP_RIGHT);
            VBox.setMargin(textFlow, new Insets(0, 0, 5, 0)); // Adjust margin for better spacing
        } else {
            messageContainer.setAlignment(Pos.TOP_LEFT);
            VBox.setMargin(textFlow, new Insets(0, 0, 5, 0));
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
        return "Sorry I didn't get that";
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
            switch (fxmlFile) {
                case "menu.fxml":
                    MenuController menuController = loader.getController();
                    menuController.getID(id);
                    break;
                case "cart.fxml":
                    CartController cartController = loader.getController();
                    cartController.getID(id);
                    break;
                case "settings.fxml":
                    //SettingsController settingsController = loader.getController();
                    //settingsController.getID(id);
                    break;
            }
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
            HomeScreenController homeScreenController = loader.getController();
            homeScreenController.getID(id);
            homeScreenController.initialize();
            Stage stage = (Stage) backButton.getScene().getWindow(); // Get the current stage
            stage.setScene(scene);
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load home.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getID(String id) {
        this.id = id;
        System.out.println("ID: " + id);
    }
}
