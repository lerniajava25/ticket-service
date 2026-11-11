package org.example.ticketservice.repository;

import org.example.ticketservice.model.Ticket;
import org.example.ticketservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreator(User creator);
}

