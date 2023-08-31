package isthatkirill.main.event.controller;


import isthatkirill.main.event.dto.EventFullDto;
import isthatkirill.main.event.dto.UpdateEventDto;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.event.service.EventService;
import isthatkirill.main.util.Formats;
import isthatkirill.main.validation.group.OnUpdateAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateByAdmin(@RequestBody @Validated(OnUpdateAdmin.class) UpdateEventDto updatedEvent,
                                      @PathVariable Long eventId) {
        return eventService.updateByAdmin(updatedEvent, eventId);
    }

    @GetMapping
    public List<EventFullDto> getAllEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                  @RequestParam(required = false) List<EventState> states,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = Formats.DATE_PATTERN) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = Formats.DATE_PATTERN) LocalDateTime rangeEnd,
                                                  @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return eventService.getAllEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

}
