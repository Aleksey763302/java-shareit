package ru.practicum.shareit.util;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.exceptions.NotValidParamException;

import java.util.Objects;

public class Validation {
    public static void validEmail(String email) {
        if (Objects.isNull(email) || email.isBlank()) {
            throw new NotValidParamException("Email должен быть указан");
        }
        if (!email.contains("@")) {
            throw new NotValidParamException("Неправильный формат Email");
        }
    }

    public static long validUserId(String id) {
        try {
            return Integer.toUnsignedLong(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            throw new NotValidParamException("Неправильный формат параметра userId");
        }
    }

    public static long validItemId(String id) {
        try {
            return Integer.toUnsignedLong(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            throw new NotValidParamException("Неправильный формат параметра itemId");
        }
    }

    public static void validItem(ItemDto item) {
        if (Objects.isNull(item.getAvailable())) {
            throw new NotValidParamException("Available должен быть указан");
        }
        if (Objects.isNull(item.getName()) || item.getName().isBlank()) {
            throw new NotValidParamException("Name должен быть указан");
        }
        if (Objects.isNull(item.getDescription()) || item.getDescription().isBlank()) {
            throw new NotValidParamException("Description должен быть указан");
        }
    }

    public static void validText(String text) {
        if (Objects.isNull(text)) {
            throw new NotValidParamException("Text должен быть указан");
        }
    }
}
