package com.projeto.restauranteapp.repositories;

import com.projeto.restauranteapp.entities.Product;
import com.projeto.restauranteapp.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
}
