package se_prototype.se_prototype.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    String name;
    String email;
    String password;
    String[] addresses;       // All addresses
    String currentAddress;    // Current address
    int[] orderID;
    int currentOrderID;
    int errorCase;

    public User(String name, String email, String password, String[] addresses, String currentAddress, int[] orderID, int currentOrderID, int errorCase) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.addresses = addresses;
        this.currentAddress = currentAddress;
        this.orderID = orderID;
        this.currentOrderID = currentOrderID;
        this.errorCase = errorCase;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String[] getAddresses() {
        return addresses;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public int[] getOrderID() {
        return orderID;
    }

    public int getCurrentOrderID() {
        return currentOrderID;
    }

    public int getErrorCase() {
        return errorCase;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public void setOrderID(int[] orderID) {
        this.orderID = orderID;
    }

    public void setCurrentOrderID(int currentOrderID) {
        this.currentOrderID = currentOrderID;
    }

    public void setErrorCase(int errorCase) {
        this.errorCase = errorCase;
    }

    @Override
    public String toString() {
        return name + "," + email + "," + password + "," +
                String.join(";", addresses) + "," +
                currentAddress + "," +
                Arrays.toString(orderID).replaceAll("[\\[\\] ]", "") + "," +
                currentOrderID + "," + errorCase;
    }

    public static User fromString(String userData) {
        // Parse the line using a CSV parser to handle quoted fields
        List<String> parts = parseCSVLine(userData);

        // Extract and split addresses
        String[] addresses = parts.get(3).replace("\"", "").split(";");

        // Parse order IDs
        int[] orderIDs = Arrays.stream(parts.get(5).replace("\"", "").split(";"))
                .mapToInt(Integer::parseInt)
                .toArray();

        // Return the User object
        return new User(
                parts.get(0), // name
                parts.get(1), // email
                parts.get(2), // password
                addresses,    // addresses
                parts.get(4), // current address
                orderIDs,     // order IDs
                Integer.parseInt(parts.get(6)), // current order ID
                Integer.parseInt(parts.get(7))  // error case
        );
    }

    /**
     * Parses a CSV line into fields, handling quoted fields with commas.
     *
     * @param line The input CSV line.
     * @return A list of parsed fields.
     */
    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"' && (currentField.isEmpty() || currentField.charAt(currentField.length() - 1) != '\\')) {
                // Toggle inQuotes state when encountering unescaped quotes
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                // Add field to result if we encounter a comma outside quotes
                result.add(currentField.toString().trim());
                currentField.setLength(0); // Clear the current field
            } else {
                // Append character to current field
                currentField.append(c);
            }
        }
        // Add the last field
        result.add(currentField.toString().trim());

        return result;
    }
}