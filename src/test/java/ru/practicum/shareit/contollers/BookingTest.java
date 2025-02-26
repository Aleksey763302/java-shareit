package ru.practicum.shareit.contollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.RequestBookingCreate;
import ru.practicum.shareit.item.ItemController;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.practicum.shareit"})
@WebMvcTest(controllers = ItemController.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
@Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class BookingTest {

    private RequestBookingCreate request;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        request = new RequestBookingCreate();
        request.setBookerId(1L);
        request.setItemId(1L);
        request.setStart(LocalDateTime.parse("2026-12-03T10:15:30"));
        request.setEnd(LocalDateTime.parse("2026-12-04T10:15:30"));
    }

    @Test
    @DisplayName("POST /bookings. Корректное создание брони")
    public void createBooking() throws Exception {
        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }
}