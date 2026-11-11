package org.example.ticketservice.controller;

import org.example.ticketservice.model.*;
import org.example.ticketservice.repository.*;
import org.example.ticketservice.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;

    public TicketController(TicketRepository ticketRepository, CommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping
    public String listTickets(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        List<Ticket> tickets;

        if (currentUser.getRole() == Role.USER) {
            tickets = ticketRepository.findByCreator(currentUser);
        } else {
            tickets = ticketRepository.findAll();
        }

        model.addAttribute("tickets", tickets);
        return "tickets/list";
    }

    @GetMapping("/new")
    public String newTicketForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        return "tickets/new";
    }

    @PostMapping
    public String createTicket(@ModelAttribute Ticket ticket, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ticket.setCreator(userDetails.getUser());
        ticket.setStatus(TicketStatus.NEW);
        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Felaktigt ID"));
        User currentUser = userDetails.getUser();

        if (currentUser.getRole() == Role.USER && !ticket.getCreator().getId().equals(currentUser.getId())) {
            return "redirect:/tickets?error=Unauthorized";
        }

        model.addAttribute("ticket", ticket);
        model.addAttribute("statuses", TicketStatus.values());
        return "tickets/detail";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @RequestParam String content, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Felaktigt ID"));
        User currentUser = userDetails.getUser();

        if (currentUser.getRole() == Role.USER && !ticket.getCreator().getId().equals(currentUser.getId())) {
            return "redirect:/tickets?error=Unauthorized";
        }

        Comment comment = Comment.builder().content(content).ticket(ticket).user(currentUser).build();
        commentRepository.save(comment);
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/claim")
    public String claimTicket(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Felaktigt ID"));
        ticket.setAgent(userDetails.getUser());
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticketRepository.save(ticket);
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Felaktigt ID"));
        ticket.setStatus(status);
        ticketRepository.save(ticket);
        return "redirect:/tickets/" + id;
    }
}

