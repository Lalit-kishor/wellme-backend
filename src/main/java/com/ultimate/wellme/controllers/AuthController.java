package com.ultimate.wellme.controllers;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ultimate.wellme.DTO.DoctorRegistrationDTO;
import com.ultimate.wellme.DTO.LoginRequest;
import com.ultimate.wellme.DTO.SignUpRequest;
import com.ultimate.wellme.config.ApiResponse;
import com.ultimate.wellme.models.CloudinaryUploadResult;
import com.ultimate.wellme.models.Doctor;
import com.ultimate.wellme.models.Patient;
import com.ultimate.wellme.models.User;
import com.ultimate.wellme.services.AppService;
import com.ultimate.wellme.services.CloudinaryService;
import com.ultimate.wellme.services.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
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
    public ResponseEntity<?> signup(@RequestBody SignUpRequest credentials) {
       
        // Proceed with signup logic
        User user = new Patient();

        user.setEmail(credentials.getEmail());
        user.setPassword(credentials.getPassword());
        user.setRole(User.Role.valueOf("PATIENT"));
        appService.saveUser(user);
        
        return new ResponseEntity<ApiResponse>(new ApiResponse(true, "SignUp success"), HttpStatus.OK);
    }

    @PostMapping("/signUpDoctor")
    public ResponseEntity<?> registerDoctor(@ModelAttribute DoctorRegistrationDTO dto) {

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
        ((Doctor) user).setCity(dto.getCity());
        System.out.println("city: " + dto.getCity());
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
            return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Image Upload failed"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        appService.saveUser(user);

        return new ResponseEntity<ApiResponse>(new ApiResponse(true, "Doctor registered successfully."), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest credentials, HttpServletResponse response) {

        try {
            
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword()));

            if (authentication.isAuthenticated()) {
                String jwtToken = jwtService.generateToken(credentials.getEmail());


                Cookie cookie = new Cookie("jwt", jwtToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                cookie.setPath("/");
                response.addCookie(cookie);

                return new ResponseEntity<ApiResponse>(new ApiResponse(true, "Login success"), HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Exception occured during Login= " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Login failed"), HttpStatus.UNAUTHORIZED);
    }
}
