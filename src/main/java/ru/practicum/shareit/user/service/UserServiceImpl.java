package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.RequestUserCreate;
import ru.practicum.shareit.user.model.RequestUserUpdate;
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
    public UserDto createUser(RequestUserCreate request) {
        String email = request.getEmail();
        checkEmail(email);
        String name = request.getName();
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        User userFromDB = repository.save(user);
        return mapper.toDto(userFromDB);
    }

    @Override
    @Transactional
    public UserDto updateUser(RequestUserUpdate request, long userId) {
        User saveUser = findUserById(userId);

        if (Objects.nonNull(request.getName())) {
            saveUser.setName(request.getName());
        }
        if (Objects.nonNull(request.getEmail())) {
            if (checkEmail(request.getEmail())) {
                saveUser.setEmail(request.getEmail());
            }
        }
        return mapper.toDto(repository.save(saveUser));
    }

    @Override
    public Optional<UserDto> getUserById(long userId) {
        User user = findUserById(userId);
        return Optional.of(mapper.toDto(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> userList = repository.findAll();
        return userList.stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        if (repository.existsById(userId)) {
            repository.deleteById(userId);
        } else {
            throw new NotFoundUserException("Не найден пользователь для удаления");
        }
    }

    private boolean checkEmail(String email) {
        if (!repository.existsByEmail(email)) {
            return true;
        } else {
            throw new DataAlreadyExistsException("Email уже используется");
        }
    }

    private User findUserById(long userId){
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Пользователь не найден"));
    }
}
