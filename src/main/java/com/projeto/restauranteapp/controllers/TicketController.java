package com.projeto.restauranteapp.controllers;

import com.projeto.restauranteapp.entities.Order;
import com.projeto.restauranteapp.entities.Ticket;
import com.projeto.restauranteapp.enums.TicketStatus;
import com.projeto.restauranteapp.repositories.OrderRepository;
import com.projeto.restauranteapp.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String listTickets(Model model) {
        List<Ticket> tickets = ticketRepository.findAll();
        model.addAttribute("tickets", tickets);
        return "";
    }

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket inválido"));
        model.addAttribute("ticket", ticket);
        return "";
    }

    @GetMapping("/new")
    public String newTicketForm(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        model.addAttribute("ticket", new Ticket());
        return "";
    }

    @PostMapping("/save")
    public String saveTicket(@ModelAttribute Ticket ticket) {
        ticket.setTimestamp(LocalDateTime.now());
        ticket.setStatus(TicketStatus.PREPARING);
        ticketRepository.save(ticket);
        return "";
    }

    @PostMapping("/update/{id}")
    public String updateTicketStatus(@PathVariable Long id, @RequestParam TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket inválido"));
        ticket.setStatus(status);
        ticket.setTimestamp(LocalDateTime.now());
        ticketRepository.save(ticket);
        return "";
    }

    @GetMapping("/delete/{id}")
    public String deleteTicket(@PathVariable Long id) {
        ticketRepository.deleteById(id);
        return "";
    }
}
