package batch02.ssf.assessment.orderingapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import batch02.ssf.assessment.orderingapp.model.CartItem;
import batch02.ssf.assessment.orderingapp.service.QuotationService;
import batch02.ssf.assessment.orderingapp.utils.Consts;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
@Slf4j
public class PurchaseOrderController {
    @Autowired
    QuotationService svc;

    /*
     * Landing page of the app
     * - adds session's cart and item form for thymeleaf to render
     */
    @GetMapping(path = { "/", "/index.html" })
    public String getLandingPage(
            HttpSession session,
            Model model) {

        log.info(">>> Requesting landing page...");

        // session.invalidate();

        Map<String, Integer> cart = getSessionCart(session);

        model.addAttribute("cart", cart);
        model.addAttribute("cartItem", new CartItem("", null));

        return "view1";
    }

    /*
     * Handles adding of item
     * - validates submitted form. Any errors will be displayed and stop item from
     * being added to cart
     */
    @PostMapping(path = "/add-item", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String postNewItem(
            @Valid CartItem cartItem,
            BindingResult result,
            HttpSession session,
            Model model) {

        log.info(">>> Received new cart item: " + cartItem);

        Map<String, Integer> cart = getSessionCart(session);

        // Validate if submitted item is in stock
        // If validation fails, produce the error to display
        log.debug(">>> Validating submitted item form...");
        svc.validateCartItem(cartItem, result);
        if (result.hasErrors()) {
            log.error("--- Invalid item form submitted");

            model.addAttribute("cart", cart);

            return "view1";
        }

        // Adds item to cart if validation is ok
        log.debug("+++ Validation successful.. Adding item to cart");
        cart = svc.addItemToCart(cartItem, cart);
        session.setAttribute(Consts.SESS_CART, cart);

        model.addAttribute("cart", cart);
        model.addAttribute("cartItem", new CartItem("", null));

        return "view1";
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Integer> getSessionCart(HttpSession session) {
        Map<String, Integer> cart;

        if (null == (cart = (Map<String, Integer>) session.getAttribute(Consts.SESS_CART))) {
            log.debug(">>> Starting new session...");
            cart = new HashMap<>();
        }

        return cart;
    }
}
