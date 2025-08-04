package com.daascmputers.website.serviceimpl;

import com.daascmputers.website.entities.User;
import com.daascmputers.website.repository.UserRepository;
import com.daascmputers.website.service.OtpService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor


public class OtpServiceImpl implements OtpService {
    @Qualifier("virtualThreadExecutor")        // Inject the named executor
    private final ExecutorService virtualExecu;

    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final String fromPhone = "+1 484 673 7949";


    private static class OtpEntry {
        String otp;
        long expiryTime;

        OtpEntry(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    @Override
    @Async
    public CompletableFuture<Void> sendOtp(String input) {
       return CompletableFuture.runAsync(() -> {
            try {
                String otp = String.valueOf(new Random().nextInt(900000) + 100000);
                otpStorage.put(input, new OtpEntry(otp, System.currentTimeMillis() + 300_000)); // 5-minute expiry

                if (input.contains("@")) {
                    userRepository.findByEmail(input)
                            .orElseGet(() -> userRepository.save(User.builder().email(input).build()));
                    SimpleMailMessage msg = new SimpleMailMessage();
                    msg.setFrom("tamild17042003@gmail.com");
                    msg.setTo(input);
                    msg.setSubject("OTP FROM DAAS");
                    msg.setText("your otp: " + otp);

                    mailSender.send(msg);
                } else {
                    userRepository.findByMobile(input)
                            .orElseGet(() -> userRepository.save(User.builder().mobile(input).build()));

                    Message.creator(
                            new PhoneNumber("+91" + input), // You can adjust the country code as needed
                            new PhoneNumber(fromPhone),
                            "Your OTP is: " + otp
                    ).create();
                }

                log.info("Sent OTP {} to {}", otp, input);
            } catch (Exception e) {
                log.error("failed to snd otp to: {}", input);
            }
        },virtualExecu);
    }

    @Override
    public boolean verifyOtp(String input, String otp) {
        OtpEntry entry = otpStorage.get(input);
        if (entry == null || System.currentTimeMillis() > entry.expiryTime) {
            otpStorage.remove(input);
            return false;
        }
        boolean valid = otp.equals(entry.otp);
        if (valid) {
            otpStorage.remove(input);
        }
        return valid;
    }
}
