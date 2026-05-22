package id.ac.ui.cs.advprog.bemanagemenautentikasi.controller;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.MandorBuruhAssignmentResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.UserIdentityResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal")
public class InternalUserReadController {

    @SuppressWarnings("EI_EXPOSE_REP2")
    private final UserService userService;

    @Value("${app.internal-service-token:dev-internal-token}")
    private String internalServiceToken;

    public InternalUserReadController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}/identity")
    public ResponseEntity<UserIdentityResponse> getInternalUserIdentity(
            @PathVariable Long id,
            @RequestHeader(name = "X-Internal-Service-Token", required = false) String token
    ) {
        if (!isValidInternalToken(token)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(userService.getIdentityById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserIdentityResponse>> getInternalUsers(
            @RequestHeader(name = "X-Internal-Service-Token", required = false) String token,
            @RequestParam(required = false) String nama,
            @RequestParam(required = false) String role
    ) {
        if (!isValidInternalToken(token)) {
            return ResponseEntity.status(401).build();
        }

        List<UserIdentityResponse> users = userService.getFilteredUsers(nama, null, role).stream()
                .map(user -> new UserIdentityResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getNama(),
                        user.getRole()))
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/buruh/{buruhId}/supervisor")
    public ResponseEntity<?> getBuruhSupervisor(
            @PathVariable Long buruhId,
            @RequestHeader(name = "X-Internal-Service-Token", required = false) String token
    ) {
        if (!isValidInternalToken(token)) {
            return ResponseEntity.status(401).build();
        }

        try {
            return ResponseEntity.ok(userService.getBuruhSupervisor(buruhId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/mandors/{mandorId}/buruh")
    public ResponseEntity<?> getBuruhsByMandor(
            @PathVariable Long mandorId,
            @RequestHeader(name = "X-Internal-Service-Token", required = false) String token
    ) {
        if (!isValidInternalToken(token)) {
            return ResponseEntity.status(401).build();
        }

        try {
            return ResponseEntity.ok(userService.getBuruhsByMandor(mandorId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/mandors/{mandorId}/buruh/{buruhId}/assignment")
    public ResponseEntity<?> getMandorBuruhAssignment(
            @PathVariable Long mandorId,
            @PathVariable Long buruhId,
            @RequestHeader(name = "X-Internal-Service-Token", required = false) String token
    ) {
        if (!isValidInternalToken(token)) {
            return ResponseEntity.status(401).build();
        }

        try {
            MandorBuruhAssignmentResponse response = userService.getMandorBuruhAssignment(mandorId, buruhId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    private boolean isValidInternalToken(String token) {
        return internalServiceToken != null
                && !internalServiceToken.isBlank()
                && internalServiceToken.equals(token);
    }
}
