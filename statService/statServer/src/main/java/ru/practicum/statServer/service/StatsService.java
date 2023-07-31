package ru.practicum.statServer.service;

import ru.practicum.statDto.dto.EndpointHitDto;
import ru.practicum.statDto.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
