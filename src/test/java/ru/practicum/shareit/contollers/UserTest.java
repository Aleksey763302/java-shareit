package ru.practicum.shareit.contollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.practicum.shareit"})
@WebMvcTest(controllers = UserController.class)
class UserTest {
    private User user;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Vasiliy");
    }

    @DisplayName("POST /users. Добавление корректного пользователя")
    @Test
    void createUser() throws Exception {
        user.setEmail("vasyan228@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(result -> {
                    User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
                    user.setId(userDb.getId());
                })
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(user)));
    }

    @DisplayName("POST /users. Добавление пользователя с неправельным Email")
    @Test
    void createUserNotValidEmail() throws Exception {
        user.setEmail("FailGmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andExpect(status().isBadRequest());
    }

    @DisplayName("PATCH /users. Корректное обновление пользователя")
    @Test
    void updateUser() throws Exception {
        user.setEmail("vasyan876@Gmail.com");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(result -> {
                    User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
                    user.setId(userDb.getId());
                });
        user.setName("Oleg");
        user.setEmail("EmailUpdate@gmail.com");

        mockMvc.perform(patch("/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(user)));
    }

    @DisplayName("PATCH /users. Обновление email пользователя на другой существующий email")
    @Test
    void updateUserExistEmail() throws Exception {
        user.setEmail("vasyan01@Gmail.com");
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andDo(result -> {
                        User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
                        user.setId(userDb.getId());
                    });
            user.setEmail("vas@gmail.com");
        }

        mockMvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict());
    }

    @DisplayName("PATCH /users. Обновление пользователя (имени нет)")
    @Test
    void updateUserNoName() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
        });
        user.setName("");

        mockMvc.perform(patch("/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andExpect(status().isBadRequest());
    }

    @DisplayName("GET /users. Получение списка пользователей")
    @Test
    void getUser() throws Exception {
        String email = "@Gmail.com";
        StringBuilder name = new StringBuilder("Oleg");
        for (int i = 0; i < 5; i++) {
            user.setName(new String(name));
            user.setEmail(name + email);
            mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)));
            name.append(i);
        }
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @DisplayName("DELETE /users. Удаление пользователя по ID")
    @Test
    void deleteUserById() throws Exception {
        user.setEmail("vasayn@ya.ru");
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andDo(result -> {
            User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
            user.setId(userDb.getId());
        });

        mockMvc.perform(delete("/users/" + user.getId())).andExpect(status().isOk());
    }

    @DisplayName("DELETE /users. Удаление несуществующего пользователя по ID")
    @Test
    void deleteNotExistUser() throws Exception {
        mockMvc.perform(delete("/users/100")).andExpect(status().isNotFound());
    }
}