package com.daascmputers.website.serviceimpl;

import com.daascmputers.website.entities.User;
import com.daascmputers.website.repository.UserRepository;
import com.daascmputers.website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public User getUserByEmailOrMobile(String input) {
        Optional<User> userOpt;

        if (input.contains("@")) {
            userOpt = userRepository.findByEmail(input);
        } else {
            userOpt = userRepository.findByMobile(input);
        }

        return userOpt.orElse(null);
    }
}
