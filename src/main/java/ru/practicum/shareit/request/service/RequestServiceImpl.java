package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestItemCreate;
import ru.practicum.shareit.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper mapper;

    @Override
    @Transactional
    public ItemRequestDto createRequest(RequestItemCreate request, long userId) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestorId(userId);
        itemRequest.setDescription(request.getDescription());
        return mapper.toDto(requestRepository.save(itemRequest));
    }

    @Override
    public Optional<ItemRequestDto> findRequestById(long requestId) {
        return requestRepository.findById(requestId).map(mapper::toDto);
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        return requestRepository.findAllRequests()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUserId(long userId) {
        return requestRepository.findByRequestorId(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
