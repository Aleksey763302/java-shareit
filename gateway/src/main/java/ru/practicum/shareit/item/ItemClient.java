package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.request.RequestCommentCreate;
import ru.practicum.shareit.item.dto.request.RequestItemCreate;
import ru.practicum.shareit.item.dto.request.RequestItemUpdate;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createItem(RequestItemCreate request, long userId) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> updateItem(RequestItemUpdate request, long itemId, long userId) {
        String url = "/" + itemId;
        return patch(url, userId, request);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        String url = "/" + itemId;
        return get(url, userId);
    }

    public ResponseEntity<Object> getItemsByUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(String text, long userId) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        String url = "/search?text={text}";

        return get(url, userId, parameters);
    }

    public ResponseEntity<Object> createComment(RequestCommentCreate body, long userId, long itemId) {
        String url = "/" + itemId + "/comment";
        return post(url, userId, body);
    }
}
