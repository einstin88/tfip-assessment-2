package com.pizza.shop.panuccipizza.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pizza.shop.panuccipizza.model.Basket;
import com.pizza.shop.panuccipizza.model.Order;
import com.pizza.shop.panuccipizza.service.PizzaService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
public class PizzaController {
    private static final Logger logger = LoggerFactory.getLogger(PizzaController.class);

    @Autowired
    PizzaService pizzaSvc;

    /*
     * Display the landing page and add an empty basket to session if it's not
     * present
     */
    @GetMapping
    public String getLandingPage(
            HttpSession session,
            Model model) {
        Basket basket = (Basket) session.getAttribute("basket");
        if (basket == null) {
            basket = new Basket("dummy", "dummy", 1);
            session.setAttribute("basket", basket);
        }
        model.addAttribute("basket", basket);
        return "index";
    }

    /*
     * - Perfom custom validation on the order basket
     * - if there is any errors, return to landing page
     * - otherwise, save basket in session and proceed to get delivery details
     */
    @PostMapping(path = "pizza", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String addToBasket(
            @Valid Basket basket,
            BindingResult result,
            HttpSession session,
            Model model) {
        logger.info(">> Basket received: " + basket.toString());

        if (pizzaSvc.validateBasket(basket, result) && !result.hasErrors()) {
            session.setAttribute("basket", basket);
            model.addAttribute("order", new Order());

            return "delivery-details";
        }
        return "index";
    }

    /*
     * - perform out-of-the-box validation
     * - if ok, process the order and save it to the database.
     * - invalidate object in the session and display the confirmation page 
     */
    @PostMapping(path = "pizza/order", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String addOrder(
            @Valid Order order,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors())
            return "delivery-details";

        Order confirmedOrder = pizzaSvc.processOrder(order,
                (Basket) session.getAttribute("basket"));
        logger.info(">> Confirmed order: " + confirmedOrder.toString());

        session.invalidate();
        model.addAttribute("order", confirmedOrder);
        model.addAttribute("pizzaCost",
                confirmedOrder.getRush() ? confirmedOrder.getTotal() - 2 : confirmedOrder.getTotal());

        return "order-confirmed";
    }

}
