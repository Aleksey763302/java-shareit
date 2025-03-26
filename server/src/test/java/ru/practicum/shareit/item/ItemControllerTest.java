package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.RequestBookingCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.RequestCommentCreate;
import ru.practicum.shareit.item.model.RequestItemCreate;
import ru.practicum.shareit.item.model.RequestItemUpdate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.RequestCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.RequestUserCreate;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.practicum.shareit"})
@WebMvcTest(controllers = ItemController.class)
@AutoConfigureDataJpa
class ItemControllerTest {
    private RequestItemCreate itemCreate;
    private RequestItemUpdate itemUpdate;

    private Map<Long, UserDto> users;
    private Map<Long, ItemDto> items;

    private List<Long> usersID;
    private List<Long> itemsID;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        itemCreate = new RequestItemCreate();
        itemCreate.setName("New Item");
        itemCreate.setDescription("New Item");
        itemCreate.setAvailable(true);

        itemUpdate = new RequestItemUpdate();
        itemUpdate.setName("Update Item");
        itemUpdate.setDescription("Update item");
        itemUpdate.setAvailable(true);

        users = createUsers(3);
        usersID = users.keySet().stream().toList();
        items = createItems(usersID, true, 1);
        itemsID = items.keySet().stream().toList();
    }

    @DisplayName("POST /items. Коректное создание предмета")
    @Test
    void createItemTest() throws Exception {
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getLast())
                        .content(objectMapper.writeValueAsString(itemCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(itemCreate.getName()))
                .andExpect(jsonPath("$.description").value(itemCreate.getDescription()))
                .andExpect(jsonPath("$.available").value(itemCreate.getAvailable()));
    }

    @DisplayName("POST /items. Коректное создание предмета в ответ на запрос")
    @Test
    void createItemForRequestTest() throws Exception {
        RequestCreate request = new RequestCreate();
        request.setDescription(generateRandomText(4));
        List<Long> requestID = new ArrayList<>();
        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", usersID.getLast())
                .content(objectMapper.writeValueAsString(request))).andDo(result -> {
            ItemRequestDto itemRequestDto = objectMapper.readValue(result.getResponse().getContentAsString(), ItemRequestDto.class);
            requestID.add(itemRequestDto.getId());
        });


        itemCreate.setRequestId(requestID.getFirst());

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getFirst())
                        .content(objectMapper.writeValueAsString(itemCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(itemCreate.getName()))
                .andExpect(jsonPath("$.description").value(itemCreate.getDescription()))
                .andExpect(jsonPath("$.available").value(itemCreate.getAvailable()));
    }


    @DisplayName("PATCH /items/{itemId}. Корректное обновление предмета")
    @Test
    void updateItemTest() throws Exception {
        Map<Long, UserDto> users = createUsers(1);
        long ownerID = users.keySet().stream().toList().getFirst();
        Map<Long, ItemDto> items = createItems(List.of(ownerID), true, 1);

        mockMvc.perform(patch("/items/" + items.keySet().stream().toList().getFirst()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerID)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemUpdate.getName()))
                .andExpect(jsonPath("$.description").value(itemUpdate.getDescription()));
    }

    @DisplayName("PATCH /items. Обновление предмета не владельцем")
    @Test
    void updateItemByUnknownUser() throws Exception {
        itemUpdate.setName(itemUpdate.getName() + generateRandomText(4));

        Map<Long, UserDto> otherUser = createUsers(1);

        mockMvc.perform(patch("/items/" + itemsID.getFirst()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", otherUser.keySet().stream().toList().getFirst())
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("GET /items/{itemId}. Получение предмета по id")
    @Test
    void getItemTest() throws Exception {
        mockMvc.perform(get("/items/" + itemsID.getFirst()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getFirst()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(items.get(itemsID.getFirst()).getName()));
    }

    @DisplayName("GET /items. Получение всех предметов пользователя")
    @Test
    void getItemsTest() throws Exception {
        Map<Long, UserDto> users = createUsers(1);
        long ownerID = users.keySet().stream().toList().getFirst();
        Map<Long, ItemDto> items = createItems(List.of(ownerID), true, 3);

        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(items.size()));
    }

    @DisplayName("GET /items. Получение несуществующих предметов пользователя")
    @Test
    void getItemsByUserIdTest() throws Exception {
        List<Long> userID = new ArrayList<>();
        RequestUserCreate userRequest = new RequestUserCreate();
        userRequest.setName(generateRandomText(7));
        userRequest.setEmail(generateRandomText(10) + "@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))).andDo(result -> {
            UserDto user = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
            userID.add(user.getId());
        });

        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID.getFirst()))
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET /items. Получение предметов пользователя c коментариями")
    @Test
    void getItemsUserWithCommentTest() throws Exception {
        Map<Long, UserDto> users = createUsers(2);
        long ownerID = users.keySet().stream().toList().getFirst();
        long otherUser = users.keySet().stream().toList().getLast();
        Map<Long, ItemDto> items = createItems(List.of(ownerID), true, 3);
        Map<Long, BookingDto> bookingDto = createBookings(items.keySet().stream().toList(), otherUser);

        approveBooking(bookingDto.keySet().stream().toList(), ownerID, true);
        RequestCommentCreate commentCreate = new RequestCommentCreate();
        commentCreate.setText(generateRandomText(8));

        mockMvc.perform(post("/items/" + items.values().stream().toList().getFirst().getId() + "/comment")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(commentCreate))
                .header("X-Sharer-User-Id", otherUser));

        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(items.size()));
    }

    @DisplayName("POST /items/{itemId}/comment. Создание коментария")
    @Test
    void createCommentTest() throws Exception {
        Map<Long, UserDto> users = createUsers(2);
        long ownerID = users.keySet().stream().toList().getFirst();
        long otherUser = users.keySet().stream().toList().getLast();
        Map<Long, ItemDto> items = createItems(List.of(ownerID), true, 3);
        Map<Long, BookingDto> bookingDto = createBookings(items.keySet().stream().toList(), otherUser);

        approveBooking(bookingDto.keySet().stream().toList(), ownerID, true);
        RequestCommentCreate commentCreate = new RequestCommentCreate();
        commentCreate.setText(generateRandomText(8));

        mockMvc.perform(post("/items/" + items.values().stream().toList().getFirst().getId() + "/comment")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(commentCreate))
                        .header("X-Sharer-User-Id", otherUser))
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .contains(objectMapper.writeValueAsString(commentCreate.getText())));
    }

    @DisplayName("POST /items/{itemId}/comment. Создание коментария для не одобренной аренды")
    @Test
    void createCommentFailApproveTest() throws Exception {
        Map<Long, UserDto> users = createUsers(2);
        long ownerID = users.keySet().stream().toList().getFirst();
        long otherUser = users.keySet().stream().toList().getLast();
        Map<Long, ItemDto> items = createItems(List.of(ownerID), true, 3);
        Map<Long, BookingDto> bookingDto = createBookings(items.keySet().stream().toList(), otherUser);

        approveBooking(bookingDto.keySet().stream().toList(), ownerID, false);
        RequestCommentCreate commentCreate = new RequestCommentCreate();
        commentCreate.setText(generateRandomText(8));

        mockMvc.perform(post("/items/" + items.values().stream().toList().getFirst().getId() + "/comment")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(commentCreate))
                        .header("X-Sharer-User-Id", otherUser))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /items/{itemId} Поиск предмета по id с коментариями")
    @Test
    void getItemWithCommentTest() throws Exception {
        Map<Long, UserDto> users = createUsers(2);
        long ownerID = users.keySet().stream().toList().getFirst();
        long otherUser = users.keySet().stream().toList().getLast();
        Map<Long, ItemDto> items = createItems(List.of(ownerID), true, 3);
        Map<Long, BookingDto> bookingDto = createBookings(items.keySet().stream().toList(), otherUser);

        approveBooking(bookingDto.keySet().stream().toList(), ownerID, true);
        RequestCommentCreate commentCreate = new RequestCommentCreate();
        commentCreate.setText(generateRandomText(8));

        mockMvc.perform(post("/items/" + items.values().stream().toList().getFirst().getId() + "/comment")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(commentCreate))
                .header("X-Sharer-User-Id", otherUser));

        mockMvc.perform(get("/items/" + items.values().stream().toList().getFirst().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", otherUser))
                .andExpect(status().isOk());

    }

    @DisplayName("GET /items/search. Поиск предмета без названия")
    @Test
    void searchItemIncorrectText() throws Exception {
        mockMvc.perform(get("/items/search").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getFirst()))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @DisplayName("GET /items. Поиск предмета по названию")
    @Test
    void searchItemTest() throws Exception {
        String text = generateRandomText(10);
        List<ItemDto> items = new ArrayList<>();
        itemCreate.setName(text);
        itemCreate.setDescription(generateRandomText(50));
        itemCreate.setAvailable(true);
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getLast())
                        .content(objectMapper.writeValueAsString(itemCreate)))
                .andDo(result -> {
                    ItemDto item = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
                    items.add(item);
                });

        mockMvc.perform(get("/items/search").contentType(MediaType.APPLICATION_JSON)
                        .param("text", text)
                        .header("X-Sharer-User-Id", usersID.getFirst()))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(items)));
    }

    @DisplayName("DELETE /items/{itemId} Удаление предмета по id")
    @Test
    void deleteItemByIdTest() throws Exception {
        Map<Long, UserDto> user = createUsers(1);
        Map<Long, ItemDto> item = createItems(user.keySet().stream().toList(), true, 1);
        mockMvc.perform(delete("/items/" + item.keySet().stream().toList().getFirst()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.keySet().stream().toList()))
                .andExpect(status().isNoContent());
    }

    @DisplayName("DELETE /items/{itemId} Удаление предмета по id не владельцем")
    @Test
    void deleteItemByIdNoOwnerTest() throws Exception {
        Map<Long, UserDto> user = createUsers(1);
        Map<Long, ItemDto> item = createItems(user.keySet().stream().toList(), true, 1);
        mockMvc.perform(delete("/items/" + item.keySet().stream().toList().getFirst()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getFirst()))
                .andExpect(status().isForbidden());
    }

    private Map<Long, UserDto> createUsers(int count) throws Exception {
        RequestUserCreate userRequest = new RequestUserCreate();

        Map<Long, UserDto> users = new HashMap<>();
        for (int i = 0; i < count; i++) {
            userRequest.setName(generateRandomText(7));
            userRequest.setEmail(generateRandomText(10) + "@Gmail.com");
            mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest))).andDo(result -> {
                UserDto user = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
                users.put(user.getId(), user);
            });
        }
        return users;
    }

    private Map<Long, ItemDto> createItems(List<Long> usersId, boolean available, int count) throws Exception {
        RequestItemCreate itemRequest = new RequestItemCreate();

        Map<Long, ItemDto> items = new HashMap<>();
        for (int i = 0; i < count; i++) {
            for (Long userID : usersId) {
                itemRequest.setName(generateRandomText(10));
                itemRequest.setDescription(generateRandomText(50));
                itemRequest.setAvailable(available);
                mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID)
                        .content(objectMapper.writeValueAsString(itemRequest))).andDo(result -> {
                    ItemDto item = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
                    items.put(item.getId(), item);
                });
            }
        }

        return items;
    }

    private Map<Long, BookingDto> createBookings(List<Long> itemsId, Long userId) throws Exception {
        RequestBookingCreate bookingRequest = new RequestBookingCreate();
        bookingRequest.setStart(LocalDateTime.parse(LocalDateTime.now().toString()));
        bookingRequest.setEnd(LocalDateTime.parse(LocalDateTime.now().toString()));

        Map<Long, BookingDto> bookings = new HashMap<>();
        for (Long itemID : itemsId) {
            bookingRequest.setItemId(itemID);
            mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", userId)
                    .content(objectMapper.writeValueAsString(bookingRequest))).andDo(result -> {
                BookingDto booking = objectMapper.readValue(result.getResponse().getContentAsString(), BookingDto.class);
                bookings.put(booking.getId(), booking);
            });
        }
        return bookings;
    }

    private void approveBooking(List<Long> bookingID, long owner, boolean value) throws Exception {
        for (Long id : bookingID) {
            mockMvc.perform(patch("/bookings/" + id)
                    .header("X-Sharer-User-Id", owner)
                    .param("approved", String.valueOf(value)));
        }
    }

    private String generateRandomText(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomChar = 'a' + random.nextInt('z' - 'a' + 1);
            sb.append((char) randomChar);
        }

        return sb.toString();
    }

}