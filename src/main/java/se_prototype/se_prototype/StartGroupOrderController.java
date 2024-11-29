package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class StartGroupOrderController {

    @FXML
    private Button startGroupOrderButton;

    @FXML
    private Button backButton;

    @FXML
    private ImageView timerIcon;

    @FXML
    private ImageView peopleIcon;

    @FXML
    private ImageView notificationIcon;

    @FXML
    private ImageView cartIcon;

    @FXML
    private ImageView itemsIcon;

    @FXML
    public void initialize() {
        // Set images for the icons
        timerIcon.setImage(new Image(getClass().getResourceAsStream("/icons/timer.png")));
        peopleIcon.setImage(new Image(getClass().getResourceAsStream("/icons/people.png")));
        notificationIcon.setImage(new Image(getClass().getResourceAsStream("/icons/notification.png")));
        cartIcon.setImage(new Image(getClass().getResourceAsStream("/icons/cart.png")));
        itemsIcon.setImage(new Image(getClass().getResourceAsStream("/icons/items.png")));

        startGroupOrderButton.setOnAction(e -> switchToPage("groupOrder.fxml", "Group Order"));
        backButton.setOnAction(e -> switchToPage("cart.fxml", "Cart"));
    }

    private void switchToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) backButton.getScene().getWindow(); // Get the current stage
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}