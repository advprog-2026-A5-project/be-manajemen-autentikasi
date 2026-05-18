package id.ac.ui.cs.advprog.bemanagemenautentikasi.controller;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private User adminUser;
    private User buruhUser;
    private User mandorUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@mysawit.com");
        adminUser.setRole("ADMIN");

        buruhUser = new User();
        buruhUser.setId(2L);
        buruhUser.setEmail("buruh@mysawit.com");
        buruhUser.setRole("BURUH");

        mandorUser = new User();
        mandorUser.setId(3L);
        mandorUser.setEmail("mandor@mysawit.com");
        mandorUser.setRole("MANDOR");
    }

    @Test
    void testGetAllUsers_WithFilters() {
        // GIVEN
        List<User> mockUsers = Arrays.asList(buruhUser, mandorUser);
        when(userService.getFilteredUsers("budi", null, "BURUH")).thenReturn(mockUsers);

        // WHEN
        ResponseEntity<List<User>> response = userController.getAllUsers("budi", null, "BURUH");

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getFilteredUsers("budi", null, "BURUH");
    }

    @Test
    void testGetUserDetail_Success() {
        // GIVEN
        when(userService.getUserById(2L)).thenReturn(buruhUser);

        // WHEN
        ResponseEntity<?> response = userController.getUserById(2L);

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(buruhUser, response.getBody());
    }

    @Test
    void testAssignBuruhToMandor_Success() {
        // GIVEN
        doNothing().when(userService).assignBuruhToMandor(2L, 3L);

        // WHEN
        ResponseEntity<?> response = userController.assignBuruhToMandor(2L, 3L);

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Buruh berhasil ditugaskan ke Mandor!", body.get("message"));
    }

    @Test
    void testDeleteUser_Success() {
        // GIVEN
        when(authentication.getName()).thenReturn("admin@mysawit.com"); // Email current admin
        when(userService.getUserById(2L)).thenReturn(buruhUser); // Menghapus id 2 (Buruh)
        doNothing().when(userService).deleteUser(2L);

        // WHEN
        ResponseEntity<?> response = userController.deleteUser(2L, authentication);

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(2L);
    }

    @Test
    void testDeleteUser_SelfDeletionFails() {
        // GIVEN: Admin mencoba menghapus dirinya sendiri (ID = 1L)
        when(authentication.getName()).thenReturn("admin@mysawit.com");
        when(userService.getUserById(1L)).thenReturn(adminUser);

        // WHEN
        ResponseEntity<?> response = userController.deleteUser(1L, authentication);

        // THEN
        assertEquals(400, response.getStatusCodeValue());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Admin Utama tidak dapat menghapus dirinya sendiri!", body.get("message"));
        verify(userService, never()).deleteUser(1L);
    }
}
