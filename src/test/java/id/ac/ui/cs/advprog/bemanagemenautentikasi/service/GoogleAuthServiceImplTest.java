package id.ac.ui.cs.advprog.bemanagemenautentikasi.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    // Gunakan Spy agar kita bisa mem-mock metode verifyToken tanpa mengganggu metode aslinya
    @Spy
    @InjectMocks
    private GoogleAuthServiceImpl googleAuthService;

    @Mock
    private GoogleIdToken googleIdToken;

    @Mock
    private GoogleIdToken.Payload payload;


    @Test
    void testVerifyAndAuthenticate_UserAlreadyExists() throws Exception {
        // GIVEN
        doReturn(googleIdToken).when(googleAuthService).verifyToken("valid-token");
        
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("existing@mysawit.com");
        when(payload.get("name")).thenReturn("User Lama");

        User existingUser = new User();
        existingUser.setEmail("existing@mysawit.com");

        when(userRepository.findByEmail("existing@mysawit.com")).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken("existing@mysawit.com")).thenReturn("jwt-token-existing");

        // WHEN
        String result = googleAuthService.verifyAndAuthenticateGoogleToken("valid-token", "BURUH");

        // THEN
        assertEquals("jwt-token-existing", result);
        verify(userRepository, never()).save(any(User.class)); // Pastikan tidak ada duplikasi data
    }

    @Test
    void testVerifyAndAuthenticate_NewUserRegisteredWithValidRole() throws Exception {
        // GIVEN
        doReturn(googleIdToken).when(googleAuthService).verifyToken("valid-token");
        
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("newuser@mysawit.com");
        when(payload.get("name")).thenReturn("Pengguna Baru");

        when(userRepository.findByEmail("newuser@mysawit.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("random-encoded-password");
        when(jwtUtil.generateToken("newuser@mysawit.com")).thenReturn("jwt-token-new");

        // WHEN
        String result = googleAuthService.verifyAndAuthenticateGoogleToken("valid-token", "MANDOR");

        // THEN
        assertEquals("jwt-token-new", result);
        // Memastikan userRepository.save() dipanggil untuk membuat user baru
        verify(userRepository, times(1)).save(argThat(user -> 
            user.getEmail().equals("newuser@mysawit.com") &&
            user.getNama().equals("Pengguna Baru") &&
            user.getRole().equals("MANDOR")
        ));
    }

    @Test
    void testVerifyAndAuthenticate_NewUserRegisteredWithInvalidRole_FallbackToBuruh() throws Exception {
        // GIVEN
        doReturn(googleIdToken).when(googleAuthService).verifyToken("valid-token");
        
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("newuser2@mysawit.com");
        when(payload.get("name")).thenReturn("Pengguna Baru Dua");

        when(userRepository.findByEmail("newuser2@mysawit.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("random-encoded-password");
        when(jwtUtil.generateToken("newuser2@mysawit.com")).thenReturn("jwt-token-new-2");

        // WHEN (Mengirim role yang ngawur)
        String result = googleAuthService.verifyAndAuthenticateGoogleToken("valid-token", "HACKER");

        // THEN
        assertEquals("jwt-token-new-2", result);
        // Memastikan fallback role terjadi ke BURUH
        verify(userRepository, times(1)).save(argThat(user -> 
            user.getRole().equals("BURUH")
        ));
    }

    @Test
    void testVerifyAndAuthenticate_InvalidToken_ThrowsException() throws Exception {
        // GIVEN
        doThrow(new RuntimeException("Token tidak valid")).when(googleAuthService).verifyToken("invalid-token");

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> 
            googleAuthService.verifyAndAuthenticateGoogleToken("invalid-token", "BURUH")
        );
        assertEquals("Invalid ID token.", exception.getMessage());
    }

    @Test
    void testVerifyToken_RealMethod_WithMalformedToken_ThrowsException() {
        // Karena metode verifyToken() memanggil Google SDK yang mengurai Base64,
        // menggunakan string sembarang akan memicu Exception (biasanya IllegalArgumentException dari SDK)
        assertThrows(Exception.class, () -> 
            googleAuthService.verifyToken("not-a-valid-jwt-token-string")
        );
    }
}
