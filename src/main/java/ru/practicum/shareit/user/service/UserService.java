package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.RequestUserCreate;
import ru.practicum.shareit.user.dto.RequestUserUpdate;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> createUser(RequestUserCreate request);

    Optional<UserDto> updateUser(RequestUserUpdate request);

    Optional<UserDto> getUserById(Long userId);

    List<UserDto> getAllUsers();

    void deleteUserById(Long userId);
}
