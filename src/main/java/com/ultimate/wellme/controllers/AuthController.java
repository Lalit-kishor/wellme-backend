package com.ultimate.wellme.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ultimate.wellme.models.Doctor;
import com.ultimate.wellme.models.Patient;
import com.ultimate.wellme.models.User;
import com.ultimate.wellme.services.AppService;

@Controller
public class AuthController {

    @Autowired
    private AppService appService;

    @PostMapping("/signup")
    public String signup(@RequestParam String email, @RequestParam String password, @RequestParam String password_repeat, @RequestParam String role) {
        if (!password.equals(password_repeat)) {
            return "redirect:/signup?error=passwords_do_not_match";
        }
        // Proceed with signup logic
        User user;
        System.out.println("Role selected: " + role);

        if(role.equals("DOCTOR")) {
            user = new Doctor();
        } else {
            user = new Patient();
        }

        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        appService.saveUser(user);
        
        return "redirect:/signup?success";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, @RequestParam String role) {
        return "redirect:/login?success";
    }
}
