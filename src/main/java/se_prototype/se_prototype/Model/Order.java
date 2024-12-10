package se_prototype.se_prototype.Model;

public class Order {
    private int orderID;
    private String orderDate;
    private String orderTimer;
    private String orderStatus;

    public Order(int orderID, String orderDate, String orderTime, String orderStatus) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderTimer = orderTime;
        this.orderStatus = orderStatus;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTimer() {
        return orderTimer;
    }

    public void setOrderTimer(String orderTimer) {
        this.orderTimer = orderTimer;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", orderDate='" + orderDate + '\'' +
                ", orderTime='" + orderTimer + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                '}';
    }
}
