package isthatkirill.stats.service;

import isthatkirill.stats.dto.EndpointHitDto;
import isthatkirill.stats.dto.ViewStatsDto;
import isthatkirill.stats.mapper.EndpointHitMapper;
import isthatkirill.stats.repository.StatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;
    private final EndpointHitMapper mapper;

    @Override
    @Transactional
    public void addHit(EndpointHitDto endpointHitDto) {
        log.info("Endpoint hit added --> {}", endpointHitDto);
        repository.save(mapper.toEndpointHit(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Get stats with params --> start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        if (start.isAfter(end)) {
            throw new IllegalStateException("Invalid time interval");
        }

        if (uris == null || uris.isEmpty()) {
            return unique ? repository.getStatsUniqueIp(start, end) : repository.getStats(start, end);
        } else {
            return unique ? repository.getStatsUniqueIpForUris(start, end, uris) : repository.getStatsForUris(start, end, uris);
        }
    }


}
