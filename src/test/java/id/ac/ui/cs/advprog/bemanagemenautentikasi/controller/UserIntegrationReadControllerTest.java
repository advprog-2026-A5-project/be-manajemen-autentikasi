package id.ac.ui.cs.advprog.bemanagemenautentikasi.controller;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.BuruhSupervisorResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.MandorBuruhAssignmentResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.UserIdentityResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserIntegrationReadControllerTest {

    private static final String INTERNAL_TOKEN = "dev-internal-token";

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @InjectMocks
    private InternalUserReadController internalUserReadController;

    private UserIdentityResponse buruhIdentity;

    @BeforeEach
    void setUp() {
        buruhIdentity = new UserIdentityResponse(2L, "buruh@mysawit.com", "Budi", "BURUH");
        ReflectionTestUtils.setField(internalUserReadController, "internalServiceToken", INTERNAL_TOKEN);
    }

    @Test
    void getCurrentUserIdentityReturnsDtoWithoutPassword() {
        when(authentication.getName()).thenReturn("buruh@mysawit.com");
        when(userService.getIdentityByEmail("buruh@mysawit.com")).thenReturn(buruhIdentity);

        ResponseEntity<UserIdentityResponse> response = userController.getCurrentUserIdentity(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().id());
        assertEquals("BURUH", response.getBody().role());
    }

    @Test
    void getBuruhSupervisorReturnsInactiveWhenNotAssigned() {
        BuruhSupervisorResponse supervisor = new BuruhSupervisorResponse(2L, "Budi", null, null, false);
        when(userService.getBuruhSupervisor(2L)).thenReturn(supervisor);

        ResponseEntity<BuruhSupervisorResponse> response = (ResponseEntity<BuruhSupervisorResponse>) internalUserReadController.getBuruhSupervisor(2L, INTERNAL_TOKEN);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().active());
    }

    @Test
    void getMandorBuruhAssignmentReturnsTrueForValidAssignment() {
        MandorBuruhAssignmentResponse assignment = new MandorBuruhAssignmentResponse(3L, 2L, true);
        when(userService.getMandorBuruhAssignment(3L, 2L)).thenReturn(assignment);

        ResponseEntity<MandorBuruhAssignmentResponse> response = (ResponseEntity<MandorBuruhAssignmentResponse>) internalUserReadController.getMandorBuruhAssignment(3L, 2L, INTERNAL_TOKEN);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().assigned());
    }

    @Test
    void getBuruhsByMandorReturnsOnlyAssignedBuruh() {
        List<UserIdentityResponse> buruhs = List.of(
                new UserIdentityResponse(2L, "buruh1@mysawit.com", "Buruh 1", "BURUH"),
                new UserIdentityResponse(4L, "buruh2@mysawit.com", "Buruh 2", "BURUH")
        );
        when(userService.getBuruhsByMandor(3L)).thenReturn(buruhs);

        ResponseEntity<List<UserIdentityResponse>> response = (ResponseEntity<List<UserIdentityResponse>>) internalUserReadController.getBuruhsByMandor(3L, INTERNAL_TOKEN);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }
}
