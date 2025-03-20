package ru.practicum.shareit.item.model;

import lombok.Data;


@Data
public class RequestItemCreate {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
