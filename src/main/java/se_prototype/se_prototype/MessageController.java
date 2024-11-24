package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MessageController {

    @FXML
    private Button backButton;

    @FXML
    private Button sendButton;

    @FXML
    private TextField messageField;

    @FXML
    private VBox chatContainer;

    @FXML
    public void initialize() {
        // Event for send button
        sendButton.setOnAction(event -> sendMessage());
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
}
