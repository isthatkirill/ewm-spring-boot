package isthatkirill.main.event.controller;

import isthatkirill.main.event.dto.EventFullDto;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.event.model.EventSort;
import isthatkirill.main.event.service.EventService;
import isthatkirill.main.util.Formats;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public EventFullDto getEventByPublic(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEventByPublic(id, request.getRequestURI(), request.getRemoteAddr());
    }

    @GetMapping
    public List<EventShortDto> getAllEventsByPublic(@RequestParam(required = false) String text,
                                                    @RequestParam(required = false) List<Long> categories,
                                                    @RequestParam(required = false) Boolean paid,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = Formats.DATE_PATTERN) LocalDateTime rangeStart,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = Formats.DATE_PATTERN) LocalDateTime rangeEnd,
                                                    @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                    @RequestParam(required = false) EventSort sort,
                                                    @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
                                                    HttpServletRequest request) {
        return eventService.getAllEventsByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, request.getRequestURI(), request.getRemoteAddr());
    }

}
