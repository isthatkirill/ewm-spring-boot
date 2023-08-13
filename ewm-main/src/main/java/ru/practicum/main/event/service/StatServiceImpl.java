package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.stats.StatClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@ComponentScan("ru.practicum.stats")
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatClient statClient;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public void hit(HttpServletRequest request) {
        EndpointHitDto hit = buildHit(request);
        log.info("Add hit --> {}", hit);
        statClient.addHit(hit);
    }

    @Override
    @Transactional
    public Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();

        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        Optional<LocalDateTime> minPublished = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo);

        if (minPublished.isPresent()) {
            LocalDateTime start = minPublished.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = publishedEvents.stream()
                    .map(e -> "/events/" + e.getId())
                    .collect(Collectors.toList());

            List<ViewStatsDto> stats = statClient.getStats(start, end, uris, true);
            stats.forEach(s -> {
                Long eventId = Long.parseLong(s.getUri().substring(s.getUri().lastIndexOf("/") + 1));
                views.put(eventId, s.getHits());
            });
        }

        return views;
    }

    // Located here, not in RequestService to avoid circular dependencies (EventService -> RequestService -> EventService)
    @Override
    @Transactional(readOnly = true)
    public Long getConfirmedRequests(Long eventId) {
        log.info("Get number of confirmed requests for event id={}", eventId);
        return requestRepository.getConfirmedRequests(eventId);
    }

    private EndpointHitDto buildHit(HttpServletRequest request) {
        return EndpointHitDto.builder()
                .app("ewm-main")
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .ip(request.getRemoteAddr())
                .build();
    }

}
