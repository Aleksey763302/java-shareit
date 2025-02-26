package ru.practicum.shareit.contollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.RequestUserCreate;
import ru.practicum.shareit.user.dto.RequestUserUpdate;
import ru.practicum.shareit.user.UserController;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.practicum.shareit"})
@WebMvcTest(controllers = UserController.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
@Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UserTest {
    private RequestUserCreate user;
    private RequestUserUpdate userUpdate;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        user = new RequestUserCreate();
        user.setName("Vasiliy");
        user.setEmail(Instant.now().toEpochMilli() + "@Gmail.com");

        userUpdate = new RequestUserUpdate();
        userUpdate.setName("Vasiliy");
        userUpdate.setEmail(Instant.now().toEpochMilli() + "@Gmail.com");
    }

    @DisplayName("POST /users. Добавление корректного пользователя")
    @Test
    void createUser() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
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
        userUpdate.setId(2L);

        mockMvc.perform(patch("/users/" + userUpdate.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(userUpdate)));
    }

    @DisplayName("PATCH /users. Обновление email пользователя на другой существующий email")
    @Test
    void updateUserExistEmail() throws Exception {
        user.setEmail("vasayn@gmail.com");

        mockMvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict());
    }

    @DisplayName("GET /users. Получение списка пользователей")
    @Test
    void getUser() throws Exception {
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @DisplayName("DELETE /users. Удаление несуществующего пользователя по ID")
    @Test
    void deleteNotExistUser() throws Exception {
        mockMvc.perform(delete("/users/100")).andExpect(status().isNotFound());
    }
}