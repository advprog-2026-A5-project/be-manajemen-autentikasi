package id.ac.ui.cs.advprog.bemanagemenautentikasi.controller;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.security.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtils;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthController authController;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setUsername("budi_buruh");
        validUser.setEmail("budi@mysawit.com");
        validUser.setNama("Budi Santoso");
        validUser.setPassword("password123");
        validUser.setRole("BURUH");
    }

    @Test
    void testAuthenticateUser_SuccessWithEmail() {
        // GIVEN: Klien mengirim payload berupa email dan password
        User loginRequest = new User();
        loginRequest.setEmail("budi@mysawit.com");
        loginRequest.setPassword("password123");

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        )).thenReturn(authentication);
        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("budi@mysawit.com");
        when(jwtUtils.generateToken("budi@mysawit.com")).thenReturn("dummy-jwt-token");

        // WHEN
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        Assertions.assertNotNull(body);
        assertEquals("dummy-jwt-token", body.get("token"));
        
        // Memastikan authentication manager dipanggil dengan EMAIL, bukan Username
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
    }

    @Test
    void testRegisterUser_Success() {
        // GIVEN
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validUser.getPassword())).thenReturn("encodedPassword");

        // WHEN
        ResponseEntity<?> response = authController.registerUser(validUser);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // GIVEN: Email sudah terdaftar
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(true);

        // WHEN
        ResponseEntity<?> response = authController.registerUser(validUser);

        // THEN
        assertEquals(400, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        Assertions.assertNotNull(body);
        assertEquals("Error: Email is already in use!", body.get("message"));
        
        // Memastikan tidak ada user yang di-save
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testRegisterUser_InvalidRole() {
        // GIVEN: Role yang dikirim tidak valid
        validUser.setRole("HACKER");
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(false);

        // WHEN
        ResponseEntity<?> response = authController.registerUser(validUser);

        // THEN
        assertEquals(400, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        Assertions.assertNotNull(body);
        assertEquals("Error: Role is not valid!", body.get("message"));
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        // GIVEN: Username sudah terdaftar
        when(userRepository.existsByUsername(validUser.getUsername())).thenReturn(true);

        // WHEN
        ResponseEntity<?> response = authController.registerUser(validUser);

        // THEN
        assertEquals(400, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        Assertions.assertNotNull(body);
        assertEquals("Error: Username is already in use!", body.get("message"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_MandorMissingSertifikasi() {
        // GIVEN: Mendaftar sebagai MANDOR tapi sertifikasi kosong
        User mandorUser = new User();
        mandorUser.setUsername("budi_mandor");
        mandorUser.setEmail("budi_mandor@mysawit.com");
        mandorUser.setNama("Budi Mandor");
        mandorUser.setPassword("password123");
        mandorUser.setRole("MANDOR");
        mandorUser.setNomorSertifikasiMandor(""); // Kosong

        when(userRepository.existsByUsername("budi_mandor")).thenReturn(false);
        when(userRepository.existsByEmail("budi_mandor@mysawit.com")).thenReturn(false);

        // WHEN
        ResponseEntity<?> response = authController.registerUser(mandorUser);

        // THEN
        assertEquals(400, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        Assertions.assertNotNull(body);
        assertEquals("Error: Nomor Sertifikasi Mandor wajib diisi!", body.get("message"));
        verify(userRepository, never()).save(any(User.class));
    }
}
