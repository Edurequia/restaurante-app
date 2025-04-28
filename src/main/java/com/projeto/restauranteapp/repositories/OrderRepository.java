package com.projeto.restauranteapp.repositories;

import com.projeto.restauranteapp.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByClientTableNumberOrderByDateDesc(Integer tableNumber);
}
