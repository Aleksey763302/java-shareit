package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.model.RequestItemCreate;
import ru.practicum.shareit.item.model.RequestItemUpdate;
import ru.practicum.shareit.item.model.RequestCommentCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody RequestItemCreate request,
                              @RequestHeader(name = USER_ID_HEADER) long userId) {
        return itemService.createItem(request, userId);
    }

    @PatchMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestBody RequestItemUpdate request,
                              @RequestHeader(name = USER_ID_HEADER) long userId,
                              @PathVariable long itemId) {
        return itemService.updateItem(request, itemId, userId);
    }

    @GetMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ItemWithCommentDto> getItem(@RequestHeader(name = USER_ID_HEADER) long userId,
                                                @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemWithCommentDto> getItemsUser(@RequestHeader(name = USER_ID_HEADER) long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItem(@RequestHeader(name = USER_ID_HEADER) long userId,
                                    @RequestParam(defaultValue = "") String text) {
        return itemService.searchItems(text);
    }

    @PostMapping(path = "/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody RequestCommentCreate body,
                                    @PathVariable long itemId,
                                    @RequestHeader(name = USER_ID_HEADER) long userId) {
        return commentService.createComment(body, userId, itemId);
    }

    @DeleteMapping(path = "/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemById(@PathVariable long itemId,
                               @RequestHeader(name = USER_ID_HEADER) long userId) {
        itemService.deleteItemById(userId, itemId);
    }
}
