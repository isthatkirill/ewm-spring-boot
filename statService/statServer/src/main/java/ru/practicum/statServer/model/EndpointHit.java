package ru.practicum.statServer.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "endpoint_hits")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "app", nullable = false, length = 255)
    String app;

    @Column(name = "uri", nullable = false, length = 31)
    String uri;

    @Column(name = "ip", nullable = false, length = 255)
    String ip;

    @Column(name = "created", nullable = false)
    LocalDateTime timestamp;
}


