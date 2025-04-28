package com.projeto.restauranteapp.controllers;

import com.projeto.restauranteapp.entities.Order;
import com.projeto.restauranteapp.entities.OrderItem;
import com.projeto.restauranteapp.entities.Ticket;
import com.projeto.restauranteapp.enums.OrderStatus;
import com.projeto.restauranteapp.enums.TicketStatus;
import com.projeto.restauranteapp.repositories.OrderRepository;
import com.projeto.restauranteapp.repositories.OrderItemRepository;
import com.projeto.restauranteapp.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    @GetMapping
    public String listTickets(Model model) {
        List<Ticket> tickets = ticketRepository.findAll();
        model.addAttribute("tickets", tickets);
        return "admin/tickets";
    }
    
    @GetMapping("/kitchen")
    public String viewKitchenDisplay(Model model) {
        return "redirect:/admin/orders";
    }

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket inválido"));
        
        List<OrderItem> orderItems = orderItemRepository.findByOrder(ticket.getOrder());
        
        model.addAttribute("ticket", ticket);
        model.addAttribute("orderItems", orderItems);
        return "admin/ticket-details";
    }

    @GetMapping("/new")
    public String newTicketForm(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        model.addAttribute("ticket", new Ticket());
        return "admin/ticket-form";
    }

    @PostMapping("/save")
    public String saveTicket(@ModelAttribute Ticket ticket) {
        ticket.setTimestamp(LocalDateTime.now());
        ticket.setStatus(TicketStatus.RECEIVED);
        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }

    @PostMapping("/update/{id}")
    public String updateTicketStatus(@PathVariable Long id, 
                                    @RequestParam TicketStatus status,
                                    RedirectAttributes redirectAttributes) {
        try {
            Ticket ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket inválido"));
            
            ticket.setStatus(status);
            ticket.setTimestamp(LocalDateTime.now());
            ticketRepository.save(ticket);
            
            if (status == TicketStatus.DONE) {
                Order order = ticket.getOrder();
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
            }
            
            redirectAttributes.addFlashAttribute("success", "Status do ticket atualizado com sucesso!");
            return "redirect:/tickets/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar status: " + e.getMessage());
            return "redirect:/tickets";
        }
    }
    
    @PostMapping("/update-status")
    public String updateTicketStatusFromKitchen(@RequestParam Long ticketId, 
                                               @RequestParam TicketStatus status,
                                               @RequestParam(required = false) String returnUrl,
                                               @RequestParam(required = false) String activeTab,
                                               RedirectAttributes redirectAttributes) {
        try {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket inválido"));
            
            ticket.setStatus(status);
            ticket.setTimestamp(LocalDateTime.now());
            ticketRepository.save(ticket);
            
            if (status == TicketStatus.DONE) {
                Order order = ticket.getOrder();
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
                redirectAttributes.addFlashAttribute("successMessage", "Pedido #" + order.getId() + " marcado como pronto!");
            } else if (status == TicketStatus.PREPARING) {
                redirectAttributes.addFlashAttribute("successMessage", "Preparo do pedido #" + ticket.getOrder().getId() + " iniciado!");
            }
            
            if (returnUrl != null && !returnUrl.isEmpty()) {
                if (activeTab != null && !activeTab.isEmpty()) {
                    return "redirect:" + returnUrl + "#" + activeTab;
                }
                return "redirect:" + returnUrl;
            }
            return "redirect:/admin/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar status: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTicket(@PathVariable Long id) {
        ticketRepository.deleteById(id);
        return "redirect:/tickets";
    }
}
