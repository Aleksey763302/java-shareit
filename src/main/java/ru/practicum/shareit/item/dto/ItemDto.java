package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
