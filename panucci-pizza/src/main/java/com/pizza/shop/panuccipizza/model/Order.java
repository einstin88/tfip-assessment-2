package com.pizza.shop.panuccipizza.model;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Order implements Serializable {

    private String orderId = generateID(8);

    @NotNull(message = "Name is required")
    @Size(min = 3, message = "Name is too short. Minimum 3 characters")
    private String name;

    @NotNull(message = "Address is required")
    private String address;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{8}$", message = "Phone number must be exactly 8 digits")
    private String phone;

    private Boolean rush;

    private String comments;

    private String pizza;

    private String size;

    private Integer quantity;

    private Float total;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getRush() {
        return rush;
    }

    public void setRush(Boolean rush) {
        this.rush = rush;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getPizza() {
        return pizza;
    }

    public void setPizza(String pizza) {
        this.pizza = pizza;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", name=" + name + ", address=" + address + ", phone=" + phone + ", rush="
                + rush + ", comments=" + comments + ", pizza=" + pizza + ", size=" + size + ", quantity=" + quantity
                + ", total=" + total + "]";
    }

    public String toJson() {
        JsonObject jsonOrder = Json.createObjectBuilder()
        .add("orderId", orderId)
        .add("name", name)
        .add("address", address)
        .add("phone", phone)
        .add("rush", rush)
        .add("comments", comments)
        .add("pizza", pizza)
        .add("size", size)
        .add("quantity", quantity)
        .add("total", total)        
        .build();

        return jsonOrder.toString();
    }

    static String generateID(Integer length) {
        String id = "";
        String HEX_STRING = "0123456789abcdef";
        Random rand = new SecureRandom();

        for (int i = 0; i < length; i++) {
            id += HEX_STRING.charAt(rand.nextInt(HEX_STRING.length()));
        }

        return id;
    }

}
