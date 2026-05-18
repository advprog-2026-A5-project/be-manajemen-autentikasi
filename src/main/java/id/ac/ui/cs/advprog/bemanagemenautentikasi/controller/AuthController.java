package id.ac.ui.cs.advprog.bemanagemenautentikasi.controller;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        // Menggunakan Email untuk Autentikasi
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(Map.of("token", jwt));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Validasi Duplikasi Username
        String usernameToRegister = user.getUsername() != null ? user.getUsername() : user.getEmail();
        if (userRepository.existsByUsername(usernameToRegister)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Username is already in use!"));
        }

        // Validasi Duplikasi Email
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Email is already in use!"));
        }
        
        // Validasi Role (Tolak ADMIN, hanya boleh BURUH, MANDOR, SUPIR)
        String role = user.getRole();
        if ("ADMIN".equals(role)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Role is not valid! Registrasi ADMIN tidak diizinkan."));
        }
        if (role == null || !(role.equals("BURUH") || role.equals("MANDOR") || role.equals("SUPIR"))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Role is not valid!"));
        }

        // Validasi Khusus Mandor: Nomor Sertifikasi Mandor wajib diisi
        if ("MANDOR".equalsIgnoreCase(role)) {
            if (user.getNomorSertifikasiMandor() == null || user.getNomorSertifikasiMandor().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Error: Nomor Sertifikasi Mandor wajib diisi!"));
            }
        }

        // Create new user's account
        User newUser = new User();
        newUser.setUsername(usernameToRegister);
        newUser.setEmail(user.getEmail());
        newUser.setNama(user.getNama());
        newUser.setPassword(encoder.encode(user.getPassword()));
        newUser.setRole(user.getRole());
        
        // Cek jika Mandor, maka assign nomor sertifikasinya juga
        if ("MANDOR".equalsIgnoreCase(user.getRole())) {
            newUser.setNomorSertifikasiMandor(user.getNomorSertifikasiMandor());
        }
        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        // Karena sistem ini menggunakan Stateless JWT, session dihancurkan di sisi frontend (menghapus token).
        // Endpoint ini disediakan murni sebagai respons HTTP 200 OK standar bagi frontend untuk merespons klik tombol "Logout".
        return ResponseEntity.ok(Map.of("message", "Log out berhasil!"));
    }
}