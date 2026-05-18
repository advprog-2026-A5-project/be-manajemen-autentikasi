package id.ac.ui.cs.advprog.bemanagemenautentikasi.service;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User buruh;
    private User mandor;

    @BeforeEach
    void setUp() {
        buruh = new User();
        buruh.setId(1L);
        buruh.setNama("Agus Buruh");
        buruh.setEmail("agus@mysawit.com");
        buruh.setRole("BURUH");

        mandor = new User();
        mandor.setId(2L);
        mandor.setNama("Budi Mandor");
        mandor.setEmail("budi@mysawit.com");
        mandor.setRole("MANDOR");
    }

    @Test
    void testGetFilteredUsers_Success() {
        // GIVEN
        when(userRepository.findAll()).thenReturn(Arrays.asList(buruh, mandor));

        // WHEN: Mencari dengan filter role = "BURUH"
        List<User> result = userService.getFilteredUsers(null, null, "BURUH");

        // THEN
        assertEquals(1, result.size());
        assertEquals("Agus Buruh", result.get(0).getNama());
    }

    @Test
    void testGetUserById_Success() {
        // GIVEN
        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));

        // WHEN
        User result = userService.getUserById(1L);

        // THEN
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserById_NotFound() {
        // GIVEN
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
        assertEquals("User tidak ditemukan!", exception.getMessage());
    }

    @Test
    void testAssignBuruhToMandor_Success() {
        // GIVEN
        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mandor));

        // WHEN
        userService.assignBuruhToMandor(1L, 2L);

        // THEN
        assertEquals(mandor, buruh.getMandor());
        verify(userRepository, times(1)).save(buruh);
    }

    @Test
    void testAssignBuruhToMandor_ThrowsException_IfTargetNotMandor() {
        // GIVEN: Target assign adalah sesama buruh, bukan mandor
        User bukanMandor = new User();
        bukanMandor.setId(3L);
        bukanMandor.setRole("SUPIR");

        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));
        when(userRepository.findById(3L)).thenReturn(Optional.of(bukanMandor));

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> userService.assignBuruhToMandor(1L, 3L));
        assertEquals("Tujuan assign harus ke seorang MANDOR!", exception.getMessage());
        
        // Memastikan save tidak pernah dipanggil karena gagal validasi
        verify(userRepository, never()).save(any(User.class));
    }
}
