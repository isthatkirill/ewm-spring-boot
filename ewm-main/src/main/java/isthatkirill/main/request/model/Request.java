package isthatkirill.main.request.model;

import isthatkirill.main.event.model.Event;
import isthatkirill.main.user.model.User;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "requests")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    Event event;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    User requester;

    @Column(name = "status", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    RequestState status;

    @Column(name = "created", nullable = false)
    LocalDateTime created;

}
