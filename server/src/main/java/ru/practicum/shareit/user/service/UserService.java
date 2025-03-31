package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.RequestUserCreate;
import ru.practicum.shareit.user.model.RequestUserUpdate;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(RequestUserCreate request);

    UserDto updateUser(RequestUserUpdate request, long userId);

    Optional<UserDto> getUserById(long userId);

    List<UserDto> getAllUsers();

    void deleteUserById(long userId);
}
