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

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.RequestItemCreate;
import ru.practicum.shareit.item.dto.RequestItemUpdate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.RequestCommentCreate;
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
    public Optional<ItemDto> createItem(RequestItemCreate request) {
        Item item = new Item();
        Optional<User> userOptional = userRepository.findById(request.getOwner());
        if (userOptional.isEmpty()) {
            throw new NotFoundUserException("User not found");
        }
        item.setOwner(userOptional.get());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        return Optional.of(itemMapper.itemToItemDto(itemRepository.save(item)));
    }

    @Override
    @Transactional
    public Optional<ItemDto> updateItem(RequestItemUpdate request) {
        Optional<Item> itemOptional = itemRepository.findById(request.getItemId());
        if (itemOptional.isEmpty()) {
            throw new NotFoundItemException("Не найден предмет для обновления");
        }
        Item saveItem = itemOptional.get();
        if (!saveItem.getOwner().getId().equals(request.getOwnerId())) {
            throw new AccessDeniedException("Доступность предмета для бронирования может быть изменена владельцем");
        }
        if (Objects.nonNull(request.getName())) {
            saveItem.setName(request.getName());
        }
        if (Objects.nonNull(request.getDescription())) {
            saveItem.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getAvailable())) {
            saveItem.setAvailable(request.getAvailable());
        }
        return Optional.of(itemMapper.itemToItemDto(itemRepository.save(saveItem)));
    }

    @Override
    @Transactional
    public Optional<ItemWithCommentDto> getItemById(Long userId, Long itemId) {
        Set<Long> idList = new HashSet<>();
        idList.add(itemId);
        LocalDateTime current = LocalDateTime.now();
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        BookingDto lastBooking = null;
        BookingDto nextBooking = null;
        if (itemOptional.isPresent()) {
            if (itemOptional.get().getOwner().getId().equals(userId)) {
                lastBooking = bookingMapper.bookingToBookingDto(bookingRepository
                        .findNextBooking(idList, current).stream().findFirst().orElseThrow());
                nextBooking = bookingMapper.bookingToBookingDto(bookingRepository
                        .findLostBooking(idList, current).stream().findFirst().orElseThrow());
            }
        } else {
            throw new NotFoundItemException("Предмет не найден");
        }
        Item item = itemOptional.get();

        Map<Long, List<Comment>> commentsMap = commentRepository.findCommentsGroupedByItem(idList);

        ItemWithCommentDto itemWithCommentDto = itemMapper.itemToItemCommentDto(item);
        itemWithCommentDto.setLastBooking(lastBooking);
        itemWithCommentDto.setNextBooking(nextBooking);
        if (!commentsMap.isEmpty()) {
            itemWithCommentDto.setComments(commentsMap.get(item.getId())
                    .stream()
                    .map(itemMapper::commentToCommentDto)
                    .toList());
        } else {
            itemWithCommentDto.setComments(List.of());
        }
        return Optional.of(itemWithCommentDto);
    }

    @Override
    @Transactional
    public Optional<List<ItemWithCommentDto>> getItemsByUserId(Long userId) {
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
            Map<Long, Booking> lastBookingMap = bookingRepository
                    .findLostBookingGroupedByItem(items.keySet(), current);
            Map<Long, Booking> nextBookingMap = bookingRepository
                    .findNextBookingGroupedByItem(items.keySet(), current);
            response = response.stream()
                    .peek(itemComment -> {
                        itemComment.setLastBooking(bookingMapper
                                .bookingToBookingDto(lastBookingMap.get(itemComment.getId())));
                        itemComment.setNextBooking(bookingMapper
                                .bookingToBookingDto(nextBookingMap.get(itemComment.getId())));
                    })
                    .toList();
        }

        return Optional.of(response);
    }

    @Override
    @Transactional
    public Optional<List<ItemDto>> searchItems(String text) {
        if (text.isBlank()) {
            return Optional.of(List.of());
        }
        List<ItemDto> response = itemRepository.findByNameContainingIgnoreCase(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::itemToItemDto).toList();
        return Optional.of(response);
    }

    @Override
    @Transactional
    public void deleteItemById(Long userId, Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(userId);
        if (itemOptional.isEmpty()) {
            throw new NotFoundItemException("Предмет не найден для удаления");
        }
        Item item = itemOptional.get();
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Удалить предмет может только владелец");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public Optional<CommentDto> createComment(RequestCommentCreate request) {
        LocalDateTime time = LocalDateTime.now();
        Booking booking = bookingRepository.findByBookerIdAndItemId(request.getUserId(), request.getItemId());
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
            UserShort authorName = userRepository.findUserShortById(request.getUserId());
            comment = commentRepository.save(comment);
            CommentDto response = itemMapper.commentToCommentDto(comment);
            response.setAuthorName(authorName.getName());
            return Optional.of(response);
        } else {
            throw new BookingApprovedException("Владелец не разрешил арендовать товар");
        }
    }
}
