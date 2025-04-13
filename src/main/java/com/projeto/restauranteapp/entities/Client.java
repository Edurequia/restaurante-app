package com.projeto.restauranteapp.entities;

import jakarta.persistence.*;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int tableNumber;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

    public Client(){}

    public Client(int tableNumber, Cart cart) {
        this.tableNumber = tableNumber;
        this.cart = cart;
    }

    public Client(Long id, int tableNumber, Cart cart) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.cart = cart;
    }

    @Override
    public String toString() {
        return "Client{" +
                "tableNumber=" + tableNumber +
                ", cart=" + cart +
                '}';
    }
}
