package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HomeScreenController {
    @FXML
    private ImageView tutorialImage;

    @FXML
    private ImageView halloweenImage;

    @FXML
    public void initialize() {
        setupImages();
    }

    private void setupImages() {
        try {
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
