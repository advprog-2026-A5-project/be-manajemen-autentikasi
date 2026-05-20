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
        assertEquals("Agus Buruh", result.getFirst().getNama());
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

    @Test
    void testUpdateUser_Success() {
        // GIVEN
        User updateData = new User();
        updateData.setNama("Agus Updated");
        updateData.setEmail("agus.new@mysawit.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));
        when(userRepository.existsByEmail("agus.new@mysawit.com")).thenReturn(false);

        // WHEN
        userService.updateUser(1L, updateData);

        // THEN
        assertEquals("Agus Updated", buruh.getNama());
        assertEquals("agus.new@mysawit.com", buruh.getEmail());
        verify(userRepository, times(1)).save(buruh);
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        // GIVEN
        User updateData = new User();
        updateData.setEmail("budi@mysawit.com"); // Email milik mandor

        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));
        // Mensimulasikan email sudah terpakai oleh user ID lain
        when(userRepository.existsByEmail("budi@mysawit.com")).thenReturn(true);

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updateData));
        assertEquals("Email sudah digunakan oleh pengguna lain!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUnassignBuruhFromMandor_Success() {
        // GIVEN: Buruh sudah memiliki mandor
        buruh.setMandor(mandor);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));

        // WHEN
        userService.unassignBuruhFromMandor(1L);

        // THEN: Mandor diset null dan disave
        assertNull(buruh.getMandor());
        verify(userRepository, times(1)).save(buruh);
    }

    @Test
    void testUnassignBuruhFromMandor_ThrowsException_IfUserNotBuruh() {
        // GIVEN: Target bukan buruh tapi Mandor
        when(userRepository.findById(2L)).thenReturn(Optional.of(mandor));

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> userService.unassignBuruhFromMandor(2L));
        assertEquals("User yang dicopot harus ber-role BURUH!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // GIVEN
        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));

        // WHEN
        userService.deleteUser(1L);

        // THEN
        verify(userRepository, times(1)).delete(buruh);
    }

    @Test
    void testAssignBuruhToMandor_ThrowsException_IfBuruhNotRoleBuruh() {
        // GIVEN: Target buruh ternyata memiliki role SUPIR
        User bukanBuruh = new User();
        bukanBuruh.setId(4L);
        bukanBuruh.setRole("SUPIR");

        when(userRepository.findById(4L)).thenReturn(Optional.of(bukanBuruh));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mandor));

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> userService.assignBuruhToMandor(4L, 2L));
        assertEquals("User yang di-assign harus ber-role BURUH!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_WithRoleAndSertifikasiMandor() {
        // GIVEN
        User updateData = new User();
        updateData.setRole("MANDOR");
        updateData.setNomorSertifikasiMandor("MANDOR-999");

        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));

        // WHEN
        userService.updateUser(1L, updateData);

        // THEN
        assertEquals("MANDOR", buruh.getRole());
        assertEquals("MANDOR-999", buruh.getNomorSertifikasiMandor());
        verify(userRepository, times(1)).save(buruh);
    }

    @Test
    void testGetIdentityByEmail_Success() {
        when(userRepository.findByEmail("agus@mysawit.com")).thenReturn(Optional.of(buruh));

        var result = userService.getIdentityByEmail("agus@mysawit.com");

        assertEquals(1L, result.id());
        assertEquals("agus@mysawit.com", result.email());
        assertEquals("BURUH", result.role());
    }

    @Test
    void testGetBuruhSupervisor_ReturnsAssignedMandor() {
        buruh.setMandor(mandor);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));

        var result = userService.getBuruhSupervisor(1L);

        assertEquals(1L, result.buruhId());
        assertEquals(2L, result.mandorId());
        assertEquals(true, result.active());
    }

    @Test
    void testGetMandorBuruhAssignment_FalseWhenDifferentMandor() {
        User otherMandor = new User();
        otherMandor.setId(9L);
        otherMandor.setRole("MANDOR");
        buruh.setMandor(otherMandor);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buruh));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mandor));

        var result = userService.getMandorBuruhAssignment(2L, 1L);

        assertEquals(false, result.assigned());
    }

    @Test
    void testGetBuruhsByMandor_ReturnsOnlyAssignedBuruh() {
        User buruh2 = new User();
        buruh2.setId(10L);
        buruh2.setNama("Buruh 2");
        buruh2.setEmail("buruh2@mysawit.com");
        buruh2.setRole("BURUH");
        buruh.setMandor(mandor);
        buruh2.setMandor(mandor);

        when(userRepository.findById(2L)).thenReturn(Optional.of(mandor));
        when(userRepository.findByMandor_Id(2L)).thenReturn(List.of(buruh, buruh2));

        var result = userService.getBuruhsByMandor(2L);

        assertEquals(2, result.size());
        assertEquals("BURUH", result.getFirst().role());
    }
}
