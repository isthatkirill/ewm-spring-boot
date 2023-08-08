package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.NewEventDto;

public interface EventService {

    EventFullDto create(NewEventDto newEventDto, Long userId);

}
