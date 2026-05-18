package id.ac.ui.cs.advprog.bemanagemenautentikasi.service;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUsername("budi_buruh");
        sampleUser.setEmail("budi@mysawit.com");
        sampleUser.setNama("Budi Santoso");
        sampleUser.setPassword("encodedPassword123");
        sampleUser.setRole("BURUH");
    }

    @Test
    void testLoadUserByUsernameShouldReturnUserDetailsWithEmailAndRole() {
        when(userRepository.findByEmail("budi@mysawit.com")).thenReturn(Optional.of(sampleUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("budi@mysawit.com");

        assertNotNull(userDetails);
        assertEquals("budi@mysawit.com", userDetails.getUsername()); 
        assertEquals("encodedPassword123", userDetails.getPassword());

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_BURUH")));
        
        verify(userRepository, times(1)).findByEmail("budi@mysawit.com");
    }

    @Test
    void testLoadUserByUsernameNotFoundShouldThrowException() {
        
        when(userRepository.findByEmail("unknown@mysawit.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("unknown@mysawit.com");
        });

        verify(userRepository, times(1)).findByEmail("unknown@mysawit.com");
    }
}
