package batch02.ssf.assessment.orderingapp.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.RestTemplate;

import batch02.ssf.assessment.orderingapp.model.CartItem;
import batch02.ssf.assessment.orderingapp.model.Quotation;
import batch02.ssf.assessment.orderingapp.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import static batch02.ssf.assessment.orderingapp.utils.Consts.*;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class QuotationService {

    private static final RestTemplate template = new RestTemplate();

    public void validateCartItem(CartItem cartItem, BindingResult result) {

        log.info(">>> Validating submitted cart item form...");
        if (!ITEM_OPTIONS.contains(cartItem.item())) {
            ObjectError err = new FieldError(
                    "itemError", "item",
                    "We do not stock <b>Item: %s</b>".formatted(cartItem.item()));

            result.addError(err);
        }
    }

    public Map<String, Integer> addItemToCart(
            CartItem item,
            Map<String, Integer> cart) {

        String itemName = item.item();

        Integer quantity = cart.getOrDefault(itemName, 0) + item.quantity();
        cart.put(itemName, quantity);

        return cart;
    }

    public Quotation getQuotations(List<String> items) throws Exception {

        log.info(">>> Requesting quotation for items: " + items.toString());

        RequestEntity<String> request = RequestEntity
                .post(URL_Q_SYS, "")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Utils.createCartJson(items));

        ResponseEntity<String> response = template.exchange(request, String.class);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new Exception(response.getBody());

        log.info("<<< Received quotations: " + response.getBody());

        // This will work but I don't think it can capture the error message
        // ResponseEntity<QuotationResponse> testResponse = template.exchange(request,
        // QuotationResponse.class);
        // log.info(">> test: " + testResponse.getBody().toString());

        return Utils.createQuotation(response.getBody());
    }
}
