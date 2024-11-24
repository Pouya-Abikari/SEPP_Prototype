package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class HomeScreenController {

    @FXML
    private Button messageButton;

    @FXML
    private Button notificationButton;

    @FXML
    private ImageView tutorialImage;

    @FXML
    private ImageView halloweenImage;



    @FXML
    public void initialize() {
        // Load images for buttons
       // messageButton.setImage(new Image(getClass().getResourceAsStream("/messageButton.png")));
      //  notificationButton.setImage(new Image(getClass().getResourceAsStream("/notificationButton.png")));

        setupImages();
    }


    private void setupImages() {
        try {

            // Load and set the message button image
            Image messageImg = new Image(getClass().getResourceAsStream("/messageButton.png"));
            ImageView messageImgView = new ImageView(messageImg);
            messageImgView.setFitWidth(30);
            messageImgView.setFitHeight(30);
            messageButton.setGraphic(messageImgView);

            // Load and set the notification button image
            Image notificationImg = new Image(getClass().getResourceAsStream("/notificationButton.png"));
            ImageView notificationImgView = new ImageView(notificationImg);
            notificationImgView.setFitWidth(30);
            notificationImgView.setFitHeight(30);
            notificationButton.setGraphic(notificationImgView);


            /*
            Image messageImg = new Image(getClass().getResourceAsStream("/messageButton.png"));
            messageButton.setImage(messageImg);
            messageButton.setFitHeight(30.0);
            messageButton.setPreserveRatio(true);

            //load notification button image
            Image notificationImg = new Image(getClass().getResourceAsStream("/notificationButton.png"));
            notificationButton.setImage(notificationImg);
            notificationButton.setFitHeight(30.0);
            notificationButton.setPreserveRatio(true); */

            // Load the tutorial image
            Image tutorialImg = new Image(this.getClass().getResourceAsStream("/videoscreen.png"));
            tutorialImage.setImage(tutorialImg);
            tutorialImage.setFitWidth(300.0);
            tutorialImage.setPreserveRatio(true);

            // Load the Halloween promo image
            Image halloweenImg = new Image(this.getClass().getResourceAsStream("/merryxmas.png"));
            halloweenImage.setImage(halloweenImg);
            halloweenImage.setFitWidth(300.0);
            halloweenImage.setPreserveRatio(true);



        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
