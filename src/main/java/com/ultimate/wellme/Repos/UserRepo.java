package com.ultimate.wellme.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ultimate.wellme.models.User;
import java.util.List;


public interface UserRepo extends JpaRepository<User, Long> {

    @Query("SELECT u FROM PARENT u WHERE u.email= :email" + 
            " UNION ALL " +
            "SELECT u FROM DOCTOR u WHERE u.email= :email"
    )
    User findByEmail(@Param("email") String email);   // The custom query has been added because User is an abstract entity due to which the persister(a hibernate component) becomes confused which entity to point patient or doctor and remains null

    List<User> findByRole(User.Role role); 
}
