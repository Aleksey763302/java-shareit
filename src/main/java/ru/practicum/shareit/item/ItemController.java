package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.dto.RequestItemCreate;
import ru.practicum.shareit.item.dto.RequestItemUpdate;
import ru.practicum.shareit.item.dto.RequestCommentCreate;
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
    public Optional<ItemDto> createItem(@Valid @RequestBody RequestItemCreate request,
                                        @RequestHeader(name = USER_ID_HEADER) Long userId) {
        request.setOwner(userId);
        return itemService.createItem(request);
    }

    @PatchMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ItemDto> updateItem(@RequestBody RequestItemUpdate request,
                                        @RequestHeader(name = USER_ID_HEADER) Long userId,
                                        @PathVariable Long itemId) {
        request.setItemId(itemId);
        request.setOwnerId(userId);
        return itemService.updateItem(request);
    }

    @GetMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ItemWithCommentDto> getItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                           @PathVariable Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<ItemWithCommentDto>> getItemsUser(@RequestHeader(name = USER_ID_HEADER, defaultValue = "") Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<ItemDto>> searchItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                              @RequestParam(defaultValue = "") String text) {
        return itemService.searchItems(text);
    }
    @PostMapping(path = "/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<CommentDto> createComment(@RequestBody RequestCommentCreate request,
                                              @PathVariable Long itemId,
                                              @RequestHeader(name = USER_ID_HEADER) Long userId){
        request.setUserId(userId);
        request.setItemId(itemId);
        return commentService.createComment(request);
    }
}
