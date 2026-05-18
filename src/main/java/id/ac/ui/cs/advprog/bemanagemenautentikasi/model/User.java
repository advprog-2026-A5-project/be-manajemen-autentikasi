package id.ac.ui.cs.advprog.bemanagemenautentikasi.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String nama;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role; // "ADMIN_UTAMA", "BURUH", "MANDOR", "SUPIR"
    
    @Column(name = "nomor_sertifikasi_mandor")
    private String nomorSertifikasiMandor; // Hanya wajib diisi jika role MANDOR
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mandor_id")
    private User mandor; // Digunakan saat Admin Utama menugaskan (Assign) Buruh ke Mandor
}