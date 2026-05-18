package id.ac.ui.cs.advprog.bemanagemenautentikasi.controller;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {

    @Autowired
    private GoogleAuthService googleAuthService;

    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("idToken");
        String role = payload.get("role");

        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token Google (idToken) harus disertakan."));
        }

        try {
            // Memverifikasi token dan mengambil/membuat user baru
            String jwt = googleAuthService.verifyAndAuthenticateGoogleToken(idToken, role);
            return ResponseEntity.ok(Map.of("token", jwt));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
