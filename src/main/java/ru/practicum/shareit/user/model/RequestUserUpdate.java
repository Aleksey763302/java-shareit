package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestUserUpdate {
    private String name;
    @Email
    private String email;
}
