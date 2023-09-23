package isthatkirill.main.request.repository;

import isthatkirill.main.event.model.EventConfirmedRequests;
import isthatkirill.main.request.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query("SELECT new isthatkirill.main.event.model.EventConfirmedRequests(r.event.id, count(r.id)) " +
            "FROM Request r " +
            "WHERE r.event.id IN ?1 " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id")
    List<EventConfirmedRequests> getConfirmedRequests(List<Long> ids);

}
