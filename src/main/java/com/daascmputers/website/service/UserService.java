package com.daascmputers.website.service;

import com.daascmputers.website.entities.User;

public interface UserService {
    User getUserByEmailOrMobile(String input);
}
