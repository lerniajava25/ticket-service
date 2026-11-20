package org.example.ticketservice.config;

import org.example.ticketservice.model.Role;
import org.example.ticketservice.model.User;
import org.example.ticketservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.user-password}")
    private String userPassword;

    @Value("${app.init.agent-password}")
    private String agentPassword;

    @Value("${app.init.admin-password}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(User.builder().username("user").password(passwordEncoder.encode(userPassword)).role(Role.USER).enabled(true).build());
        }
        if (userRepository.findByUsername("agent").isEmpty()) {
            userRepository.save(User.builder().username("agent").password(passwordEncoder.encode(agentPassword)).role(Role.AGENT).enabled(true).build());
        }
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(User.builder().username("admin").password(passwordEncoder.encode(adminPassword)).role(Role.ADMIN).enabled(true).build());
        }
    }
}


