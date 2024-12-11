package se_prototype.se_prototype.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserCart {
    private final String email;
    private final List<Product> cartItems;

    public UserCart(String userName, List<Product> cartItems) {
        this.email = userName;
        this.cartItems = cartItems;
    }

    public String getEmail() {
        return email;
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(email + ";");
        for (Product item : cartItems) {
            sb.append(item.getName()).append(",").append(item.getDescription()).append(",")
                    .append(item.getPrice()).append(",").append(item.getImageUrl()).append(",")
                    .append(item.getDiscount()).append(",").append(item.getQuantity()).append(";");
        }
        return sb.toString();
    }

    public static UserCart fromString(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: data is null or empty");
        }

        String[] parts = data.split(";");
        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid format: " + data);
        }

        String email = parts[0].trim();
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Invalid email in data: " + data);
        }

        List<Product> cartItems = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String[] itemParts = parts[i].split(",");
            if (itemParts.length != 6) {
                System.err.println("Skipping invalid product data: " + Arrays.toString(itemParts));
                continue;
            }

            try {
                String name = itemParts[0].trim();
                String description = itemParts[1].trim();
                double price = Double.parseDouble(itemParts[2].trim());
                String imageUrl = itemParts[3].trim();
                double discount = Double.parseDouble(itemParts[4].trim());
                int quantity = Integer.parseInt(itemParts[5].trim());

                cartItems.add(new Product(name, description, price, imageUrl, discount, quantity));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing product data: " + Arrays.toString(itemParts) + " - " + e.getMessage());
            }
        }

        return new UserCart(email, cartItems);
    }
}