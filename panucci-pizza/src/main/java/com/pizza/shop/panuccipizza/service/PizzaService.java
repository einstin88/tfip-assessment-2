package com.pizza.shop.panuccipizza.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.pizza.shop.panuccipizza.Utils.Constants;
import com.pizza.shop.panuccipizza.model.Basket;
import com.pizza.shop.panuccipizza.model.Order;
import com.pizza.shop.panuccipizza.repo.PizzaRepo;

@Service
public class PizzaService {
    private static final Logger logger = LoggerFactory.getLogger(PizzaService.class);

    @Autowired
    PizzaRepo pizzaRepo;

    /*
     * Retrieve order from DB
     */
    public Optional<Order> getOrder(String id) {
        return pizzaRepo.findById(id);
    }


    /*
     * Custom validation of order basket (view 0)
     */
    public Boolean validateBasket(Basket basket, BindingResult result) {
        logger.info("++ Validating submitted basket");
        String pizza = basket.getPizza();
        String size = basket.getSize();

        if (pizza != null && Constants.PIZZAS.keySet().contains(pizza)) {
            if (size != null && Constants.SIZES.keySet().contains(size)) {
                return true;

            } else {
                String errMessage = "%s is not a valid size choice".formatted(basket.getSize());
                logger.error(errMessage);
                ObjectError err = new FieldError("sizeError", "size", errMessage);
                result.addError(err);
            }
        } else {
            String errMessage = "%s is not a valid pizza choice".formatted(basket.getPizza());
            logger.error(errMessage);
            ObjectError err = new FieldError("sizeError", "pizza", errMessage);
            result.addError(err);
        }
        return false;
    }

    /*
     * Populate the Order object and get the total cost
     */
    public Order processOrder(Order order, Basket basket) {
        logger.info("++ Processing Order...");

        order.setPizza(basket.getPizza());
        order.setSize(basket.getSize());
        order.setQuantity(basket.getQuantity());

        Float rushCost = order.getRush() ? 2f : 0f;
        Float totalCost = calculateCost(basket, rushCost);
        order.setTotal(totalCost);

        logger.info(">> Total cost of order-%s is: $%.2f".formatted(order.getOrderId(), totalCost));

        pizzaRepo.saveOrder(order);

        return order;
    }

    /*
     * Helper function to segregate some of the logics from the caller function
     */
    static Float calculateCost(Basket basket, Float rushCost) {

        return Constants.PIZZAS.get(basket.getPizza()) *
                Constants.SIZES.get(basket.getSize()) *
                basket.getQuantity() +
                rushCost;
    }
}
