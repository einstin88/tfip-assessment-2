package batch02.ssf.assessment.orderingapp.utils;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import batch02.ssf.assessment.orderingapp.model.Invoice;
import batch02.ssf.assessment.orderingapp.model.Quotation;
import batch02.ssf.assessment.orderingapp.model.ShippingForm;
import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Utils {

    public static List<String> createCartList(Map<String, Integer> cart) {
        return cart.keySet().stream().toList();
    }

    public static String createCartJson(List<String> cartList) {
        return Json.createArrayBuilder(cartList)
                .build().toString();
    }

    public static Quotation createQuotation(String response) {

        Quotation quote = new Quotation();
        JsonObject r = Json.createReader(new StringReader(response))
                .readObject();

        quote.setQuoteId(r.getString(Consts.ATTR_QUOTE_ID));

        r.getJsonArray(Consts.ATTR_QUOTES).stream()
                .map(q -> {
                    return q.asJsonObject();
                })
                .forEach(q -> {
                    quote.addQuotation(
                            q.getString(Consts.ATTR_QUOTE_ITEM),
                            (float) q.getJsonNumber(Consts.ATTR_QUOTE_PRICE).doubleValue());
                });

        return quote;
    }

    public static Invoice createInvoice(
            Map<String, Integer> cart,
            ShippingForm shippingForm,
            Quotation quotation) {

        Float total = 0f;

        for (String item : quotation.getQuotations().keySet()) {
            total += (cart.get(item) * quotation.getQuotation(item));
        }

        return new Invoice(
                quotation.getQuoteId(),
                shippingForm.name(),
                shippingForm.address(),
                total);
    }

}
