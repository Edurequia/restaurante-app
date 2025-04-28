package com.projeto.restauranteapp.controllers;

import com.projeto.restauranteapp.entities.Order;
import com.projeto.restauranteapp.entities.OrderItem;
import com.projeto.restauranteapp.repositories.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/customer/orders")
    public String customerOrders(
            @RequestParam(required = false) Integer tableNumber,
            Model model
    ) {
        if (tableNumber != null) {
            List<Order> orders = orderRepository.findByClientTableNumberOrderByDateDesc(tableNumber);
            
            double totalAmount = 0.0;
            if (!orders.isEmpty()) {
                for (Order order : orders) {
                    for (OrderItem item : order.getItems()) {
                        totalAmount += item.getProduct().getPrice() * item.getQuantity();
                    }
                }
            }
            
            model.addAttribute("orders", orders);
            model.addAttribute("tableNumber", tableNumber);
            model.addAttribute("totalAmount", totalAmount);
        }
        
        return "customer-orders";
    }
} 