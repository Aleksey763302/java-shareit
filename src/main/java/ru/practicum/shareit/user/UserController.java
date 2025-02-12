package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.util.Validation;

import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public Optional<User> createUser(@RequestBody User user) {
        Validation.validEmail(user.getEmail());
        return service.createUser(user);
    }

    @PatchMapping("/{userId}")
    public Optional<User> updateUser(@RequestBody User user, @PathVariable String userId) {
        long id = Validation.validUserId(userId);
        return service.updateUser(user, id);
    }

    @GetMapping("/{userId}")
    public Optional<User> getUser(@PathVariable String userId) {
        long id = Validation.validUserId(userId);
        return service.getUserById(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable String userId) {
        long id = Validation.validUserId(userId);
        service.deleteUserById(id);
    }

}
