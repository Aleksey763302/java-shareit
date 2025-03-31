package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.request.RequestItemCreate;
import ru.practicum.shareit.item.dto.request.RequestCommentCreate;
import ru.practicum.shareit.item.dto.request.RequestItemUpdate;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@Valid @RequestBody RequestItemCreate request,
                                             @RequestHeader(name = USER_ID_HEADER) long userId) {
        return itemClient.createItem(request, userId);
    }

    @PatchMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@RequestBody RequestItemUpdate request,
                                             @RequestHeader(name = USER_ID_HEADER) long userId,
                                             @PathVariable long itemId) {
        return itemClient.updateItem(request, itemId, userId);
    }

    @GetMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItem(@RequestHeader(name = USER_ID_HEADER) long userId,
                                          @PathVariable long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemsUser(@RequestHeader(name = USER_ID_HEADER) long userId) {
        return itemClient.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchItem(@RequestHeader(name = USER_ID_HEADER) long userId,
                                             @RequestParam(defaultValue = "") String text) {
        return itemClient.searchItems(text, userId);
    }

    @PostMapping(path = "/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createComment(@RequestBody RequestCommentCreate body,
                                                @PathVariable long itemId,
                                                @RequestHeader(name = USER_ID_HEADER) long userId) {
        return itemClient.createComment(body, userId, itemId);
    }
}
