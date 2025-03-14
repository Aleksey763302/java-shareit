package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;

import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserShort;
import ru.practicum.shareit.util.exceptions.BookingApprovedException;
import ru.practicum.shareit.util.exceptions.NotFoundItemException;
import ru.practicum.shareit.util.exceptions.AccessDeniedException;
import ru.practicum.shareit.util.exceptions.NotFoundUserException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService, CommentService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public ItemDto createItem(RequestItemCreate request, long ownerId) {
        Item item = new Item();
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundUserException("Пользователь не найден"));
        item.setOwner(user);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        return itemMapper.itemToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(RequestItemUpdate request, long itemId, long ownerId) {
        Item item = findItemById(itemId);
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Доступность предмета для бронирования может быть изменена владельцем");
        }
        if (Objects.nonNull(request.getName())) {
            item.setName(request.getName());
        }
        if (Objects.nonNull(request.getDescription())) {
            item.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getAvailable())) {
            item.setAvailable(request.getAvailable());
        }
        return itemMapper.itemToItemDto(itemRepository.save(item));
    }

    @Override
    public Optional<ItemWithCommentDto> getItemById(long userId, long itemId) {
        Set<Long> idList = new HashSet<>();
        idList.add(itemId);
        LocalDateTime current = LocalDateTime.now();
        Item item = findItemById(itemId);
        BookingDto lastBooking = null;
        BookingDto nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findNextBooking(idList, current)
                    .stream().findFirst().map(bookingMapper::toDto).orElse(null);
            nextBooking = bookingRepository.findLostBooking(idList, current)
                    .stream().findFirst().map(bookingMapper::toDto).orElse(null);
        }

        Map<Long, List<Comment>> comments = commentRepository.findCommentsGroupedByItem(idList);

        ItemWithCommentDto itemWithCommentDto = itemMapper.itemToItemCommentDto(item);
        itemWithCommentDto.setLastBooking(lastBooking);
        itemWithCommentDto.setNextBooking(nextBooking);
        if (!comments.isEmpty()) {
            itemWithCommentDto.setComments(comments.get(item.getId())
                    .stream()
                    .map(itemMapper::commentToCommentDto)
                    .toList());
        } else {
            itemWithCommentDto.setComments(List.of());
        }
        return Optional.of(itemWithCommentDto);
    }

    @Override
    public List<ItemWithCommentDto> getItemsByUserId(long userId) {
        LocalDateTime current = LocalDateTime.now();
        Map<Long, Item> items = itemRepository.findItemsGroupedById(userId);
        if (items.isEmpty()) {
            throw new NotFoundItemException("У пользователя нет предметов");
        }

        Map<Long, List<Comment>> comments = commentRepository.findCommentsGroupedByItem(items.keySet());

        List<ItemWithCommentDto> response = items.values()
                .stream()
                .map(itemMapper::itemToItemCommentDto)
                .peek(itemWithComment -> {
                    if (!comments.isEmpty()) {
                        List<Comment> commentsList = comments.get(itemWithComment.getId());
                        itemWithComment.setComments(commentsList.stream()
                                .map(itemMapper::commentToCommentDto)
                                .toList());
                    } else {
                        itemWithComment.setComments(List.of());
                    }
                })
                .toList();

        if (items.values().stream().allMatch(item -> item.getOwner().getId().equals(userId))) {
            Map<Long, Booking> lastBookingMap = findLostBookingGroupedByItem(items.keySet(), current);
            Map<Long, Booking> nextBookingMap = findNextBookingGroupedByItem(items.keySet(), current);
            response = response.stream()
                    .peek(itemComment -> {
                        itemComment.setLastBooking(bookingMapper
                                .toDto(lastBookingMap.get(itemComment.getId())));
                        itemComment.setNextBooking(bookingMapper
                                .toDto(nextBookingMap.get(itemComment.getId())));
                    })
                    .toList();
        }

        return response;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameContainingIgnoreCase(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::itemToItemDto).toList();
    }

    @Override
    @Transactional
    public void deleteItemById(long userId, long itemId) {
        Item item = findItemById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Удалить предмет может только владелец");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(RequestCommentCreate request, long userId, long itemId) {
        LocalDateTime time = LocalDateTime.now();
        Booking booking = bookingRepository.findByBookerIdAndItemId(userId, itemId);
        if (Objects.isNull(booking)) {
            throw new AccessDeniedException("Коментарии могут оставлять только пользователи бронировавшие этот предмет");
        }
        boolean isApproved = booking.getStatus().equals(Status.APPROVED);
        boolean isEndTime = booking.getEnd().isBefore(time);
        if (isApproved && isEndTime) {
            Comment comment = new Comment();
            comment.setItem(booking.getItem());
            comment.setUser(booking.getBooker());
            comment.setText(request.getText());
            comment.setCreated(time);
            UserShort authorName = userRepository.findUserShortById(userId);
            comment = commentRepository.save(comment);
            CommentDto response = itemMapper.commentToCommentDto(comment);
            response.setAuthorName(authorName.getName());
            return response;
        } else {
            throw new BookingApprovedException("Владелец не разрешил арендовать товар");
        }
    }

    private Map<Long, Booking> findNextBookingGroupedByItem(Set<Long> itemId, LocalDateTime current) {
        return bookingRepository.findNextBooking(itemId, current).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
    }

    private Map<Long, Booking> findLostBookingGroupedByItem(Set<Long> itemId, LocalDateTime current) {
        return bookingRepository.findLostBooking(itemId, current).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException("Предмет не найден"));
    }
}
