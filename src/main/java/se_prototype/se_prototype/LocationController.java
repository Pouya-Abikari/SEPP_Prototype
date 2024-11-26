package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationController {

    @FXML
    private VBox addressList;

    @FXML
    private Button addAddressButton;

    @FXML
    private Button backButton;

    @FXML
    private VBox addressFormContainer;

    @FXML
    private TextField titleField;

    @FXML
    private TextField detailsField;

    @FXML
    private Button saveAddressButton;

    @FXML
    public void initialize() {
        addressFormContainer.setVisible(false);
        addressFormContainer.setManaged(false);
        getAddresses().forEach(address -> addAddressNode(address[0], address[1]));
        backButton.setOnAction(event -> switchToPage("cart.fxml", "Cart"));
        addAddressButton.setOnAction(event -> toggleAddressForm());
        saveAddressButton.setOnAction(event -> saveNewAddress());
    }

    private void toggleAddressForm() {
        // Toggle the visibility of the form
        addressFormContainer.setVisible(!addressFormContainer.isVisible());
        addressFormContainer.setManaged(addressFormContainer.isVisible());
    }

    private void saveNewAddress() {
        String title = titleField.getText().trim();
        String details = detailsField.getText().trim();

        if (!title.isEmpty() && !details.isEmpty()) {
            // Add to the UI
            addAddressNode(title, details);

            // Add to the file
            List<String[]> addresses = getAddresses();
            addresses.add(new String[]{title, details});
            addAddress(addresses);

            // Clear the form fields
            titleField.clear();
            detailsField.clear();

            // Hide the form
            addressFormContainer.setVisible(false);
            addressFormContainer.setManaged(false);
        } else {
            System.out.println("Title or details cannot be empty.");
        }
    }

    private List<String[]> getAddresses() {
        List<String[]> addresses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/addresses.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line by commas
                String[] parts = line.split(",");
                if (parts.length > 1) {
                    // The first part is the title
                    String title = parts[0].trim();
                    // The rest is joined into a single string for details
                    String details = String.join(", ", Arrays.copyOfRange(parts, 1, parts.length)).trim();
                    addresses.add(new String[]{title, details});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    private void addAddress(List<String[]> addresses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/addresses.txt"))) {
            for (String[] address : addresses) {
                writer.write(String.join(",", address));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAddressNode(String title, String details) {
        // Create a container for the address
        HBox addressBox = new HBox(10);
        addressBox.setAlignment(Pos.CENTER_LEFT);
        addressBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        // Title and details
        VBox addressDetails = new VBox(2);
        addressDetails.setAlignment(Pos.CENTER_LEFT);

        Label addressTitle = new Label(title);
        addressTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37474F;");

        Label addressDescription = new Label(details);
        addressDescription.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");

        addressDetails.getChildren().addAll(addressTitle, addressDescription);

        // Create Edit Button inside a Circle
        Button editButton = new Button();
        editButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        StackPane editButtonContainer = new StackPane();

        Circle editCircle = new Circle(15, Color.web("#FFD980")); // Circle with light yellow
        SVGPath editIcon = new SVGPath();
        editIcon.setContent("M17.1272 5.76208C17.4463 5.44299 17.4463 4.92753 17.1272 4.60844L15.2127 2.6939C15.0598 2.54069 14.8523 2.45459 14.6358 2.45459C14.4194 2.45459 14.2119 2.54069 14.059 2.6939L12.5618 4.19117L15.6299 7.25935L17.1272 5.76208ZM2.63721 14.2875V16.7748C2.63721 17.0039 2.81721 17.1839 3.0463 17.1839H5.53357C5.63994 17.1839 5.7463 17.143 5.81994 17.0612L14.7545 8.13481L11.6863 5.06662L2.75993 13.993C2.67812 14.0748 2.63721 14.173 2.63721 14.2875ZM12.439 8.13277L11.6863 7.38005L4.27357 14.7928V15.5455H5.0263L12.439 8.13277Z");
        editIcon.setScaleX(1);
        editIcon.setScaleY(1);
        editIcon.setFill(Color.WHITE);

        editButtonContainer.getChildren().addAll(editCircle, editIcon);
        editButton.setGraphic(editButtonContainer);

        // Create Delete Button inside a Circle
        Button deleteButton = new Button();
        deleteButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        StackPane deleteButtonContainer = new StackPane();

        Circle deleteCircle = new Circle(15, Color.web("#FF8080")); // Circle with light red
        SVGPath deleteIcon = new SVGPath();
        deleteIcon.setContent("M12.2836 2.69186L12.8645 3.27277H14.9099C15.3599 3.27277 15.7281 3.64095 15.7281 4.09095C15.7281 4.54095 15.3599 4.90914 14.9099 4.90914H5.09174C4.64174 4.90914 4.27356 4.54095 4.27356 4.09095C4.27356 3.64095 4.64174 3.27277 5.09174 3.27277H7.1372L7.71811 2.69186C7.86538 2.54459 8.07811 2.45459 8.29083 2.45459H11.7108C11.9236 2.45459 12.1363 2.54459 12.2836 2.69186ZM5.09174 15.5455C5.09174 16.4455 5.82811 17.1819 6.72811 17.1819H13.2736C14.1736 17.1819 14.9099 16.4455 14.9099 15.5455V7.36368C14.9099 6.46368 14.1736 5.72732 13.2736 5.72732H6.72811C5.82811 5.72732 5.09174 6.46368 5.09174 7.36368V15.5455ZM7.54629 7.36368H12.4554C12.9054 7.36368 13.2736 7.73186 13.2736 8.18186V14.7273C13.2736 15.1773 12.9054 15.5455 12.4554 15.5455H7.54629C7.09629 15.5455 6.72811 15.1773 6.72811 14.7273V8.18186C6.72811 7.73186 7.09629 7.36368 7.54629 7.36368Z");
        deleteIcon.setScaleX(1);
        deleteIcon.setScaleY(1);
        deleteIcon.setFill(Color.WHITE);

        deleteButtonContainer.getChildren().addAll(deleteCircle, deleteIcon);
        deleteButton.setGraphic(deleteButtonContainer);

        deleteButton.setOnAction(e -> {
            addressList.getChildren().remove(addressBox);

            deleteAddressFromFile(title, details);
        });

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add all components to the addressBox
        addressBox.getChildren().addAll(addressDetails, spacer, editButton, deleteButton);

        // Add the addressBox to the list
        addressList.getChildren().add(addressBox);
    }

    private void deleteAddressFromFile(String title, String details) {
        List<String[]> addresses = getAddresses();

        // Remove the address matching the title and details
        addresses.removeIf(address -> address[0].equals(title) && address[1].equals(details));

        // Rewrite the updated list back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/addresses.txt"))) {
            for (String[] address : addresses) {
                writer.write(address[0] + "," + address[1]);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addNewAddress() {
        // Open a new form or dialog to add a new address

    }

    private void switchToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 400, 711);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
