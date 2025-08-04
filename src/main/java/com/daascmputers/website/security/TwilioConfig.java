package com.daascmputers.website.security;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

//    @Value("${twilio.account-sid}")
//    private String accountSid;
//
//    @Value("${twilio.auth-token}")
//    private String authToken;

    private String accountSid="AC428582a231e2e7e107b402744e8cea0b";


    private String authToken="28dd54e60b6702b81ae9d54d7312381a";

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }
}