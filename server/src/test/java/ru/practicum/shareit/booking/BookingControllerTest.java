package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.RequestBookingCreate;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.RequestItemCreate;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.RequestUserCreate;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.practicum.shareit"})
@WebMvcTest(controllers = {ItemController.class, UserController.class, ItemController.class})
@AutoConfigureDataJpa
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class BookingControllerTest {
    private RequestBookingCreate bookingRequest;

    private Map<Long, UserDto> users;
    private Map<Long, ItemDto> items;
    private Map<Long, BookingDto> bookings;

    private List<Long> usersID;
    private List<Long> itemsID;
    private List<Long> bookingsID;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {

        bookingRequest = new RequestBookingCreate();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.parse("2036-12-03T10:15:30"));
        bookingRequest.setEnd(LocalDateTime.parse("2036-12-04T10:15:30"));

        users = createUsers(3);
        usersID = users.keySet().stream().toList();
        items = createItems(users.keySet().stream().toList(), true, 1);
        itemsID = items.keySet().stream().toList();
        bookings = createBookings(itemsID, users.keySet().stream().findFirst().orElseThrow());
        bookingsID = bookings.keySet().stream().toList();
    }


    @Test
    @DisplayName("POST /bookings. Корректное создание брони")
    public void createBookingTest() throws Exception {
        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getFirst())
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /bookings. Создание брони не доступной для аренды вещи")
    public void createBookingFailTest() throws Exception {
        Map<Long, UserDto> users = createUsers(1);
        Map<Long, ItemDto> items = createItems(List.of(users.keySet().stream().toList().getFirst()), false, 1);

        bookingRequest.setItemId(items.keySet().stream().toList().getLast());
        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", usersID.getFirst())
                .content(objectMapper.writeValueAsString(bookingRequest))).andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("GET /bookings/owner. Получение списка аренд владельца вещи")
    public void getBookingByOwnerTest() throws Exception {
        Map<Long, UserDto> users = createUsers(2);
        Map<Long, ItemDto> items = createItems(List.of(users.keySet().stream().toList().getFirst()), true, 2);
        Map<Long, BookingDto> bookings = createBookings(items.keySet().stream().toList(), users.keySet().stream().toList().getLast());
        for (UserDto userDto : users.values()) {
            System.out.println(userDto.getId());
        }

        mockMvc.perform(get("/bookings/owner").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", users.keySet().stream().toList().getFirst())
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /bookings/{bookingID}. Поиск брони по ID")
    public void getBookingTest() throws Exception {

        BookingDto bookingDto = bookings.get(bookingsID.getFirst());

        mockMvc.perform(get("/bookings/" + bookingDto.getId()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getFirst()))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(bookingDto)));
    }

    @Test
    @DisplayName("GET /bookings/{bookingID}. Поиск несуществующей брони по ID")
    public void getBookingFailTest() throws Exception {
        mockMvc.perform(get("/bookings/" + 9823176).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", usersID.getFirst()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /bookings/owner. Получение броней по статусу REJECTED")
    public void getBookingListFromStateRejectedTest() throws Exception {
        Map<Long, UserDto> users = createUsers(2);
        Map<Long, ItemDto> items = createItems(List.of(users.keySet().stream().toList().getFirst()), true, 2);
        Map<Long, BookingDto> bookings = createBookings(items.keySet().stream().toList(),
                users.keySet().stream().toList().getLast());
        for (Long bookingId : bookings.keySet()) {
            approveBooking(bookingId, users.keySet().stream().toList().getFirst(), false);
        }
        createItems(List.of(users.keySet().stream().toList().getFirst()), true, 2);
        createBookings(items.keySet().stream().toList(), users.keySet().stream().toList().getLast());

        mockMvc.perform(get("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", users.keySet().stream().toList().getLast())
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()));
    }

    @Test
    @DisplayName("GET /bookings/owner. Получение броней по статусу FUTURE")
    public void getBookingListFromStateFutureTest() throws Exception {
        Map<Long, UserDto> users = createUsers(2);
        Map<Long, ItemDto> items = createItems(List.of(users.keySet().stream().toList().getFirst()), true, 2);
        Map<Long, BookingDto> bookings = createBookings(items.keySet().stream().toList(),
                users.keySet().stream().toList().getLast());
        for (Long bookingId : bookings.keySet()) {
            approveBooking(bookingId, users.keySet().stream().toList().getFirst(), false);
        }
        createItems(List.of(users.keySet().stream().toList().getFirst()), true, 2);
        createBookings(items.keySet().stream().toList(), users.keySet().stream().toList().getLast());

        mockMvc.perform(get("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", users.keySet().stream().toList().getLast())
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()));
    }

    @Test
    @DisplayName("GET /bookings/owner. Получение броней по статусу CURRENT")
    public void getBookingListFromStateCurrentTest() throws Exception {
        Map<Long, UserDto> users = createUsers(2);
        Map<Long, ItemDto> items = createItems(List.of(users.keySet().stream().toList().getFirst()), true, 2);
        bookingRequest.setStart(LocalDateTime.parse("2016-12-03T10:15:30"));
        bookingRequest.setEnd(LocalDateTime.parse("2037-12-03T10:15:30"));
        Map<Long, BookingDto> bookings = createBookings(items.keySet().stream().toList(),
                users.keySet().stream().toList().getLast());
        for (Long bookingId : bookings.keySet()) {
            approveBooking(bookingId, users.keySet().stream().toList().getFirst(), true);
        }

        mockMvc.perform(get("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", users.keySet().stream().toList().getLast())
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()));
    }

    @Test
    @DisplayName("GET /bookings/owner. Получение броней по статусу PAST")
    public void getBookingListFromStatePastTest() throws Exception {
        Map<Long, UserDto> users = createUsers(2);
        Map<Long, ItemDto> items = createItems(List.of(users.keySet().stream().toList().getFirst()), true, 2);
        bookingRequest.setStart(LocalDateTime.parse("2016-12-03T10:15:30"));
        bookingRequest.setEnd(LocalDateTime.parse("2017-12-03T10:15:30"));
        Map<Long, BookingDto> bookings = createBookings(items.keySet().stream().toList(),
                users.keySet().stream().toList().getLast());

        for (Long bookingId : bookings.keySet()) {
            approveBooking(bookingId, users.keySet().stream().toList().getFirst(), true);
        }

        mockMvc.perform(get("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", users.keySet().stream().toList().getLast())
                        .param("state", "PAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()));
    }

    @Test
    @DisplayName("GET /bookings. Получение списка броней пользователя")
    public void getBookingsTest() throws Exception {
        Map<Long, UserDto> user = createUsers(1);
        long userId = user.keySet().stream().toList().getFirst();
        Map<Long, BookingDto> bookings = createBookings(itemsID, userId);

        Set<BookingDto> expecting = new TreeSet<>(Comparator.comparing(BookingDto::getId));
        expecting.addAll(bookings.values());

        mockMvc.perform(get("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId).param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(expecting)));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingID}. Разрешить аренду как владелец вещи")
    public void approveBookingAsOwnerTest() throws Exception {
        long owner = usersID.getFirst();
        long booker = usersID.getLast();

        Map<Long, ItemDto> userItems = createItems(List.of(owner), true, 1);
        Map<Long, BookingDto> bookingBooker = createBookings(userItems.keySet().stream().toList(), booker);
        long bookingID = bookingBooker.values().stream().toList().getFirst().getId();

        mockMvc.perform(patch("/bookings/" + bookingID)
                        .header("X-Sharer-User-Id", owner)
                        .param("approved", "true"))
                .andExpect(status().isOk());
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

    private String generateRandomText(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomChar = 'a' + random.nextInt('z' - 'a' + 1);
            sb.append((char) randomChar);
        }

        return sb.toString();
    }

    private void approveBooking(long bookingID, long owner, boolean value) throws Exception {
        mockMvc.perform(patch("/bookings/" + bookingID)
                .header("X-Sharer-User-Id", owner)
                .param("approved", String.valueOf(value)));
    }
}