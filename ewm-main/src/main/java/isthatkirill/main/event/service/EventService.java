package isthatkirill.main.event.service;

import isthatkirill.main.event.dto.EventFullDto;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.event.dto.NewEventDto;
import isthatkirill.main.event.dto.UpdateEventDto;
import isthatkirill.main.event.model.EventSort;
import isthatkirill.main.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto create(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto updateByInitiator(UpdateEventDto updatedEvent, Long eventId, Long userId);

    EventFullDto getEventByIdAndInitiatorId(Long eventId, Long userId);

    EventFullDto updateByAdmin(UpdateEventDto updatedEvent, Long eventId);

    List<EventFullDto> getAllEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto getEventByPublic(Long eventId, String uri, String ip);

    List<EventShortDto> getAllEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from,
                                             Integer size, String uri, String ip);

}
