package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.RequestUserCreate;
import ru.practicum.shareit.user.dto.RequestUserUpdate;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<UserDto> createUser(@Valid @RequestBody RequestUserCreate request) {
        return service.createUser(request);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<UserDto> updateUser(@Valid @RequestBody RequestUserUpdate request, @PathVariable Long userId) {
        request.setId(userId);
        return service.updateUser(request);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<UserDto> getUser(@PathVariable Long userId) {
        return service.getUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        service.deleteUserById(userId);
    }

}
