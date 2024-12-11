package se_prototype.se_prototype.Model;

import java.util.ArrayList;
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
        String[] parts = data.split(";");
        String userName = parts[0];
        List<Product> cartItems = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String[] itemData = parts[i].split(",");
            cartItems.add(new Product(
                    itemData[0],
                    itemData[1],
                    Double.parseDouble(itemData[2]),
                    itemData[3],
                    Double.parseDouble(itemData[4]),
                    Integer.parseInt(itemData[5])
            ));
        }
        return new UserCart(userName, cartItems);
    }
}