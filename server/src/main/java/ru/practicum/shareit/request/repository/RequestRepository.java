package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT ir FROM ItemRequest ir ORDER BY ir.created ASC")
    List<ItemRequest> findAllRequests();

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestorId = :requestorId ORDER BY ir.created ASC")
    List<ItemRequest> findByRequestorId(long requestorId);
}
