package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.model.RequestItemCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.RequestCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.RequestUserCreate;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.practicum.shareit"})
@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureDataJpa
class ItemRequestControllerTest {
    private RequestCreate request;

    private RequestItemCreate itemCreate;

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
        request = new RequestCreate();
        request.setDescription(generateRandomText(4));
        itemCreate = new RequestItemCreate();
        itemCreate.setName("New Item");
        itemCreate.setDescription("New Item");
        itemCreate.setAvailable(true);

        users = createUsers(3);
        usersID = users.keySet().stream().toList();
        items = createItems(usersID, true, 1);
        itemsID = items.keySet().stream().toList();
    }

    @DisplayName("POST /requests. Создание запроса")
    @Test
    void createRequest() throws Exception {
        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getLast())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(request.getDescription()));
    }

    @DisplayName("GET /requests. Поиск запросов пользователя")
    @Test
    void getRequests() throws Exception {
        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", usersID.getLast())
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/requests").contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", usersID.getLast())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @DisplayName("GET /requests/ALL. Поиск всех запросов")
    @Test
    void getRequestsAll() throws Exception {
        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", usersID.getLast())
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/requests").contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", usersID.getLast())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @DisplayName("POST /requests/{requestId} . Поиск запроса по ID")
    @Test
    void getRequestById() throws Exception {
        List<Long> id = new ArrayList<>();
        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", usersID.getLast())
                .content(objectMapper.writeValueAsString(request))).andDo(result -> {
            ItemRequestDto requestDto = objectMapper.readValue(result.getResponse().getContentAsString(), ItemRequestDto.class);
            id.add(requestDto.getId());
        });

        mockMvc.perform(get("/requests/" + id.getFirst()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getLast()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(request.getDescription()));
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