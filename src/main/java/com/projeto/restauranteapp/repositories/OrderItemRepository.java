package com.projeto.restauranteapp.repositories;

import com.projeto.restauranteapp.entities.Order;
import com.projeto.restauranteapp.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
