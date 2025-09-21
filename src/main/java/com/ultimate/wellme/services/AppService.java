package com.ultimate.wellme.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ultimate.wellme.Repos.UserRepo;
import com.ultimate.wellme.models.User;
import com.ultimate.wellme.models.UserPrincipal;

@Service
public class AppService implements UserDetailsService{

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(username);
        if(user != null){
            return new UserPrincipal(user);
        }

        throw new UsernameNotFoundException("User not found");
    }

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public User saveUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }
    
}
