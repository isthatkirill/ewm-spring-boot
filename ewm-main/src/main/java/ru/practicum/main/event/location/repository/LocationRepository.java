package ru.practicum.main.event.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.event.location.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(Float lat, Float lon);

}
