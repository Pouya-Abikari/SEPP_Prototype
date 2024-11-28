package se_prototype.se_prototype.Model;

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
}