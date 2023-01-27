package com.pizza.shop.panuccipizza.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.pizza.shop.panuccipizza.model.Order;
import com.pizza.shop.panuccipizza.service.PizzaService;

import jakarta.json.Json;

@RestController
public class PizzaRestController {
    private static final Logger logger = LoggerFactory.getLogger(PizzaRestController.class);

    @Autowired
    PizzaService pizzaSvc;

    /*
     * Retrieve the order from DB
     * - respond according to whether a record is found
     */
    @GetMapping("/order/{orderID}")
    public ResponseEntity<String> viewOrder(
            @PathVariable String orderID) {

        logger.info(">> Accessing order: " + orderID);
        Optional<Order> order = pizzaSvc.getOrder(orderID);

        if (order.isPresent()) {
            String data = order.get().toJson();
            logger.info(data);
            return ResponseEntity
                    .ok(data);
        }

        String message = "Order " + orderID + " not found";
        logger.error("-- " + message);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Json.createObjectBuilder()
                        .add("message", message)
                        .build().toString());

    }
}
