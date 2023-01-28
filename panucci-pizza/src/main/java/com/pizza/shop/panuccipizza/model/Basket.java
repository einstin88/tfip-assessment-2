package com.pizza.shop.panuccipizza.model;

// import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Basket (
    @NotNull(message = "Select <em>ONE</em> Pizza") 
    String pizza,
    @NotNull(message = "Select ONE Size")
    String size,
    @Min(value = 1, message = "Minimum quantity of ONE")
    @Max(value =  10, message = "Maximum quantity of TEN")
    @NotNull(message = "Please enter a quantity from <b>1 to 10</b>")
    Integer quantity
) {}

// public class Basket implements Serializable {
//     // Form fields
//     @NotNull(message = "Select <em>ONE</em> Pizza")
//     private String pizza;

//     @NotNull(message = "Select ONE Size")
//     private String size;

//     @Min(value = 1, message = "Minimum quantity of ONE")
//     @Max(value =  10, message = "Maximum quantity of TEN")
//     @NotNull(message = "Please enter a quantity from <b>1 to 10</b>")
//     private Integer quantity;

//     public String getPizza() {
//         return pizza;
//     }

//     public void setPizza(String pizza) {
//         this.pizza = pizza;
//     }

//     public String getSize() {
//         return size;
//     }

//     public void setSize(String size) {
//         this.size = size;
//     }

//     public Integer getQuantity() {
//         return quantity;
//     }

//     public void setQuantity(Integer quantity) {
//         this.quantity = quantity;
//     }

//     @Override
//     public String toString() {
//         return "Basket [pizza=" + pizza + ", size=" + size + ", quantity=" + quantity + "]";
//     }
// }
