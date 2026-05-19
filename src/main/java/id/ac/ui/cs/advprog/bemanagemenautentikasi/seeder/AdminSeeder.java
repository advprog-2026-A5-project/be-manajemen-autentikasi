package id.ac.ui.cs.advprog.bemanagemenautentikasi.seeder;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@mysawit.com")) {
            User admin = new User();
            admin.setNama("Admin Utama");
            admin.setUsername("admin");
            admin.setEmail("admin@mysawit.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");

            userRepository.save(admin);
            log.info("Default admin seeded: email=admin@mysawit.com username=admin role=ADMIN");
        } else {
            log.info("Default admin already exists: email=admin@mysawit.com");
        }
    }
}
