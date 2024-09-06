package net.javaguides.identity_service.service;

import net.javaguides.identity_service.dto.UserDto;

public interface UserService {
    UserDto getUserByUsername(String username);
}
