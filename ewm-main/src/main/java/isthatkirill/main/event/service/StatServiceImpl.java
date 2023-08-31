package isthatkirill.main.event.service;

import isthatkirill.main.event.model.Event;
import isthatkirill.main.request.repository.RequestRepository;
import isthatkirill.stats.StatClient;
import isthatkirill.stats.dto.EndpointHitDto;
import isthatkirill.stats.dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@ComponentScan("isthatkirill.stats")
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatClient statClient;
    private final RequestRepository requestRepository;

    @Override
    public void hit(String uri, String ip) {
        EndpointHitDto hit = buildHit(uri, ip);
        log.info("Add hit --> {}", hit);
        statClient.addHit(hit);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> publishedIds = events.stream()
                .filter(e -> e.getPublishedOn() != null)
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = new HashMap<>();

        requestRepository.getConfirmedRequests(publishedIds)
                .forEach(cr -> confirmedRequests.put(cr.getEventId(), cr.getConfirmed()));

        return confirmedRequests;
    }


    private EndpointHitDto buildHit(String uri, String ip) {
        return EndpointHitDto.builder()
                .app("ewm-main")
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .ip(ip)
                .build();
    }

}
