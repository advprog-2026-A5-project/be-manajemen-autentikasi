package id.ac.ui.cs.advprog.bemanagemenautentikasi.controller;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.MandorBuruhAssignmentResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.UserIdentityResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal")
public class InternalUserReadController {

    @SuppressWarnings("EI_EXPOSE_REP2")
    private final UserService userService;

    public InternalUserReadController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}/identity")
    public ResponseEntity<UserIdentityResponse> getInternalUserIdentity(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getIdentityById(id));
    }

    @GetMapping("/buruh/{buruhId}/supervisor")
    public ResponseEntity<?> getBuruhSupervisor(@PathVariable Long buruhId) {
        try {
            return ResponseEntity.ok(userService.getBuruhSupervisor(buruhId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/mandors/{mandorId}/buruh")
    public ResponseEntity<?> getBuruhsByMandor(@PathVariable Long mandorId) {
        try {
            return ResponseEntity.ok(userService.getBuruhsByMandor(mandorId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/mandors/{mandorId}/buruh/{buruhId}/assignment")
    public ResponseEntity<?> getMandorBuruhAssignment(@PathVariable Long mandorId, @PathVariable Long buruhId) {
        try {
            MandorBuruhAssignmentResponse response = userService.getMandorBuruhAssignment(mandorId, buruhId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
