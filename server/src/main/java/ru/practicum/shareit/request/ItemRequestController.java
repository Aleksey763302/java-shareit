package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.RequestCreate;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService service;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody RequestCreate request,
                                        @RequestHeader(USER_ID_HEADER) long userId) {
        return service.createRequest(request, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return service.getAllRequestsByUserId(userId);
    }

    @GetMapping(path = "/all")
    public List<ItemRequestDto> getRequestsAll(@RequestHeader(USER_ID_HEADER) long userId) {
        return service.getAllRequests();
    }

    @GetMapping(path = "/{requestId}")
    public Optional<ItemRequestDto> getRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                                   @PathVariable long requestId) {
        return service.findRequestById(requestId);
    }

}
