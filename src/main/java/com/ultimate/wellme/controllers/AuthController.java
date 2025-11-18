package com.ultimate.wellme.controllers;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ultimate.wellme.DTO.DoctorRegistrationDTO;
import com.ultimate.wellme.models.CloudinaryUploadResult;
import com.ultimate.wellme.models.Doctor;
import com.ultimate.wellme.models.Patient;
import com.ultimate.wellme.models.User;
import com.ultimate.wellme.services.AppService;
import com.ultimate.wellme.services.CloudinaryService;
import com.ultimate.wellme.services.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Autowired
    private AppService appService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    public AuthenticationManager authenticationManager;

    @Autowired
    public JwtService jwtService;

    @PostMapping("/signup")
    public String signup(@RequestParam String email, @RequestParam String password, @RequestParam String password_repeat) {
        if (!password.equals(password_repeat)) {
            return "redirect:/signup?error=passwords_do_not_match";
        }
        // Proceed with signup logic
        User user = new Patient();

        user.setEmail(email);
        user.setPassword(password);
        user.setRole(User.Role.valueOf("PATIENT"));
        appService.saveUser(user);
        
        return "redirect:/login?success=registered";
    }



    @PostMapping("/signUpDoctor")
    public String registerDoctor(@ModelAttribute DoctorRegistrationDTO dto) {

        List<String> specializationList = Arrays.stream(dto.getSpecializations().split(",")).map(String::trim).collect(Collectors.toList());
        List<String> languageList = Arrays.stream(dto.getLanguages().split(",")).map(String::trim).collect(Collectors.toList());

        User user = new Doctor();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(User.Role.DOCTOR);
        ((Doctor) user).setFirstName(dto.getFirstName());
        ((Doctor) user).setLastName(dto.getLastName());
        ((Doctor) user).setMedicalLicenseNumber(dto.getMedicalLicenseNumber());
        ((Doctor) user).setSpecializations(specializationList);
        ((Doctor) user).setClinicAddress(dto.getClinicAddress());
        ((Doctor) user).setYearsOfExperience(dto.getYearsOfExperience());
        ((Doctor) user).setGender(dto.getGender());
        ((Doctor) user).setLanguages(languageList);
        ((Doctor) user).setConsultationFee(dto.getConsultationFee());

        // upload image to Cloudinary
        try {
            MultipartFile profilePicture = dto.getProfilePicture();
            
            if (profilePicture != null && !profilePicture.isEmpty()) {
                CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(profilePicture);
                user.setProfileImage(uploadResult);
            } else {
                System.out.println("No profile picture uploaded or file is empty");
            }
        } catch (Exception e) {
            System.out.println("Image upload failed 🥱");
            e.printStackTrace();
        }

        appService.saveUser(user);

        return "redirect:/login?success=registered";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {

        try {
            
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, password));

            if (authentication.isAuthenticated()) {
                String jwtToken = jwtService.generateToken(email);

                Cookie cookie = new Cookie("jwt", jwtToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                cookie.setPath("/");
                response.addCookie(cookie);

                return jwtToken;
            }

        } catch (Exception e) {
            return e.getMessage();
        }
        

        return "Login Failed";
    }
}
