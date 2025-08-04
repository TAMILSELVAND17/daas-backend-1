package com.daascmputers.website.controller;


import com.daascmputers.website.entities.User;
import com.daascmputers.website.service.JwtService;
import com.daascmputers.website.service.OtpService;
import com.daascmputers.website.service.UserService;
import com.daascmputers.website.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
public class AuthController {
    @Autowired
    private RestResponseBuilder restResponseBuilder;
    @Autowired
    private final OtpService otpService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private UserService userService;


    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        if (input == null || input.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(restResponseBuilder.error(HttpStatus.BAD_REQUEST, "Input is required", null));
        }
        otpService.sendOtp(input);
        return ResponseEntity.ok(restResponseBuilder.success(HttpStatus.OK, "OTP sent to " + input, input));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        String otp = request.get("otp");

        if (otpService.verifyOtp(input, otp)) {
            User user = userService.getUserByEmailOrMobile(input);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
            String subject = (user.getMobile() != null && !user.getMobile().isBlank())
                    ? user.getMobile()
                    : user.getEmail();

            String token = jwtService.generateToken(subject, user.getId());


            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId()+"");

            return restResponseBuilder.success(HttpStatus.OK, "Login successful!", data);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }
    }
}