package id.ac.ui.cs.advprog.bemanagemenautentikasi.service;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.BuruhSupervisorResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.MandorBuruhAssignmentResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.dto.UserIdentityResponse;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;

import java.util.List;

public interface UserService {
    List<User> getFilteredUsers(String nama, String email, String role);
    User getUserById(Long id);
    void assignBuruhToMandor(Long buruhId, Long mandorId);
    void unassignBuruhFromMandor(Long buruhId);
    void updateUser(Long id, User updateData);
    void deleteUser(Long id);
    UserIdentityResponse getIdentityByEmail(String email);
    UserIdentityResponse getIdentityById(Long id);
    BuruhSupervisorResponse getBuruhSupervisor(Long buruhId);
    List<UserIdentityResponse> getBuruhsByMandor(Long mandorId);
    MandorBuruhAssignmentResponse getMandorBuruhAssignment(Long mandorId, Long buruhId);
}
