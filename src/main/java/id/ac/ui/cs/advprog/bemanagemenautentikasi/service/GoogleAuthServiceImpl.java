package id.ac.ui.cs.advprog.bemanagemenautentikasi.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Sebaiknya taruh Client ID asli di application.properties
    @Value("${google.client.id:YOUR_GOOGLE_CLIENT_ID}")
    private String googleClientId;

    // Metode dipisahkan agar mudah di-mock (Spy) pada saat Unit Testing
    protected GoogleIdToken verifyToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        return verifier.verify(idTokenString);
    }

    @Override
    public String verifyAndAuthenticateGoogleToken(String idTokenString, String role) {
        try {
            GoogleIdToken idToken = verifyToken(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // Cek apakah user sudah terdaftar di database
                Optional<User> optionalUser = userRepository.findByEmail(email);
                User user;

                if (optionalUser.isEmpty()) {
                    // Jika belum terdaftar, lakukan registrasi otomatis
                    user = new User();
                    user.setEmail(email);
                    user.setUsername(email);
                    user.setNama(name);
                    
                    // Assign random password karena login via Google
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    
                    // Assign role (fallback ke BURUH jika tidak spesifik/invalid)
                    if (role != null && (role.equals("ADMIN") || role.equals("BURUH") || role.equals("MANDOR") || role.equals("SUPIR"))) {
                        user.setRole(role);
                    } else {
                        user.setRole("BURUH"); 
                    }

                    userRepository.save(user);
                } else {
                    user = optionalUser.get();
                }

                // Generate JWT Token dari sistem MySawit kita
                return jwtUtil.generateToken(user.getEmail());
            } else {
                throw new RuntimeException("Invalid ID token.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid ID token.");
        }
    }
}
