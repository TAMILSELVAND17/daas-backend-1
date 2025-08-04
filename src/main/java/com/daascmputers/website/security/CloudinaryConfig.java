package com.daascmputers.website.security;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {
//    @Value("${cloudinary.cloud-name}")
    private  String cloudName="dgxk7ot2e";

//    @Value("${cloudinary.api-key}")
    private  String apiKey="169538859451858";

//    @Value("${cloudinary.api-secret}")
    private  String apiSecret="wI7KExyW6GBPnlCAqd-xzyoNFrc";

    @Bean(name = "cloudinary")
    public Cloudinary cloudinary(){
        if (cloudName == null || apiKey == null || apiSecret == null) {
            throw new IllegalStateException("Cloudinary configuration is incomplete: " +
                    "cloud_name=" + cloudName + ", api_key=" + apiKey + ", api_secret=" + (apiSecret == null ? "null" : "set"));
        }
        Map<String,String> config= ObjectUtils.asMap("cloud_name",cloudName,
                "api_key",apiKey,
                "api_secret",apiSecret);
        return new Cloudinary(config);
    }
}
