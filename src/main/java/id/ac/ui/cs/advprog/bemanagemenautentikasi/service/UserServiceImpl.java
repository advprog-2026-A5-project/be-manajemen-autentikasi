package id.ac.ui.cs.advprog.bemanagemenautentikasi.service;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getFilteredUsers(String nama, String email, String role) {
        // Menggunakan findAll dan mem-filter di Java (untuk MVP). 
        // Secara ideal menggunakan custom query JPA atau Specification untuk performa.
        return userRepository.findAll().stream()
                .filter(u -> nama == null || u.getNama().toLowerCase().contains(nama.toLowerCase()))
                .filter(u -> email == null || u.getEmail().toLowerCase().contains(email.toLowerCase()))
                .filter(u -> role == null || u.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan!"));
    }

    @Override
    public void assignBuruhToMandor(Long buruhId, Long mandorId) {
        User buruh = getUserById(buruhId);
        User mandor = getUserById(mandorId);

        if (!"BURUH".equalsIgnoreCase(buruh.getRole())) {
            throw new RuntimeException("User yang di-assign harus ber-role BURUH!");
        }
        if (!"MANDOR".equalsIgnoreCase(mandor.getRole())) {
            throw new RuntimeException("Tujuan assign harus ke seorang MANDOR!");
        }

        buruh.setMandor(mandor);
        userRepository.save(buruh);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
