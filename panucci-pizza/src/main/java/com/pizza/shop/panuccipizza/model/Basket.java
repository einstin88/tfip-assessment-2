package com.pizza.shop.panuccipizza.model;

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

