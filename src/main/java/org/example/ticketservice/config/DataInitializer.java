package org.example.ticketservice.config;

import org.example.ticketservice.model.Role;
import org.example.ticketservice.model.User;
import org.example.ticketservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder().username("user").password(passwordEncoder.encode("user123")).role(Role.USER).enabled(true).build());
            userRepository.save(User.builder().username("agent").password(passwordEncoder.encode("agent456")).role(Role.AGENT).enabled(true).build());
        }
    }
}
