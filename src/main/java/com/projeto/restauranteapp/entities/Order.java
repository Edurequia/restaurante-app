package com.projeto.restauranteapp.entities;

import com.projeto.restauranteapp.enums.OrderStatus;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private Order() {
    }

    public Order(Long id, Date date, OrderStatus status, List<OrderItem> items, Client client) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.items = items;
        this.client = client;
    }

    public Order(Date date, OrderStatus status, List<OrderItem> items, Client client) {
        this.date = date;
        this.status = status;
        this.items = items;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Order{" +
                "date=" + date +
                ", status=" + status +
                ", items=" + items +
                ", client=" + client +
                '}';
    }
}
