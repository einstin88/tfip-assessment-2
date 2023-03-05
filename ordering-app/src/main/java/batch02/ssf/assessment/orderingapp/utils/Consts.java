package batch02.ssf.assessment.orderingapp.utils;

import java.util.List;

public class Consts {
    public static final String SESS_CART = "cart";

    public static final String URL_Q_SYS = "https://quotation.chuklee.com/quotation";

    public static final String ATTR_QUOTE_ID = "quoteId";
    public static final String ATTR_QUOTES = "quotations";
    public static final String ATTR_QUOTE_ITEM = "item";
    public static final String ATTR_QUOTE_PRICE = "unitPrice";

    public static final List<String> ITEM_OPTIONS;

    static {
        ITEM_OPTIONS = List.of(
                "apple", "orange", "bread", "cheese",
                "chicken", "mineral_water", "instant_noodles");
    }
}
