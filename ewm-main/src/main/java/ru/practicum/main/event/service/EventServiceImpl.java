package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.service.CategoryService;
import ru.practicum.main.error.exception.EntityNotFoundException;
import ru.practicum.main.error.exception.ForbiddenException;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.mapper.LocationMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.model.enums.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.event.repository.LocationRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventFullDto create(NewEventDto newEventDto, Long userId) {
        User initiator = userService.getById(userId);
        Category category = categoryService.getCategoryById(newEventDto.getCategory());
        Location location = findLocation(locationMapper.toLocation(newEventDto.getLocation()));

        Event event = eventMapper.toEvent(newEventDto, initiator, category, location, EventState.PENDING);
        log.info("Create new event with title={} by user with id={}", event.getTitle(), userId);

        return mapToFullDtoWithViewsAndRequests(List.of(eventRepository.save(event))).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get events from={} size={} added by user with id={}", from, size, userId);
        return mapToShortDtoWithViewsAndRequests(eventRepository.findEventsByInitiatorId(userId, pageable));
    }

    @Override
    public EventFullDto update(UpdateEventUserRequest updateEvent, Long userId, Long eventId) {
        log.info("Update event with id={} by user id={}. New data={}", eventId, userId, updateEvent);

        userService.getById(userId);
        Event event = checkIfEventExistsAndGet(eventId, userId);
        checkIfCanUpdate(event);

        if (updateEvent.getEventDate() != null) event.setEventDate(updateEvent.getEventDate());
        if (updateEvent.getPaid() != null) event.setPaid(updateEvent.getPaid());
        if (updateEvent.getAnnotation() != null) event.setAnnotation(updateEvent.getAnnotation());
        if (updateEvent.getParticipantLimit() != null) event.setParticipantLimit(updateEvent.getParticipantLimit());
        if (updateEvent.getDescription() != null) event.setDescription(updateEvent.getDescription());
        if (updateEvent.getRequestModeration() != null) event.setRequestModeration(updateEvent.getRequestModeration());
        if (updateEvent.getCategory() != null) event.setCategory(categoryService.getCategoryById(updateEvent.getCategory()));
        if (updateEvent.getTitle() != null) event.setTitle(updateEvent.getTitle());
        if (updateEvent.getLocation() != null) event.setLocation(findLocation(locationMapper.toLocation(updateEvent.getLocation())));
        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
            }
        }

        return mapToFullDtoWithViewsAndRequests(List.of(eventRepository.save(event))).get(0);

    }


    private List<EventShortDto> mapToShortDtoWithViewsAndRequests(List<Event> events) {

        //views and requests processing
        //TODO add processing views and confirmedRequests

        return events.stream()
                .map(e -> eventMapper.toEventShortDto(e, 0L, 0L))
                .collect(Collectors.toList());
    }

    private List<EventFullDto> mapToFullDtoWithViewsAndRequests(List<Event> events) {

        //views and requests processing
        //TODO add processing views and confirmedRequests

        return events.stream()
                .map(e -> eventMapper.toEventFullDto(e, 0L, 0L))
                .collect(Collectors.toList());
    }

    private void checkIfCanUpdate(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) throw new ForbiddenException("Only pending or canceled events can be changed");
    }

    private Event checkIfEventExistsAndGet(Long eventId, Long userId) {
        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }


    private Location findLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));

    }

}
