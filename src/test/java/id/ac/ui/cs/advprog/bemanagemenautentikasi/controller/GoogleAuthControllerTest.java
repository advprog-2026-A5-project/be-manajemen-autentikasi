package id.ac.ui.cs.advprog.bemanagemenautentikasi.controller;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.service.GoogleAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthControllerTest {

    @Mock
    private GoogleAuthService googleAuthService;

    @InjectMocks
    private GoogleAuthController googleAuthController;

    @Test
    void testGoogleAuth_Success() {
        // GIVEN: Client mengirimkan idToken dari Google
        Map<String, String> payload = new HashMap<>();
        payload.put("idToken", "valid.google.token.abc");
        
        // Asumsi: jika registrasi, role dikirimkan; jika sudah ada, role diabaikan.
        payload.put("role", "BURUH"); 

        when(googleAuthService.verifyAndAuthenticateGoogleToken("valid.google.token.abc", "BURUH"))
                .thenReturn("dummy-jwt-token-from-google");

        // WHEN: Endpoint auth/google di-hit
        ResponseEntity<?> response = googleAuthController.googleAuth(payload);

        // THEN: Harus kembali status 200 OK dengan token sistem MySawit
        assertEquals(200, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("dummy-jwt-token-from-google", body.get("token"));

        verify(googleAuthService, times(1)).verifyAndAuthenticateGoogleToken("valid.google.token.abc", "BURUH");
    }

    @Test
    void testGoogleAuth_InvalidToken() {
        // GIVEN: Client mengirimkan token yang tidak valid
        Map<String, String> payload = new HashMap<>();
        payload.put("idToken", "invalid.token");

        when(googleAuthService.verifyAndAuthenticateGoogleToken(anyString(), any()))
                .thenThrow(new RuntimeException("Invalid ID token."));

        // WHEN
        ResponseEntity<?> response = googleAuthController.googleAuth(payload);

        // THEN: Harus mereturn Bad Request (400)
        assertEquals(400, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Invalid ID token.", body.get("message"));
    }
}
