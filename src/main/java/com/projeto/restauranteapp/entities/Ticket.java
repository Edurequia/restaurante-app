package com.projeto.restauranteapp.entities;

import com.projeto.restauranteapp.enums.TicketStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Ticket() {}

    public Ticket(Order order, TicketStatus status, LocalDateTime timestamp) {
        this.order = order;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Ticket(Long id, Order order, TicketStatus status, LocalDateTime timestamp) {
        this.id = id;
        this.order = order;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", order=" + order +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}
