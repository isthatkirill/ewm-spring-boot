package isthatkirill.main.event.repository;

import isthatkirill.main.event.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, CustomEventRepository {

    List<Event> findEventsByInitiatorId(Long userId, Pageable pageable);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT e FROM Event e " +
            "WHERE e.id = ?1 and e.initiator.id = ?2")
    Optional<Event> findByIdAndInitiatorIdAndLock(Long eventId, Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT e FROM Event e " +
            "WHERE e.id = ?1 ")
    Optional<Event> findByIdAndLock(Long eventId);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' AND " +
            "e.id = ?1")
    Optional<Event> getEventIfPublished(Long eventId);

    List<Event> findEventsByIdIn(List<Long> ids);

    List<Event> findEventsByCategoryId(Long catId);

    Optional<Event> findEventByIdAndInitiatorId(Long eventId, Long userId);

}
