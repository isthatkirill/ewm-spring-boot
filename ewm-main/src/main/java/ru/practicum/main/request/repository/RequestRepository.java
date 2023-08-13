package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findRequestByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findRequestsByRequesterId(Long userId);

    Optional<Request> findRequestsByRequesterIdAndId(Long userId, Long requestId);

    List<Request> findRequestsByIdIn(List<Long> ids);

    List<Request> findRequestsByEventInitiatorIdAndEventId(Long userId, Long eventId);

    @Query("SELECT count(r) FROM Request r " +
            "WHERE r.event.id = ?1 AND " +
            "r.status = 'CONFIRMED'")
    Long getConfirmedRequests(Long eventId);

}
