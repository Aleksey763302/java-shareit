package ru.practicum.shareit.util;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.exceptions.NotValidParamException;

import java.util.Objects;

public class Validation {
    public static void validEmail(String email) {
        if (Objects.isNull(email) || email.isBlank()) {
            throw new NotValidParamException("The email field must be specified");
        }
        if (!email.contains("@")) {
            throw new NotValidParamException("Incorrect email format");
        }
    }

    public static void validName(String name){
        if (Objects.isNull(name) || name.isBlank()){
            throw new NotValidParamException("The name field must be specified");
        }
    }

    public static long validUserId(String id) {
        try {
            return Integer.toUnsignedLong(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            throw new NotValidParamException("Incorrect format of userId parameter");
        }
    }

    public static long validItemId(String id) {
        try {
            return Integer.toUnsignedLong(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            throw new NotValidParamException("Incorrect format of itemId parameter");
        }
    }

    public static void validItem(ItemDto item) {
        if (Objects.isNull(item.getAvailable())) {
            throw new NotValidParamException("The available field must be specified");
        }
        if (Objects.isNull(item.getName()) || item.getName().isBlank()) {
            throw new NotValidParamException("The name field must be specified");
        }
        if (Objects.isNull(item.getDescription()) || item.getDescription().isBlank()) {
            throw new NotValidParamException("The description field must be specified");
        }
    }

    public static void validText(String text) {
        if (Objects.isNull(text)) {
            throw new NotValidParamException("The text parameter must be specified");
        }
    }
}
