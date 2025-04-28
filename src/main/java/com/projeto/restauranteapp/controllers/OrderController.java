package com.projeto.restauranteapp.controllers;

import com.projeto.restauranteapp.entities.*;
import com.projeto.restauranteapp.repositories.*;
import com.projeto.restauranteapp.enums.OrderStatus;
import com.projeto.restauranteapp.enums.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private CartRepository cartRepository;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/new")
    public String newOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "admin/order-form";
    }
    
    @PostMapping
    public String createOrderFromCart(HttpSession session, RedirectAttributes redirectAttributes) {
        @SuppressWarnings("unchecked")
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");
        
        if (cartItems == null || cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Seu carrinho está vazio");
            return "redirect:/cart";
        }
        
        try {
            Order order = new Order();
            order.setDate(new Date());
            order.setStatus(OrderStatus.PENDING);
            
            Client client = new Client();
            client.setTableNumber((int)(Math.random() * 100) + 1);
            
            clientRepository.save(client);
            
            Cart cart = new Cart();
            cart.setClient(client);
            cart.setItems(new ArrayList<>());
            cartRepository.save(cart);
            
            client.setCart(cart);
            clientRepository.save(client);
            
            order.setClient(client);
            
            orderRepository.save(order);
            
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setNotes(cartItem.getCustomization());
                orderItem.setPrice(cartItem.getProduct().getPrice());
                
                orderItemRepository.save(orderItem);
                orderItems.add(orderItem);
            }
            
            order.setItems(orderItems);
            orderRepository.save(order);
            
            Ticket ticket = new Ticket();
            ticket.setOrder(order);
            ticket.setStatus(TicketStatus.RECEIVED);
            ticket.setTimestamp(LocalDateTime.now());
            ticketRepository.save(ticket);
            
            session.removeAttribute("cart");
            
            redirectAttributes.addFlashAttribute("orderSuccess", true);
            redirectAttributes.addFlashAttribute("orderId", order.getId());
            
            return "redirect:/orders/confirmation";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar pedido: " + e.getMessage());
            return "redirect:/cart";
        }
    }
    
    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        boolean orderSuccess = model.asMap().containsKey("orderSuccess") ? 
                               (Boolean) model.asMap().get("orderSuccess") : false;
        
        if (!orderSuccess) {
            return "redirect:/products";
        }
        
        return "order-confirmation";
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("order", order);
        return "admin/order-form";
    }

    @PostMapping("/update/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute Order updatedOrder) {
        updatedOrder.setId(id);
        orderRepository.save(updatedOrder);
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "redirect:/orders";
    }

    @GetMapping("/add-item/{orderId}")
    public String addOrderItemForm(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderItem", new OrderItem());
        return "admin/order-item-form";
    }

    @PostMapping("/add-item/{orderId}")
    public String saveOrderItem(@PathVariable Long orderId, @ModelAttribute OrderItem orderItem) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        orderItem.setOrder(order);
        orderItemRepository.save(orderItem);
        return "redirect:/orders/" + orderId + "/items";
    }

    @GetMapping("/{orderId}/items")
    public String listOrderItems(@PathVariable Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        List<OrderItem> items = orderItemRepository.findByOrder(order);
        model.addAttribute("items", items);
        return "admin/order-items";
    }

    @GetMapping("/delete-item/{id}")
    public String deleteOrderItem(@PathVariable Long id) {
        orderItemRepository.deleteById(id);
        return "redirect:/orders";
    }
}
