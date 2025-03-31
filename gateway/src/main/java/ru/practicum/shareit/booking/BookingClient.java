package ru.practicum.shareit.booking;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.RequestBookingCreate;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.client.BaseClient;

@Slf4j
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> bookItem(long userId, RequestBookingCreate requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        String url = "/" + bookingId;
        return get(url, userId);
    }

    public ResponseEntity<Object> approveBooking(long bookingId, long userId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        String url = "/" + bookingId + "?approved={approved}";

        log.debug("URL: {}, parameters: {}", url, parameters);

        return patch(url, userId, parameters,null);
    }

    public ResponseEntity<Object> getBookingsByOwner(State state, long userId) {
        String url = "/owner?state={state}";
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get(url, userId, parameters);
    }
}
