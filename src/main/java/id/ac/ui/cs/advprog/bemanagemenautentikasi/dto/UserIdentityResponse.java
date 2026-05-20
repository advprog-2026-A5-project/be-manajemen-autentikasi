package id.ac.ui.cs.advprog.bemanagemenautentikasi.dto;

public record UserIdentityResponse(
        Long id,
        String email,
        String nama,
        String role
) {
}
