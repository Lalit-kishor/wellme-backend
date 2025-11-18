package com.ultimate.wellme.Repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ultimate.wellme.models.User;
import java.util.List;


public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmail(String email);

    List<User> findByRole(User.Role role); 
}
