package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;


import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private ImageView profileImage;
    @FXML
    private Label userNameLabel;

    @FXML
    private Button editButton;
    @FXML
    private ImageView editIcon;

    @FXML
    private VBox editFormContainer;
    @FXML
    private TextField editNameField; // Only editing the user's name now

    @FXML
    private StackPane sectionsStack;
    @FXML
    private VBox mainContentVBox;

    @FXML
    private VBox myAddressSection;
    @FXML
    private VBox myOrdersSection;
    @FXML
    private VBox myWishlistSection;
    @FXML
    private VBox supportSection;
    @FXML
    private VBox talkToSupportSection;
    @FXML
    private VBox mailToUsSection;
    @FXML
    private VBox logOutSection;

    @FXML
    private HBox mainHeader;
    @FXML
    private HBox subHeader;
    @FXML
    private Label subHeaderTitle;
    @FXML
    private Button backButton;

    @FXML
    private Button homeButton;
    @FXML
    private Button categoriesButton;
    @FXML
    private Button cartButton;
    @FXML
    private Button settingsButton;

    @FXML
    private Pane loadingOverlay;

    @FXML
    private ImageView myAddressIcon;
    @FXML
    private ImageView myOrdersIcon;
    @FXML
    private ImageView myWishlistIcon;
    @FXML
    private ImageView supportIcon;
    @FXML
    private ImageView talkSupportIcon;
    @FXML
    private ImageView mailIcon;
    @FXML
    private ImageView logOutIcon;

    private String id;  // This will hold the user's email (unique ID)
    private String[] currentUserData; // Store current user's data from users.txt

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProfileImage();
        setUserName("Loading..."); // Temporary until we get actual data after getID()

        loadIcons();
        hideAllSections();
        showMainView();
        setupBottomNavigation();
        setupBackButton();

        // Initially hide the edit form
        editFormContainer.setVisible(false);
        editFormContainer.setManaged(false);
    }

    private void loadProfileImage() {
        try {
            Image img = new Image(getClass().getResourceAsStream("/settings/pfp_icon.jpg"));
            profileImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
        }
    }

    private void loadIcons() {
        InputStream editIconStream = getClass().getResourceAsStream("/settings/settingsEdit_icon.png");
        if (editIconStream == null) {
            System.err.println("Could not find /settings/settingsEdit_icon.png");
        } else {
            editIcon.setImage(new Image(editIconStream));
        }

        myAddressIcon.setImage(new Image(getClass().getResourceAsStream("/settings/myAddress_icon.png")));
        myOrdersIcon.setImage(new Image(getClass().getResourceAsStream("/settings/myOrders_icon.png")));
        myWishlistIcon.setFitWidth(20);
        myWishlistIcon.setFitHeight(20);
        myWishlistIcon.setImage(new Image(getClass().getResourceAsStream("/settings/myWishlist_icon.png")));
        supportIcon.setImage(new Image(getClass().getResourceAsStream("/settings/support_icon.png")));
        talkSupportIcon.setImage(new Image(getClass().getResourceAsStream("/settings/talkSupport_icon.png")));
        mailIcon.setImage(new Image(getClass().getResourceAsStream("/settings/mail_icon.png")));
        logOutIcon.setImage(new Image(getClass().getResourceAsStream("/settings/signOut_icon.png")));
    }

    private void setUserName(String userName) {
        userNameLabel.setText(userName);
    }

    private void hideAllSections() {
        myAddressSection.setVisible(false); myAddressSection.setManaged(false);
        myOrdersSection.setVisible(false); myOrdersSection.setManaged(false);
        myWishlistSection.setVisible(false); myWishlistSection.setManaged(false);
        supportSection.setVisible(false); supportSection.setManaged(false);
        talkToSupportSection.setVisible(false); talkToSupportSection.setManaged(false);
        mailToUsSection.setVisible(false); mailToUsSection.setManaged(false);
        logOutSection.setVisible(false); logOutSection.setManaged(false);
    }

    private void showMainView() {
        mainContentVBox.setVisible(true);
        mainContentVBox.setManaged(true);

        sectionsStack.setVisible(false);
        sectionsStack.setManaged(false);

        mainHeader.setVisible(true);
        mainHeader.setManaged(true);
        subHeader.setVisible(false);
        subHeader.setManaged(false);
    }

    private void showSection(VBox section, String title) {
        mainContentVBox.setVisible(false);
        mainContentVBox.setManaged(false);

        // Hide edit form if open
        editFormContainer.setVisible(false);
        editFormContainer.setManaged(false);

        sectionsStack.setVisible(true);
        sectionsStack.setManaged(true);

        hideAllSections();
        section.setVisible(true);
        section.setManaged(true);

        mainHeader.setVisible(false);
        mainHeader.setManaged(false);

        subHeader.setVisible(true);
        subHeader.setManaged(true);
        subHeaderTitle.setText(title);
    }

    @FXML
    private void handleMyAddress() {
        System.out.println("My Address clicked");
        switchToLocationPage("location.fxml", "My Addresses", "Settings");
    }

    private void switchToLocationPage(String fxmlFile, String title, String previousPage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            LocationController locationController = loader.getController();
            locationController.setPreviousPage(previousPage);

            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyOrders() {
        System.out.println("My Orders clicked");
        showSection(myOrdersSection, "My Orders");
        for (javafx.scene.Node node : myOrdersSection.getChildren()) {
            if (node instanceof Label) {
                Label lbl = (Label) node;
                lbl.setWrapText(true);
                lbl.setMaxWidth(300);
            }
        }
    }

    @FXML
    private void handleMyWishlist() {
        System.out.println("My Wishlist clicked");
        showSection(myWishlistSection, "My Wishlist");
        myWishlistSection.getChildren().clear();
        Label wishlistLabel = new Label("You have not added anything to your Wishlist yet");
        wishlistLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #757575;");
        wishlistLabel.setWrapText(true);
        wishlistLabel.setMaxWidth(300);
        myWishlistSection.getChildren().add(wishlistLabel);
    }

    @FXML
    private void handleSupport() {
        System.out.println("Support clicked");
        showSection(supportSection, "Support");
        supportSection.getChildren().clear();
        Label supportLabel = new Label("Currently, no support tickets are available. Please check FAQs or contact us.");
        supportLabel.setWrapText(true);
        supportLabel.setMaxWidth(300);
        supportLabel.setStyle("-fx-font-size:16px; -fx-text-fill:#757575;");
        supportSection.getChildren().add(supportLabel);
    }

    @FXML
    private void handleTalkToSupport() {
        System.out.println("Talk to our Support clicked");
        showSection(talkToSupportSection, "Talk to our Support");
        talkToSupportSection.getChildren().clear();
        Label talkSupportLabel = new Label("You can talk directly to our support agents by calling 123-456-7890.");
        talkSupportLabel.setWrapText(true);
        talkSupportLabel.setMaxWidth(300);
        talkSupportLabel.setStyle("-fx-font-size:16px; -fx-text-fill:#757575;");
        talkToSupportSection.getChildren().add(talkSupportLabel);
    }

    @FXML
    private void handleMailToUs() {
        System.out.println("Mail to us clicked");
        showSection(mailToUsSection, "Mail to us");
        mailToUsSection.getChildren().clear();
        Label mailLabel = new Label("We value your feedback! Please drop us an email at support@example.com.");
        mailLabel.setWrapText(true);
        mailLabel.setMaxWidth(300);
        mailLabel.setStyle("-fx-font-size:16px; -fx-text-fill:#757575;");
        mailToUsSection.getChildren().add(mailLabel);
    }

    @FXML
    private void handleLogOut() {
        System.out.println("Log Out clicked");
        switchToPage("login.fxml", "Login");
    }

    @FXML
    private void handleEditButton() {
        // Show edit form with current values (only name now)
        editNameField.setText(userNameLabel.getText());

        sectionsStack.setVisible(false);
        sectionsStack.setManaged(false);

        mainContentVBox.setVisible(true);
        mainContentVBox.setManaged(true);

        editFormContainer.setVisible(true);
        editFormContainer.setManaged(true);
    }

    @FXML
    private void saveEdits() {
        String newName = editNameField.getText().trim();
        if (!newName.isEmpty()) {
            setUserName(newName);
            // Save updated name back to users.txt
            if (id != null && !id.isEmpty()) {
                saveUserNameToFile(id, newName);
            }
        }

        editFormContainer.setVisible(false);
        editFormContainer.setManaged(false);
    }

    @FXML
    private void cancelEdit() {
        editFormContainer.setVisible(false);
        editFormContainer.setManaged(false);
    }

    private void setupBackButton() {
        backButton.setOnAction(event -> {
            showMainView();
        });
    }

    private void setupBottomNavigation() {
        try {
            Image homeImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/homePageButton.png"));
            ImageView homeImgView = new ImageView(homeImg);
            homeImgView.setFitWidth(30);
            homeImgView.setFitHeight(30);
            homeButton.setGraphic(homeImgView);

            Image categoriesImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/menuPageButton.png"));
            ImageView categoriesImgView = new ImageView(categoriesImg);
            categoriesImgView.setFitWidth(35);
            categoriesImgView.setFitHeight(35);
            categoriesButton.setGraphic(categoriesImgView);

            Image cartImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/cartPageButton.png"));
            ImageView cartImgView = new ImageView(cartImg);
            cartImgView.setFitWidth(20);
            cartImgView.setFitHeight(20);
            cartButton.setGraphic(cartImgView);

            Image settingsImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/settingPageButtonClicked.png"));
            ImageView settingsImgView = new ImageView(settingsImg);
            settingsImgView.setFitWidth(35);
            settingsImgView.setFitHeight(35);
            settingsButton.setGraphic(settingsImgView);

        } catch (Exception e) {
            System.err.println("Error loading bottom navigation images: " + e.getMessage());
        }

        homeButton.setOnAction(event -> switchToPage("home_screen.fxml", "Home"));
        categoriesButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        cartButton.setOnAction(event -> switchToPage("cart.fxml", "Cart"));
    }

    private void switchToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            // If you have controllers that need ID, pass it here as well
            // e.g., if home_screen or cart requires ID, call getID(id) on them as done before

            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called from other controllers after loading this page to pass the user ID (email).
     */
    public void getID(String id) {
        this.id = id;
        System.out.println("ID: " + id);
        loadUserDataByEmail(id);
    }

    private void loadUserDataByEmail(String email) {
        // Load from users.txt
        File file = new File("src/main/resources/users.txt");
        if (!file.exists()) {
            System.err.println("users.txt not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Format: Name,Email,Password,"Addresses","CurrentAddress","OrderIDs",CurrentOrderID,ErrorCase
                // We split by comma but must be careful with quoted fields
                // Let's do a simple parse assuming no commas inside Name and Email
                // Name can have spaces but not commas, email no commas, password no commas
                // The tricky part: addresses are in quotes and may contain commas.

                String[] parts = parseUserLine(line);
                if (parts.length >= 8) {
                    String userEmail = parts[1].trim();
                    if (userEmail.equalsIgnoreCase(email)) {
                        currentUserData = parts;
                        // parts[0] = Name
                        // Set the name
                        setUserName(parts[0].trim());
                        return;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the user line handling quoted fields. We'll assume a format:
     * Name,Email,Password,"Address;Address2","CurrentAddress","OrderIDs",CurrentOrderID,ErrorCase
     * We can split by comma only at top-level.
     */
    private String[] parseUserLine(String line) {
        // We'll split by commas not inside quotes. A simple approach:
        // Using a small parser:
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"' && (sb.length() == 0 || sb.charAt(sb.length()-1) != '\\')) {
                inQuotes = !inQuotes;
                sb.append(c);
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString().trim());

        // fields might have quotes. We'll leave them as is if needed.
        return fields.toArray(new String[0]);
    }

    private void saveUserNameToFile(String email, String newName) {
        File file = new File("src/main/resources/users.txt");
        if (!file.exists()) {
            System.err.println("users.txt not found. Cannot save.");
            return;
        }

        List<String[]> users = new ArrayList<>();
        // Read all users
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseUserLine(line);
                if (parts.length >= 8) {
                    if (parts[1].trim().equalsIgnoreCase(email)) {
                        // Update name
                        parts[0] = newName;
                    }
                    users.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write all back
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String[] user : users) {
                // Reconstruct line
                // We must ensure we preserve quotes for addresses fields
                // user format: Name,Email,Password,"Addresses","CurrentAddress","OrderIDs",CurrentOrderID,ErrorCase
                // Let's trust original format and re-join them.
                StringBuilder sb = new StringBuilder();
                for (int i=0; i<user.length; i++) {
                    sb.append(user[i]);
                    if (i < user.length-1) sb.append(",");
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("User name updated in users.txt for " + email);
    }
}