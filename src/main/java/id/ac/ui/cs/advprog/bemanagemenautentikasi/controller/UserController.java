package id.ac.ui.cs.advprog.bemanagemenautentikasi.controller;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Mendapatkan daftar pengguna dengan filter opsional (nama, email, role)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Hanya Admin Utama yang bisa melihat seluruh daftar pengguna
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String nama,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.getFilteredUsers(nama, email, role));
    }

    // Mendapatkan detail profil spesifik pengguna
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Assign / Reassign Buruh ke Mandor
    @PostMapping("/{buruhId}/assign-mandor/{mandorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignBuruhToMandor(@PathVariable Long buruhId, @PathVariable Long mandorId) {
        userService.assignBuruhToMandor(buruhId, mandorId);
        return ResponseEntity.ok(Map.of("message", "Buruh berhasil ditugaskan ke Mandor!"));
    }

    // Hapus pengguna
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        User userToDelete = userService.getUserById(id);
        
        // Validasi Admin Utama tidak dapat menghapus dirinya sendiri
        if (userToDelete.getEmail().equals(authentication.getName())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Admin Utama tidak dapat menghapus dirinya sendiri!"));
        }

        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User berhasil dihapus!"));
    }

    // Update profil pengguna
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updateData) {
        try {
            userService.updateUser(id, updateData);
            return ResponseEntity.ok(Map.of("message", "User berhasil diperbarui!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
