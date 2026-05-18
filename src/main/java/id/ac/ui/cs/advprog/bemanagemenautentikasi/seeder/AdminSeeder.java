package id.ac.ui.cs.advprog.bemanagemenautentikasi.seeder;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Mengecek apakah Admin Utama sudah ada di database (via Email)
        if (!userRepository.existsByEmail("admin@mysawit.com")) {
            
            User admin = new User();
            admin.setNama("Admin Utama");
            admin.setUsername("admin");
            admin.setEmail("admin@mysawit.com");
            // Sebaiknya password default ini nantinya diubah melalui mekanisme reset password 
            // atau dimasukkan lewat environment variables di deployment production
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");

            userRepository.save(admin);
            System.out.println("✅ Default Admin Utama berhasil dibuat!");
        } else {
            System.out.println("ℹ️ Default Admin Utama sudah tersedia.");
        }
    }
}
