package se_prototype.se_prototype;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.management.Notification;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class NotificationsController {
    @FXML
    private Button backButton;
    @FXML
    private VBox notificationContainer;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button homeButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button cartButton;
    @FXML
    private Button settingsButton;

    @FXML
    public void initialize() {
        setUpImages();
        populateNotifications();

        backButton.setOnAction(event -> goBack());
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        cartButton.setOnAction(event -> switchToPage("cart.fxml", "Cart"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings"));

    }

    private void setUpImages() {
        // Back arrow image
        Image backImg = new Image(getClass().getResourceAsStream("/backArrow.png"));
        ImageView backImgView = new ImageView(backImg);
        backImgView.setFitWidth(20);
        backImgView.setFitHeight(20);
        backButton.setGraphic(backImgView);
        backButton.setOnAction(event -> goBack());

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

    private void populateNotifications() {
        // Predefined notifications
        List<Notification> notifications = Arrays.asList(
                new Notification("Order #315", "Your Order is Confirmed. Please check everything is okay.", "11:01 AM", "/notificationSymbols/tickIcon.png", LocalDate.now().minusDays(2)),
                new Notification("Order #315", "Your Order is our for delivery. Please contact the driver for more details. ", "11:37 AM", "/notificationSymbols/PhoneIconV2.png", LocalDate.now().minusDays(2)),
                new Notification("Order #315", "Your Order had been delivered. Please rate the experience.", "11:59 AM", "/notificationSymbols/starIcon.png", LocalDate.now().minusDays(2)),

                new Notification("Order #395", "Your Order is confirmed. Please check everything is okay.", "3:57 PM", "/notificationSymbols/tickIcon.png", LocalDate.now().minusDays(1)),
                new Notification("Order #395", "Your Order is our for delivery. Please contact the driver for more details. ", "4:25 PM", "/notificationSymbols/PhoneIconV2.png", LocalDate.now().minusDays(1)),
                new Notification("Order #395", "Your Order had been delivered. Please rate the experience.", "4:43 PM", "/notificationSymbols/starIcon.png", LocalDate.now().minusDays(1)),

                new Notification("Order #421", "Your Order is confirmed. Please check everything is okay.", "3:57 PM", "/notificationSymbols/tickIcon.png", LocalDate.now())
        );

        new Thread(() -> {
            for (Notification notification : notifications) {
                // Format the date for the notification
                String formattedDate = getFormattedDate(notification.getDate());

                Platform.runLater(() -> {
                    // Add the notification to the UI
                    addNotification(notification.getOrderNumber(), notification.getStatus(), notification.getTime(), notification.getIconPath(), formattedDate);
                });

                try {
                    // Delay of 1 second between adding notifications
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void addNotification(String orderNumber, String status, String time, String iconPath, String date) {
        // Notification container
        HBox notificationBox = new HBox();
        notificationBox.setSpacing(10);
        notificationBox.setStyle("-fx-padding: 18; -fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 4);");
        notificationBox.setAlignment(Pos.CENTER_LEFT);

        // Icon
        InputStream iconStream = getClass().getResourceAsStream(iconPath);
        if (iconStream == null) {
            throw new RuntimeException("Icon not found at path: " + iconPath);
        }
        ImageView icon = new ImageView(new Image(iconStream));
        icon.setFitWidth(30);
        icon.setPreserveRatio(true); // Maintain original proportions
        icon.setSmooth(true); // Smooth scaling for better appearance

        VBox iconContainer = new VBox(icon);
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefHeight(50);

        VBox textContainer = new VBox();
        textContainer.setSpacing(5);
        textContainer.setPrefWidth(300);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        Label orderLabel = new Label(orderNumber);
        orderLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
        statusLabel.setWrapText(true); // Enable text wrapping
        statusLabel.setMaxWidth(300);

        VBox dateTimeContainer = new VBox();
        dateTimeContainer.setAlignment(Pos.CENTER_RIGHT);
        dateTimeContainer.setPrefWidth(150);

        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #999999;");
        dateLabel.setAlignment(Pos.CENTER_RIGHT);
        dateLabel.setWrapText(false);

        // Time label
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #999999;");
        timeLabel.setAlignment(Pos.CENTER_RIGHT);
        timeLabel.setWrapText(false);

        // Add date and time labels to the container
        dateTimeContainer.getChildren().addAll(dateLabel, timeLabel);



        textContainer.getChildren().addAll(orderLabel, statusLabel);

        // Add to notification box
        notificationBox.getChildren().addAll(icon, textContainer, dateTimeContainer);
        notificationBox.setSpacing(15); // Increase spacing between icon and text
        notificationBox.setPadding(new Insets(10));

        // Add to notification container at the top
        notificationContainer.getChildren().add(0, notificationBox); // Add to the top
    }
    private String getFormattedDate(LocalDate notificationDate) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (notificationDate.equals(today)) {
            return "Today";
        } else if (notificationDate.equals(yesterday)) {
            return "Yesterday";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM");
            return notificationDate.format(formatter); // Format as "2/11"
        }
    }

    private static class Notification {
        private final String orderNumber;
        private final String status;
        private final String time;
        private final String iconPath;
        private final LocalDate date; // Add a date field

        public Notification(String orderNumber, String status, String time, String iconPath, LocalDate date) {
            this.orderNumber = orderNumber;
            this.status = status;
            this.time = time;
            this.iconPath = iconPath;
            this.date = date; // Initialize the date field
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public String getStatus() {
            return status;
        }

        public String getTime() {
            return time;
        }

        public String getIconPath() {
            return iconPath;
        }

        public LocalDate getDate() {
            return date; // Add getter for date
        }
    }


}

