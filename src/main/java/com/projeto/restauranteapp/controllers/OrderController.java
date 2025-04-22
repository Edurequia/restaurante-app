package com.projeto.restauranteapp.controllers;

import com.projeto.restauranteapp.entities.Order;
import com.projeto.restauranteapp.entities.OrderItem;
import com.projeto.restauranteapp.repositories.OrderRepository;
import com.projeto.restauranteapp.repositories.OrderItemRepository;
import com.projeto.restauranteapp.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "";
    }

    @GetMapping("/new")
    public String newOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "";
    }

    @PostMapping
    public String saveOrder(@ModelAttribute Order order) {
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        return "";
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("order", order);
        return "orders/edit";
    }

    @PostMapping("/update/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute Order updatedOrder) {
        updatedOrder.setId(id);
        orderRepository.save(updatedOrder);
        return "";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "";
    }

    @GetMapping("/add-item/{orderId}")
    public String addOrderItemForm(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderItem", new OrderItem());
        return "";
    }

    @PostMapping("/add-item/{orderId}")
    public String saveOrderItem(@PathVariable Long orderId, @ModelAttribute OrderItem orderItem) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        orderItem.setOrder(order);
        orderItemRepository.save(orderItem);
        return "";
    }

    @GetMapping("/{orderId}/items")
    public String listOrderItems(@PathVariable Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        List<OrderItem> items = orderItemRepository.findByOrder(order);
        model.addAttribute("items", items);
        return "";
    }

    @GetMapping("/delete-item/{id}")
    public String deleteOrderItem(@PathVariable Long id) {
        orderItemRepository.deleteById(id);
        return "";
    }
}
