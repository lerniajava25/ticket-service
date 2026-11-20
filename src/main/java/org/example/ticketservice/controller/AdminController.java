package org.example.ticketservice.controller;

import org.example.ticketservice.model.Role;
import org.example.ticketservice.model.User;
import org.example.ticketservice.repository.UserRepository;
import org.example.ticketservice.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Visa lista över alla användare och formulär för att skapa ny
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    // Skapa en ny användare
    @PostMapping
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/admin/users?error=UsernameExists";
        }

        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .build();

        userRepository.save(newUser);
        return "redirect:/admin/users?success=Created";
    }

    // Ändra roll på en användare
    @PostMapping("/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ogiltigt användar-ID"));
        user.setRole(role);
        userRepository.save(user);
        return "redirect:/admin/users?success=RoleUpdated";
    }

    // Aktivera eller inaktivera ett konto
    @PostMapping("/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ogiltigt användar-ID"));

        // Förhindra att admin råkar inaktivera sitt eget konto
        if (user.getId().equals(currentUser.getUser().getId())) {
            return "redirect:/admin/users?error=CannotDisableSelf";
        }

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return "redirect:/admin/users?success=StatusUpdated";
    }
}
