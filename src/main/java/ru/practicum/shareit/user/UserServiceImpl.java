package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.util.exceptions.NotFoundUserException;
import ru.practicum.shareit.util.exceptions.ValidateException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final List<User> users;
    private Long maxId = 1L;

    @Override
    public Optional<User> createUser(User user) {
        String email = user.getEmail();
        if (checkEmail(email)) {
            user.setId(maxId);
            maxId++;
            users.add(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> updateUser(User user, Long userId) {
        Optional<User> optionalUser = getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь не найден");
        }
        User saveUser = optionalUser.get();

        users.remove(saveUser);
        if (Objects.nonNull(user.getName())) {
            saveUser.setName(user.getName());
        }
        if (Objects.nonNull(user.getEmail())) {
            if (checkEmail(user.getEmail())) {
                saveUser.setEmail(user.getEmail());
            }
        }
        users.add(saveUser);
        return Optional.of(saveUser);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return users.stream()
                .filter(user -> Objects.equals(user.getId(), userId)).findFirst();
    }

    @Override
    public void deleteUserById(Long userId) {
        Optional<User> userOptional = getUserById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundUserException("Не найден пользователь для удаления");
        }
        users.remove(userOptional.get());
    }

    private boolean checkEmail(String email) {
        if (users.stream().filter(user -> Objects.equals(user.getEmail(), email))
                .toList().isEmpty()) {
            return true;
        } else {
            throw new ValidateException("Email уже используется");
        }
    }
}
