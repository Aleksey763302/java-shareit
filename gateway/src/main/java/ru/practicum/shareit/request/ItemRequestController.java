package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemCreate;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestClient service;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody RequestItemCreate request,
                                                @RequestHeader(USER_ID_HEADER) long userId) {
        return service.createRequest(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return service.getAllRequestsByUserId(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getRequestsAll(@RequestHeader(USER_ID_HEADER) long userId) {
        return service.getAllRequests();
    }

    @GetMapping(path = "/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @PathVariable long requestId) {
        return service.findRequestById(requestId, userId);
    }

}
