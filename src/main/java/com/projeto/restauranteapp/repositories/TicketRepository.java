package com.projeto.restauranteapp.repositories;

import com.projeto.restauranteapp.entities.Ticket;
import com.projeto.restauranteapp.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatusIn(List<TicketStatus> statuses);
    List<Ticket> findByStatus(TicketStatus status);
}
