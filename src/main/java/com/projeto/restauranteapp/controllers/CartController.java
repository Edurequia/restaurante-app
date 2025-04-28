package com.projeto.restauranteapp.controllers;

import com.projeto.restauranteapp.entities.Cart;
import com.projeto.restauranteapp.entities.CartItem;
import com.projeto.restauranteapp.entities.Product;
import com.projeto.restauranteapp.repositories.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    private List<CartItem> getCart(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(required = false) String customization,
                            HttpSession session) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido"));

        List<CartItem> cart = getCart(session);

        boolean found = false;
        for (CartItem existingItem : cart) {
            if (existingItem.getProduct().getId().equals(productId) &&
                    (existingItem.getCustomization() == null ? customization == null :
                            existingItem.getCustomization().equals(customization))) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setCustomization(customization);
            cart.add(item);
        }

        session.setAttribute("cart", cart);

        return "redirect:/products";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        List<CartItem> cart = getCart(session);

        cart.removeIf(item -> item.getProduct().getId().equals(productId));

        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItemQuantity(@RequestParam Long productId,
                                        @RequestParam int quantity,
                                        HttpSession session) {
        if (quantity < 1) {
            return "redirect:/cart";
        }
        
        List<CartItem> cart = getCart(session);
        
        for (CartItem item : cart) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/cart";
    }
}
