package com.pizza.shop.panuccipizza.Utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    // Constants
    public static final Map<String, Float> PIZZAS = new HashMap<>();

    public static final Map<String, Float> SIZES = new HashMap<>();

    static {
        PIZZAS.put("bella", 30f);
        PIZZAS.put("margherita", 22f);
        PIZZAS.put("marinara", 30f);
        PIZZAS.put("spianatacalabrese", 30f);
        PIZZAS.put("trioformaggio", 25f);

        SIZES.put("sm", 1f);
        SIZES.put("md", 1.2f);
        SIZES.put("lg", 1.5f);

    }
}
