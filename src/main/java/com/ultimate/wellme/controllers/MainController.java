package com.ultimate.wellme.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping(value = {"/", "/home", "/index"})
    public String landing() {
        return "landing";
    }

    @RequestMapping("/signup")
    public String signup() {
        return "signup";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/features")
    public String features() {
        return "features";
    }

    @RequestMapping("/contacts")
    public String contacts() {
        return "contacts";
    }

    @RequestMapping("/pricing")
    public String pricing() {
        System.out.println("Accessed pricing page");
        return "pricing";
    }

    @RequestMapping("/forgot-password")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @RequestMapping("/signUpDoctor")
    public String registerDoctor() {
        return "doctorRegistration";
    }

    @RequestMapping("/blogPostList")
    @PreAuthorize("isAuthenticated()")
    public String blogPostList() {
        System.out.println("Accessed blog post list page");
        return "blogPostList";
    }

    @RequestMapping("/catalog-page")
    @PreAuthorize("isAuthenticated()")
    public String catalogPage() {
        System.out.println("Accessed catalog page");
        return "catalog-page";
    }
}