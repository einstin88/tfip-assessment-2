package batch02.ssf.assessment.orderingapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import batch02.ssf.assessment.orderingapp.model.CartItem;
import batch02.ssf.assessment.orderingapp.model.Invoice;
import batch02.ssf.assessment.orderingapp.model.Quotation;
import batch02.ssf.assessment.orderingapp.model.ShippingForm;
import batch02.ssf.assessment.orderingapp.service.QuotationService;
import batch02.ssf.assessment.orderingapp.utils.Consts;
import batch02.ssf.assessment.orderingapp.utils.Utils;
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

        log.info(">>> POST request for adding item to cart...");
        log.debug(">>> Received new cart item: " + cartItem.toString());

        Map<String, Integer> cart = getSessionCart(session);

        // Validate if submitted item is in stock
        // If validation fails, produce the error to display
        svc.validateCartItem(cartItem, result);
        if (result.hasErrors()) {
            log.error("--- Invalid item form submitted");

            model.addAttribute("cart", cart);

            return "view1";
        }

        // Adds item to cart if validation is ok
        log.info("+++ Validation successful.. Adding item to cart");
        cart = svc.addItemToCart(cartItem, cart);
        session.setAttribute(Consts.SESS_CART, cart);

        model.addAttribute("cart", cart);
        model.addAttribute("cartItem", new CartItem("", null));

        return "view1";
    }

    @GetMapping("/shippingaddress")
    public String getShippingAddress(
            HttpSession session,
            Model model) {

        log.info(">>> Requesting shipping address form...");

        // Checks for valid cart in session
        Map<String, Integer> cart = getSessionCart(session);
        if (cart.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("shippingForm", new ShippingForm("", ""));

        return "view2";
    }

    @PostMapping(path = "/shippingaddress", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String postShippingAddress(
            @Valid ShippingForm shippingForm,
            BindingResult result,
            HttpSession session,
            Model model) {

        // Checks for valid cart in session
        Map<String, Integer> cart = getSessionCart(session);
        if (cart.isEmpty()) {
            return "redirect:/";
        }

        log.info(">>> POST request for submitting shipping address...");
        log.info(">>> Received shipping form: " + shippingForm.toString());

        // Validates form
        if (result.hasErrors()) {
            log.error("--- Shipping form is invalid");
            return "view2";
        }

        // Make api call to Qsys
        log.info("+++ Validation successful. Proceeding to generate order...");
        Quotation quotation;
        try {
            quotation = svc.getQuotations(
                    Utils.createCartList(getSessionCart(session)));
                    
        } catch (Exception e) {
            result.addError(new FieldError(
                    "QuotationError",
                    "global",
                    "Couldn't get quotations. " + e.getMessage()));
            return "view2";
        }

        log.info(">>> Quotation received: " + quotation);
        log.info(">>> Generating Invoice...");
        Invoice invoice = Utils.createInvoice(cart, shippingForm, quotation);

        log.info(">>> Generated invoice: " + invoice);
        model.addAttribute("invoice", invoice);
        session.invalidate();

        return "view3";
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Integer> getSessionCart(HttpSession session) {
        Map<String, Integer> cart;

        if (null == (cart = (Map<String, Integer>) session.getAttribute(Consts.SESS_CART))) {
            log.info(">>> Starting new session...");
            cart = new HashMap<>();
        }

        return cart;
    }
}
