package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class SettingsController {

    @FXML
    private Button myAddressButton;

    @FXML
    private Button myOrdersButton;

    @FXML
    private Button myWishlistButton;

    @FXML
    private VBox myAddressSection;

    @FXML
    private VBox myOrdersSection;

    @FXML
    private VBox myWishlistSection;

    @FXML
    public void initialize() {
        // Initialize the visibility of each section
        hideAllSections();
    }

    @FXML
    private void handleMyAddressButton() {
        hideAllSections();
        myAddressSection.setVisible(true);
        myAddressSection.setManaged(true);
    }

    @FXML
    private void handleMyOrdersButton() {
        hideAllSections();
        myOrdersSection.setVisible(true);
        myOrdersSection.setManaged(true);
    }

    @FXML
    private void handleMyWishlistButton() {
        hideAllSections();
        myWishlistSection.setVisible(true);
        myWishlistSection.setManaged(true);
    }

    // Utility method to hide all sections
    private void hideAllSections() {
        myAddressSection.setVisible(false);
        myAddressSection.setManaged(false);
        myOrdersSection.setVisible(false);
        myOrdersSection.setManaged(false);
        myWishlistSection.setVisible(false);
        myWishlistSection.setManaged(false);
    }

    private void setupImages() {
        try {
            // Set images for the buttons
            myAddressButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/address.png"))));
            myOrdersButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/orders.png"))));
            myWishlistButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/wishlist.png"))));

        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }
}