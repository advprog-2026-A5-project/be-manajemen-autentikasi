package id.ac.ui.cs.advprog.bemanagemenautentikasi.service;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;

import java.util.List;

public interface UserService {
    List<User> getFilteredUsers(String nama, String email, String role);
    User getUserById(Long id);
    void assignBuruhToMandor(Long buruhId, Long mandorId);
    void updateUser(Long id, User updateData);
    void deleteUser(Long id);
}
