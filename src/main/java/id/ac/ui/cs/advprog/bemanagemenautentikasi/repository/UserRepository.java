package id.ac.ui.cs.advprog.bemanagemenautentikasi.repository;


import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}