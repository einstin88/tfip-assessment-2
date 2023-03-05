package batch02.ssf.assessment.orderingapp.utils;

import java.util.List;

public class Consts {
    public static final String SESS_CART = "cart";

    public static final List<String> ITEM_OPTIONS;

    static {
        ITEM_OPTIONS = List.of(
                "apple", "orange", "bread", "cheese",
                "chicken", "mineral_water", "instant_noodles");
    }
}
