package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.Validation;
import ru.practicum.shareit.util.exceptions.NotFoundUserException;
import ru.practicum.shareit.util.exceptions.NotValidParamException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<ItemDto> createItem(@RequestBody ItemDto item,
                                        @RequestHeader(name = "X-Sharer-User-Id", defaultValue = "") String userId) {
        if (userId.isBlank()) {
            throw new NotValidParamException("The X-Sharer-User-Id parameter must be specified");
        }
        long ownerId = Validation.validUserId(userId);
        if (userService.getUserById(ownerId).isEmpty()) {
            throw new NotFoundUserException("User not found");
        }
        Validation.validItem(item);
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("{itemId}")
    public Optional<ItemDto> updateItem(@RequestBody ItemDto itemUpdate,
                                        @RequestHeader("X-Sharer-User-Id") String userId,
                                        @PathVariable String itemId) {
        long user = Validation.validUserId(userId);
        long item = Validation.validItemId(itemId);
        return itemService.updateItem(itemUpdate, user, item);
    }

    @GetMapping("{itemId}")
    public Optional<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") String userId,
                                     @PathVariable String itemId) {
        long user = Validation.validUserId(userId);
        long item = Validation.validItemId(itemId);
        return itemService.getItemById(user, item);
    }

    @GetMapping
    public Optional<List<ItemDto>> getItemsUser(@RequestHeader("X-Sharer-User-Id") String userId) {
        long user = Validation.validUserId(userId);
        return itemService.getItemsByUserId(user);
    }

    @GetMapping("/search")
    public Optional<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") String userId,
                                              @RequestParam(required = false) String text) {
        Validation.validText(text);
        return itemService.searchItems(text);
    }
}
