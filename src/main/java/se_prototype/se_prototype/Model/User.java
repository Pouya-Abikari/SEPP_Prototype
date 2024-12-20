package se_prototype.se_prototype.Model;

import java.util.Arrays;

public class User {
    String name;
    String email;
    String password;
    String[] addresses;       // All addresses
    String currentAddress;    // Current address
    int[] orderID;
    int currentOrderID;
    int log;

    public User(String name, String email, String password, String[] addresses, String currentAddress, int[] orderID, int currentOrderID, int errorCase) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.addresses = addresses;
        this.currentAddress = currentAddress;
        this.orderID = orderID;
        this.currentOrderID = currentOrderID;
        this.log = errorCase;
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

    public int getLog() {
        return log;
    }

    public int[] getOrderIDs() {
        return orderID;
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

    public void setLog(int log) {
        this.log = log;
    }

    @Override
    public String toString() {
        return name + "," + email + "," + password + "," +
                String.join(";", addresses) + "," +
                currentAddress + "," +
                Arrays.toString(orderID).replaceAll("[\\[\\] ]", "") + "," +
                currentOrderID + "," + log;
    }

    public static User fromString(String userData) {
        String[] parts = userData.split(",");
        String[] addresses = parts[3].split(";");
        int[] orderIDs = Arrays.stream(parts[5].split(";"))
                .mapToInt(Integer::parseInt)
                .toArray();

        return new User(
                parts[0], parts[1], parts[2], addresses,
                parts[4], orderIDs, Integer.parseInt(parts[6]),
                Integer.parseInt(parts[7])
        );
    }
}