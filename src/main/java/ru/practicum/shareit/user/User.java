package ru.practicum.shareit.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Data
@Component
@EqualsAndHashCode(of = {"id"})
public class User {
    private Long id;
    private String name;
    private String email;
}
