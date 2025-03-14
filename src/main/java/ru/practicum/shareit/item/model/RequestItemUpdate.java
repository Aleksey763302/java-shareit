package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class RequestItemUpdate {
    private String name;
    private String description;
    private Boolean available;
}
