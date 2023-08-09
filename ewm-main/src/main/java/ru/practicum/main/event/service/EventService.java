package ru.practicum.main.event.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;

import javax.validation.Valid;
import java.util.List;

public interface EventService {

    EventFullDto create(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto update(UpdateEventUserRequest updateEvent, Long userId, Long eventId);

}
