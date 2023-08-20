package ru.practicum.main.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.model.Event;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, CustomEventRepository {

    List<Event> findEventsByInitiatorId(Long userId, Pageable pageable);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Event> findEventByIdAndInitiatorId(Long eventId, Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Event> findById(Long aLong);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' AND " +
            "e.id = ?1")
    Optional<Event> getEventIfPublished(Long eventId);

    List<Event> findEventsByIdIn(List<Long> ids);

    List<Event> findEventsByCategoryId(Long catId);

}
