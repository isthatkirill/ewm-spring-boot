package ru.practicum.statServer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statDto.dto.EndpointHitDto;
import ru.practicum.statDto.dto.ViewStatsDto;
import ru.practicum.statServer.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void addHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        service.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(value = "uris", required = false) List<String> uris,
                                       @RequestParam(value = "unique", defaultValue = "false", required = false) boolean unique) {
        return service.getStats(start, end, uris, unique);
    }

}
