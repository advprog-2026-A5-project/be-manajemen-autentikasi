package id.ac.ui.cs.advprog.bemanagemenautentikasi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserHasMinimalRequiredFields() {
        user.setUsername("budi_buruh");
        user.setEmail("budi@mysawit.com");
        user.setNama("Budi Santoso");
        user.setPassword("rahasia123");
        user.setRole("BURUH");

        assertEquals("budi_buruh", user.getUsername());
        assertEquals("budi@mysawit.com", user.getEmail());
        assertEquals("Budi Santoso", user.getNama());
        assertEquals("rahasia123", user.getPassword());
        assertEquals("BURUH", user.getRole());
    }

    @Test
    void testMandorCanHaveNomorSertifikasi() {
        User mandor = new User();
        mandor.setRole("MANDOR");
        mandor.setNama("Mandor Jaja");
        mandor.setNomorSertifikasiMandor("MDR-2026-XYZ");

        assertEquals("MANDOR", mandor.getRole());
        assertEquals("MDR-2026-XYZ", mandor.getNomorSertifikasiMandor());
    }

    @Test
    void testBuruhCanBeAssignedToMandor() {
        User mandor = new User();
        mandor.setRole("MANDOR");
        mandor.setNama("Mandor Jaja");

        User buruh = new User();
        buruh.setRole("BURUH");
        buruh.setNama("Budi Santoso");
        buruh.setMandor(mandor); // Assigment Buruh ke Mandor

        assertNotNull(buruh.getMandor());
        assertEquals("Mandor Jaja", buruh.getMandor().getNama());
    }
}
