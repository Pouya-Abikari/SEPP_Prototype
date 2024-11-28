package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StartGroupOrderController {

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
    }
}