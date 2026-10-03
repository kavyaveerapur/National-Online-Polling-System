package com.polling.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.polling.system.model.User;
import com.polling.system.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlinePollingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlinePollingSystemApplication.class, args);
    }

    // 🔥 DEFAULT ADMIN CREATION
    @Bean
    CommandLineRunner init(UserRepository repo) {
        return args -> {

            User admin = repo.findByEmail("admin@gmail.com");

            if (admin == null) {
                User user = new User();
                user.setName("Admin");
                user.setEmail("admin@gmail.com");
                user.setPassword("admin123");
                user.setRole("ADMIN");
                user.setVerified(true);

                repo.save(user);

                System.out.println("✅ Default admin created");
            }
        };
    }
}