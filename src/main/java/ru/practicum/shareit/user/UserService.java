package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserService {
    Optional<User> createUser(User user);

    Optional<User> updateUser(User user, Long userId);

    Optional<User> getUserById(Long userId);

    void deleteUserById(Long userId);
}
