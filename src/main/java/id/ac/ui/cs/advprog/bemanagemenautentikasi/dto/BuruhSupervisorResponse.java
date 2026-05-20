package id.ac.ui.cs.advprog.bemanagemenautentikasi.dto;

public record BuruhSupervisorResponse(
        Long buruhId,
        String buruhNama,
        Long mandorId,
        String mandorNama,
        boolean active
) {
}
