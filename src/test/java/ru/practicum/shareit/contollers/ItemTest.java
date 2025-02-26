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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.RequestItemCreate;
import ru.practicum.shareit.item.dto.RequestItemUpdate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.practicum.shareit"})
@WebMvcTest(controllers = ItemController.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
@Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ItemTest {
    private RequestItemCreate item;
    private RequestItemUpdate itemUpdate;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        item = new RequestItemCreate();
        item.setName("drill");
        item.setDescription("electric drill");
        item.setAvailable(true);

        itemUpdate = new RequestItemUpdate();
        itemUpdate.setName("drill");
        itemUpdate.setDescription("electric drill");
        itemUpdate.setAvailable(true);
    }

    @DisplayName("POST /users. Добавление корректного предмета")
    @Test
    void createItem() throws Exception {
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .content(objectMapper.writeValueAsString(item))).andExpect(status().isCreated());
    }

    @DisplayName("POST /users. Добавление предмета без загаловка X-Sharer-User-Id")
    @Test
    void createItemWithoutUserId() throws Exception {
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item))).andExpect(status().isBadRequest());
    }

    @DisplayName("PATCH /items. Корректное обновление предмета")
    @Test
    void updateItem() throws Exception {
        mockMvc.perform(patch("/items/2").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(itemUpdate))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemUpdate.getName()))
                .andExpect(jsonPath("$.description").value(itemUpdate.getDescription()));
    }

    @DisplayName("PATCH /items. Обновление несуществующего предмета")
    @Test
    void updateItemIncorrectItemId() throws Exception {
        mockMvc.perform(patch("/items/99999").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .content(objectMapper.writeValueAsString(item))).andExpect(status().isNotFound());
    }

    @DisplayName("PATCH /items. Обновление предмета не владельцем")
    @Test
    void updateItemByUnknownUser() throws Exception {
        item.setName("Update name");
        item.setDescription("Update description");

        mockMvc.perform(patch("/items/2").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .content(objectMapper.writeValueAsString(item))).andExpect(status().isForbidden());
    }

    @DisplayName("GET /items. Получение предмета по id")
    @Test
    void getItem() throws Exception {
        mockMvc.perform(get("/items/4").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Палатка"));
    }

    @DisplayName("GET /items/search. Поиск предмета без названия")
    @Test
    void searchItemIncorrectText() throws Exception {
        mockMvc.perform(get("/items/search").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)).andExpect(jsonPath("$.length()").value(0));
    }

}
