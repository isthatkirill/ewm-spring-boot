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
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.location.mapper.LocationMapper;
import ru.practicum.main.event.location.model.Location;
import ru.practicum.main.event.location.repository.LocationRepository;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.enums.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import java.util.Collections;
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

        return mapToFullDtoWithViewsAndRequests(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get events from={} size={} added by user with id={}", from, size, userId);
        return mapToShortDtoWithViewsAndRequests(eventRepository.findEventsByInitiatorId(userId, pageable));
    }

    @Override
    public EventFullDto updateByInitiator(UpdateEventUserRequest updatedEvent, Long userId, Long eventId) {
        userService.getById(userId);
        Event event = checkIfEventExistsAndGet(eventId, userId);
        checkIfUserCanUpdate(event);

        if (updatedEvent.getEventDate() != null) event.setEventDate(updatedEvent.getEventDate());
        if (updatedEvent.getPaid() != null) event.setPaid(updatedEvent.getPaid());
        if (updatedEvent.getAnnotation() != null) event.setAnnotation(updatedEvent.getAnnotation());
        if (updatedEvent.getParticipantLimit() != null) event.setParticipantLimit(updatedEvent.getParticipantLimit());
        if (updatedEvent.getDescription() != null) event.setDescription(updatedEvent.getDescription());
        if (updatedEvent.getRequestModeration() != null) event.setRequestModeration(updatedEvent.getRequestModeration());
        if (updatedEvent.getCategory() != null) event.setCategory(categoryService.getCategoryById(updatedEvent.getCategory()));
        if (updatedEvent.getTitle() != null) event.setTitle(updatedEvent.getTitle());
        if (updatedEvent.getLocation() != null) event.setLocation(findLocation(locationMapper.toLocation(updatedEvent.getLocation())));
        if (updatedEvent.getStateAction() != null) {
            switch (updatedEvent.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
            }
        }

        log.info("Update event with id={} by user id={}. New data={}", eventId, userId, updatedEvent);
        return mapToFullDtoWithViewsAndRequests(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateByAdmin(UpdateEventAdminRequest updatedEvent, Long eventId) {
        Event event = checkIfEventExistsAndGet(eventId);
        checkIfAdminCanUpdate(event);

        if (updatedEvent.getEventDate() != null) event.setEventDate(updatedEvent.getEventDate());
        if (updatedEvent.getAnnotation() != null) event.setAnnotation(updatedEvent.getAnnotation());
        if (updatedEvent.getPaid() != null) event.setPaid(updatedEvent.getPaid());
        if (updatedEvent.getParticipantLimit() != null) event.setParticipantLimit(updatedEvent.getParticipantLimit()); //TODO
        if (updatedEvent.getRequestModeration() != null) event.setRequestModeration(updatedEvent.getRequestModeration());
        if (updatedEvent.getDescription() != null) event.setDescription(updatedEvent.getDescription());
        if (updatedEvent.getCategory() != null) event.setCategory(categoryService.getCategoryById(updatedEvent.getCategory()));
        if (updatedEvent.getLocation() != null) event.setLocation(findLocation(locationMapper.toLocation(updatedEvent.getLocation())));
        if (updatedEvent.getTitle() != null) event.setTitle(updatedEvent.getTitle());
        if (updatedEvent.getStateAction() != null) {
            switch (updatedEvent.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        log.info("Update event with id={} by admin. New data={}", eventId, updatedEvent);
        return mapToFullDtoWithViewsAndRequests(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByIdAndInitiatorId(Long eventId, Long userId) {
        return mapToFullDtoWithViewsAndRequests(checkIfEventExistsAndGet(eventId, userId));
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

    private EventFullDto mapToFullDtoWithViewsAndRequests(Event event) {
        return mapToFullDtoWithViewsAndRequests(Collections.singletonList(event)).get(0);
    }

    private EventShortDto mapToShortDtoWithViewsAndRequests(Event event) {
        return mapToShortDtoWithViewsAndRequests(Collections.singletonList(event)).get(0);
    }

    private void checkIfUserCanUpdate(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) throw new ForbiddenException("Only pending or canceled events can be changed");
    }

    private void checkIfAdminCanUpdate(Event event) {
        if (!event.getState().equals(EventState.PENDING)) throw new ForbiddenException("Cannot publish the event because it's not in the right state: PUBLISHED");
    }

    private Event checkIfEventExistsAndGet(Long eventId, Long userId) {
        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }

    private Event checkIfEventExistsAndGet(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }

    private Location findLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));

    }

}
