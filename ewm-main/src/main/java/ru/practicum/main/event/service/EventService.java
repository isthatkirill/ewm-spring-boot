package ru.practicum.main.event.service;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.enums.EventSort;
import ru.practicum.main.event.model.enums.EventState;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.main.util.Formats.DATE_PATTERN;

public interface EventService {

    EventFullDto create(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto updateByInitiator(UpdateEventUserRequest updatedEvent, Long eventId, Long userId);

    EventFullDto getEventByIdAndInitiatorId(Long eventId, Long userId);

    EventFullDto updateByAdmin(UpdateEventAdminRequest updatedEvent, Long eventId);

    List<EventFullDto> getAllEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto getEventByPublic(Long eventId, HttpServletRequest request);

    List<EventShortDto> getAllEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from,
                                            Integer size, HttpServletRequest request);

    Event getById(Long eventId);

    Event checkIfOwnEventExistsAndGet(Long eventId, Long userId);

}
