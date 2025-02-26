package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long id);

    List<Item> findByNameContainingIgnoreCase(String text);

    default Map<Long, Item> findItemsGroupedById(Long userId) {
        return findByOwnerId(userId).stream().collect(Collectors.toMap(Item::getId, Function.identity()));
    }
}