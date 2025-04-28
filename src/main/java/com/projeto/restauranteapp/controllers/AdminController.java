package com.projeto.restauranteapp.controllers;

import com.projeto.restauranteapp.entities.Product;
import com.projeto.restauranteapp.entities.Order;
import com.projeto.restauranteapp.entities.Ticket;
import com.projeto.restauranteapp.entities.Client;
import com.projeto.restauranteapp.repositories.ProductRepository;
import com.projeto.restauranteapp.repositories.OrderRepository;
import com.projeto.restauranteapp.repositories.TicketRepository;
import com.projeto.restauranteapp.enums.TicketStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("adminAuthenticated") != null &&
                (Boolean) session.getAttribute("adminAuthenticated");
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute("adminAuthenticated", true);
            return "redirect:/admin/products";
        } else {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminAuthenticated");
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String adminProducts(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "admin/products";
    }
    
    @GetMapping("/orders")
    public String adminOrders(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        
        try {
            List<Order> orders = orderRepository.findAll();
            
            for (Order order : orders) {
                if (order.getClient() == null) {
                    Client client = new Client();
                    client.setTableNumber(0);
                    order.setClient(client);
                }
                
                if (order.getItems() == null) {
                    order.setItems(new ArrayList<>());
                }
            }
            
            model.addAttribute("orders", orders);
            
            List<Ticket> activeTickets = ticketRepository.findByStatusIn(List.of(TicketStatus.RECEIVED, TicketStatus.PREPARING));
            
            for (Ticket ticket : activeTickets) {
                if (ticket.getOrder() == null) {
                    continue;
                }
                
                if (ticket.getOrder().getClient() == null) {
                    Client client = new Client();
                    client.setTableNumber(0);
                    ticket.getOrder().setClient(client);
                }
                
                if (ticket.getOrder().getItems() == null) {
                    ticket.getOrder().setItems(new ArrayList<>());
                }
            }
            
            model.addAttribute("activeTickets", activeTickets);
            
            return "admin/orders";
        } catch (Exception e) {
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "Ocorreu um erro ao carregar os pedidos: " + e.getMessage());
            
            model.addAttribute("orders", new ArrayList<>());
            model.addAttribute("activeTickets", new ArrayList<>());
            
            return "admin/orders";
        }
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new Product());
        }
        
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, 
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        
        if (productRepository.existsByCode(product.getCode())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro: Já existe um produto com o código " + product.getCode());
            redirectAttributes.addFlashAttribute("product", product);
            return "redirect:/admin/products/new";
        }
        
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produto adicionado com sucesso!");
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, 
                             Model model, 
                             HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        
        if (!model.containsAttribute("product")) {
            Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
            model.addAttribute("product", product);
        }
        
        return "admin/product-form";
    }

    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product product, 
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        
        Product existingProduct = productRepository.findByCode(product.getCode());
        if (existingProduct != null && !existingProduct.getId().equals(product.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro: Já existe um produto com o código " + product.getCode());
            redirectAttributes.addFlashAttribute("product", product);
            return "redirect:/admin/products/edit/" + product.getId();
        }
        
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso!");
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, 
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        
        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Produto removido com sucesso!");
        return "redirect:/admin/products";
    }
} 