package com.pizza.shop.panuccipizza.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping
    public String getLandingPage(
            HttpSession session,
            Model model) {
        Basket basket = (Basket) session.getAttribute("basket");
        if (basket == null) {
            basket = new Basket();
            session.setAttribute("basket", basket);
        }
        model.addAttribute("basket", basket);
        return "index";
    }

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

    @PostMapping(path = "pizza/order", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String addOrder(
            @Valid Order order,
            BindingResult result,
            HttpSession session,
            Model model) {

        Basket basket = (Basket) session.getAttribute("basket");
        logger.info(">> Basket in order details page: " + basket.toString());
        // logger.info(">> Order received: " + order.toString());

        if (result.hasErrors())
            return "delivery-details";

        Order confirmedOrder = pizzaSvc.processOrder(order, basket);
        logger.info(">> Confirmed order: " + confirmedOrder.toString());

        session.invalidate();
        model.addAttribute("order", confirmedOrder);
        model.addAttribute("pizzaCost",
                confirmedOrder.getRush() ? confirmedOrder.getTotal() - 2 : confirmedOrder.getTotal());

        return "order-confirmed";
    }

}
