package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.RequestUserCreate;
import ru.practicum.shareit.user.dto.RequestUserUpdate;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exceptions.DataAlreadyExistsException;
import ru.practicum.shareit.util.exceptions.NotFoundUserException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final BookingRepository bookingRepository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public Optional<UserDto> createUser(RequestUserCreate request) {
        String email = request.getEmail();
        String name = request.getName();
        User user = new User();
        if (checkEmail(email)) {
            user.setName(name);
            user.setEmail(email);
            User userFromDB = repository.save(user);
            UserDto userDto = mapper.userToUserDto(userFromDB);
            return Optional.of(userDto);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<UserDto> updateUser(RequestUserUpdate request) {
        User saveUser = repository.findById(request.getId()).orElseThrow(() -> new NotFoundUserException("User not found"));

        if (Objects.nonNull(request.getName())) {
            saveUser.setName(request.getName());
        }
        if (Objects.nonNull(request.getEmail())) {
            if (checkEmail(request.getEmail())) {
                saveUser.setEmail(request.getEmail());
            }
        }
        return Optional.of(mapper.userToUserDto(repository.save(saveUser)));
    }

    @Override
    @Transactional
    public Optional<UserDto> getUserById(Long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundUserException("Пользователь не найден"));
        return Optional.of(mapper.userToUserDto(user));
    }

    @Override
    @Transactional
    public List<UserDto> getAllUsers() {
        List<User> userList = repository.findAll();
        return userList.stream().map(mapper::userToUserDto).toList();
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        if (repository.existsById(userId)) {
            repository.deleteById(userId);
        } else {
            throw new NotFoundUserException("Не найден пользователь для удаления");
        }
    }

    private boolean checkEmail(String email) {
        if (repository.findByEmailContainingIgnoreCase(email).isEmpty()) {
            return true;
        } else {
            throw new DataAlreadyExistsException("Email уже используется");
        }
    }
}
