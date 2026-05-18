package id.ac.ui.cs.advprog.bemanagemenautentikasi.service;

import id.ac.ui.cs.advprog.bemanagemenautentikasi.model.User;
import id.ac.ui.cs.advprog.bemanagemenautentikasi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Spring Security menggunakan email sebagai unique identifier (username)
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}