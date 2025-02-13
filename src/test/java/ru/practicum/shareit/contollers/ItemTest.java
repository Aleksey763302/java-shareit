package ru.practicum.shareit.contollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.practicum.shareit"})
@WebMvcTest(controllers = ItemController.class)
public class ItemTest {
    private ItemDto item;
    private User user;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Vasiliy");

        item = new ItemDto();
        item.setName("drill");
        item.setDescription("electric drill");
        item.setAvailable(true);
    }

    @DisplayName("POST /users. Добавление корректного предмета")
    @Test
    void createItem() throws Exception {
        user.setEmail("vAsyAn123@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
        });

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", user.getId())
                .content(objectMapper.writeValueAsString(item))).andExpect(status().isCreated());
    }

    @DisplayName("POST /users. Добавление предмета без загаловка X-Sharer-User-Id")
    @Test
    void createItemWithoutUserId() throws Exception {
        user.setEmail("vasyan123@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
        });

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item))).andExpect(status().isBadRequest());
    }

    @DisplayName("PATCH /items. Корректное обновление предмета")
    @Test
    void updateItem() throws Exception {
        user.setEmail("vasyan221@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
        });
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", user.getId())
                .content(objectMapper.writeValueAsString(item))).andDo(result -> {
            ItemDto itemFromDb = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
            item.setId(itemFromDb.getId());
        });

        item.setName("Update name");
        item.setDescription("Update description");
        mockMvc.perform(patch("/items/" + item.getId()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .content(objectMapper.writeValueAsString(item))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));
    }

    @DisplayName("PATCH /items. Обновление несуществующего предмета")
    @Test
    void updateItemIncorrectItemId() throws Exception {
        user.setEmail("qwerty@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
        });

        item.setName("Update name");
        item.setDescription("Update description");
        mockMvc.perform(patch("/items/99999").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", user.getId())
                .content(objectMapper.writeValueAsString(item))).andExpect(status().isNotFound());
    }

    @DisplayName("PATCH /items. Обновление предмета не владельцем")
    @Test
    void updateItemByUnknownUser() throws Exception {
        user.setEmail("vasYan12313@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
            user.setEmail("adada@ya.ru");
        });
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", user.getId())
                .content(objectMapper.writeValueAsString(item))).andDo(result -> {
            ItemDto itemFromDb = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
            item.setId(itemFromDb.getId());
        });
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
        });

        item.setName("Update name");
        item.setDescription("Update description");
        mockMvc.perform(patch("/items/" + item.getId()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .content(objectMapper.writeValueAsString(item))).andExpect(status().isForbidden());
    }

    @DisplayName("GET /items. Получение предмета по id")
    @Test
    void getItem() throws Exception {
        user.setEmail("vasyan987@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
        });
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", user.getId())
                .content(objectMapper.writeValueAsString(item))).andDo(result -> {
            ItemDto itemFromDb = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
            item.setId(itemFromDb.getId());
        });

        mockMvc.perform(get("/items/" + item.getId()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.getName()));
    }

    @DisplayName("GET /items/search. Поиск предмета по названию")
    @Test
    void searchItem() throws Exception {
        String text = "search";
        String defaultName = item.getName();
        user.setEmail("vasyan666@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
        });
        for (int i = 0; i < 5; i++) {
            if (item.getName().contains(text)) {
                item.setName(defaultName);
            }
            if (i == 3) {
                item.setName(text);
            }
            mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", user.getId())
                    .content(objectMapper.writeValueAsString(item))).andDo(result -> {
                ItemDto itemFromDb = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
                item.setId(itemFromDb.getId());
            });
        }

        mockMvc.perform(get("/items/search").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()).param("text", text))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(text));
    }

    @DisplayName("GET /items/search. Поиск предмета без названия")
    @Test
    void searchItemIncorrectText() throws Exception {
        mockMvc.perform(get("/items/search").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)).andExpect(status().isBadRequest());
    }

}
