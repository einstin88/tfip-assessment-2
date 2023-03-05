package batch02.ssf.assessment.orderingapp.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import batch02.ssf.assessment.orderingapp.model.CartItem;
import lombok.extern.slf4j.Slf4j;

import static batch02.ssf.assessment.orderingapp.utils.Consts.*;

// import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class QuotationService {

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

        Integer quantity = cart.getOrDefault(item.item(), 0) + item.quantity();
        cart.put(item.item(), quantity);
        
        return cart;
    }
}
