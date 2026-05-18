package id.ac.ui.cs.advprog.bemanagemenautentikasi.seeder;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminSeederTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminSeeder adminSeeder;

    @Test
    void testRun_AdminDoesNotExist_ShouldCreateAdmin() throws Exception {
        // GIVEN: Belum ada akun admin utama di database
        when(userRepository.existsByEmail("admin@mysawit.com")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded_admin_password");

        // WHEN: Metode run() dijalankan otomatis oleh Spring Boot saat startup
        adminSeeder.run();

        // THEN: Memastikan UserRepository.save() dipanggil tepat 1 kali
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        // Memastikan data Admin yang di-save sudah benar dan sesuai spek
        User savedAdmin = userCaptor.getValue();
        assertEquals("Admin Utama", savedAdmin.getNama());
        assertEquals("admin@mysawit.com", savedAdmin.getEmail());
        assertEquals("admin", savedAdmin.getUsername());
        assertEquals("encoded_admin_password", savedAdmin.getPassword());
        assertEquals("ADMIN", savedAdmin.getRole());
    }

    @Test
    void testRun_AdminAlreadyExists_ShouldNotCreateAdmin() throws Exception {
        // GIVEN: Akun admin utama sudah ada di database
        when(userRepository.existsByEmail("admin@mysawit.com")).thenReturn(true);

        // WHEN
        adminSeeder.run();

        // THEN: Memastikan UserRepository.save() TIDAK pernah dipanggil
        verify(userRepository, never()).save(any(User.class));
    }
}
