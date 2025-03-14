package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemIdIn(Set<Long> setItemId);

    default Map<Long, List<Comment>> findCommentsGroupedByItem(Set<Long> userId) {
        return findByItemIdIn(userId).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
    }
}
