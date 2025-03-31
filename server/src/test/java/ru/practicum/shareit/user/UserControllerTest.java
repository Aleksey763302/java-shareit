package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.RequestUserCreate;
import ru.practicum.shareit.user.model.RequestUserUpdate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.practicum.shareit"})
@WebMvcTest(controllers = UserController.class)
@AutoConfigureDataJpa
class UserControllerTest {
    private RequestUserCreate userCreate;
    private RequestUserUpdate userUpdate;

    private Map<Long, UserDto> users;
    private List<Long> usersID;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        userCreate = new RequestUserCreate();
        userCreate.setName(generateRandomText(7));
        userCreate.setEmail(generateRandomText(10) + "@Gmail.com");

        userUpdate = new RequestUserUpdate();
        userUpdate.setName("Update User");
        userUpdate.setEmail(generateRandomText(10) + "@Gmail.com");

        users = createUsers(3);
        usersID = users.keySet().stream().toList();
    }

    @DisplayName("POST /users. Добавление корректного пользователя")
    @Test
    void createUserTest() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(userCreate.getName()))
                .andExpect(jsonPath("$.email").value(userCreate.getEmail()));
    }

    @DisplayName("PATCH /users. Корректное обновление пользователя")
    @Test
    void updateUserTest() throws Exception {
        Map<Long, UserDto> users = createUsers(1);
        long userId = users.keySet().stream().toList().getFirst();

        mockMvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userUpdate.getName()))
                .andExpect(jsonPath("$.email").value(userUpdate.getEmail()));
    }

    @DisplayName("PATCH /users. Корректное обновление имени пользователя")
    @Test
    void updateUser() throws Exception {
        Map<Long, UserDto> users = createUsers(1);
        long userId = users.keySet().stream().toList().getFirst();
        userUpdate.setEmail(null);

        mockMvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userUpdate.getName()))
                .andExpect(jsonPath("$.email").value(users.get(userId).getEmail()));
    }


    @DisplayName("GET /users. Поиск пользователя по ID")
    @Test
    void getUserTest() throws Exception {
        Map<Long, UserDto> users = createUsers(1);
        long userId = users.keySet().stream().toList().getFirst();

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(users.get(userId).getName()))
                .andExpect(jsonPath("$.email").value(users.get(userId).getEmail()));
    }

    @DisplayName("GET /users. Поиск несуществующего пользователя по ID")
    @Test
    void getUserByIdFailTest() throws Exception {
        mockMvc.perform(get("/users/" + usersID.getLast() + 100))
                .andExpect(status().isNotFound());
    }

    @DisplayName("DELETE /users. Удаление пользователя по ID")
    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/" + usersID.getFirst()))
                .andExpect(status().isNoContent());
    }

    @DisplayName("DELETE /users. Удаление пользователя по ID")
    @Test
    void deleteUserFailTest() throws Exception {
        mockMvc.perform(delete("/users/" + usersID.getLast() + 100))
                .andExpect(status().isNotFound());
    }

    @DisplayName("PATCH /users. Обновление email пользователя на другой существующий email")
    @Test
    void updateUserExistEmail() throws Exception {
        Map<Long, UserDto> users = createUsers(1);
        long userId = users.keySet().stream().toList().getFirst();
        userUpdate.setName(null);
        userUpdate.setEmail(users.get(userId).getEmail());

        mockMvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isConflict());
    }

    @DisplayName("GET /users. Поиск всех пользователей")
    @Test
    void getAllUsersTest() throws Exception {
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
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