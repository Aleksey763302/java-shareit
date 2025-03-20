package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private long id;

    @Column(name = "requestor_id")
    private long requestorId;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDateTime created;

    @OneToMany(mappedBy = "request", fetch = FetchType.EAGER)
    private List<Item> items;
}
