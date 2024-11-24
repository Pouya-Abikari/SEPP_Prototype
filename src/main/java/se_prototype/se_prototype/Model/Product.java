package se_prototype.se_prototype.Model;

public class Product {
    private String name;
    private String description;
    private double price;
    private String imageUrl;

    public Product(String name, String description, double price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDiscountPrice(int discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        } else if (discount == 0) {
            return String.format("$%.2f", price);
        } else {
            double discountedPrice = price - (price * discount / 100);
            return String.format("$%.2f", discountedPrice);
        }
    }
}
