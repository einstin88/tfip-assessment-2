package com.pizza.shop.panuccipizza.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PizzaRestController {
    @GetMapping("/order/{orderID}")
    public ResponseEntity<String> viewOrder(
        @PathVariable String orderID
    ) {
        return ResponseEntity.ok(orderID);
    }
}
