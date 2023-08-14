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
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.enums.EventSort;
import ru.practicum.main.event.model.enums.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.location.mapper.LocationMapper;
import ru.practicum.main.location.model.Location;
import ru.practicum.main.location.repository.LocationRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
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
    private final StatService statService;

    @Override
    @Transactional
    public EventFullDto create(NewEventDto newEventDto, Long userId) {
        User initiator = userService.getById(userId);
        Category category = categoryService.getCategoryById(newEventDto.getCategory());
        Location location = findLocation(locationMapper.toLocation(newEventDto.getLocation()));

        Event event = eventMapper.toEvent(newEventDto, initiator, category, location, EventState.PENDING);
        log.info("Create new event --> {}", event);

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
    @Transactional
    public EventFullDto updateByInitiator(UpdateEventUserRequest updatedEvent, Long eventId, Long userId) {
        userService.getById(userId);
        Event event = checkIfOwnEventExistsAndGet(eventId, userId);
        checkIfUserCanUpdate(event);

        if (updatedEvent.getEventDate() != null) event.setEventDate(updatedEvent.getEventDate());
        if (updatedEvent.getPaid() != null) event.setPaid(updatedEvent.getPaid());
        if (updatedEvent.getAnnotation() != null) event.setAnnotation(updatedEvent.getAnnotation());
        if (updatedEvent.getParticipantLimit() != null) event.setParticipantLimit(updatedEvent.getParticipantLimit());
        if (updatedEvent.getDescription() != null) event.setDescription(updatedEvent.getDescription());
        if (updatedEvent.getRequestModeration() != null)
            event.setRequestModeration(updatedEvent.getRequestModeration());
        if (updatedEvent.getCategory() != null)
            event.setCategory(categoryService.getCategoryById(updatedEvent.getCategory()));
        if (updatedEvent.getTitle() != null) event.setTitle(updatedEvent.getTitle());
        if (updatedEvent.getLocation() != null)
            event.setLocation(findLocation(locationMapper.toLocation(updatedEvent.getLocation())));
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
    @Transactional
    public EventFullDto updateByAdmin(UpdateEventAdminRequest updatedEvent, Long eventId) {
        Event event = checkIfEventExistsAndGet(eventId);
        checkIfAdminCanUpdate(event);

        if (updatedEvent.getEventDate() != null) event.setEventDate(updatedEvent.getEventDate());
        if (updatedEvent.getAnnotation() != null) event.setAnnotation(updatedEvent.getAnnotation());
        if (updatedEvent.getPaid() != null) event.setPaid(updatedEvent.getPaid());
        if (updatedEvent.getParticipantLimit() != null) {
            checkNewLimit(updatedEvent.getParticipantLimit(), statService.getConfirmedRequests(eventId));
            event.setParticipantLimit(updatedEvent.getParticipantLimit());
        }
        if (updatedEvent.getRequestModeration() != null)
            event.setRequestModeration(updatedEvent.getRequestModeration());
        if (updatedEvent.getDescription() != null) event.setDescription(updatedEvent.getDescription());
        if (updatedEvent.getCategory() != null)
            event.setCategory(categoryService.getCategoryById(updatedEvent.getCategory()));
        if (updatedEvent.getLocation() != null)
            event.setLocation(findLocation(locationMapper.toLocation(updatedEvent.getLocation())));
        if (updatedEvent.getTitle() != null) event.setTitle(updatedEvent.getTitle());
        if (updatedEvent.getStateAction() != null) {
            switch (updatedEvent.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
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
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("Get events by admin with params: users={}, states={}, categories={}, start={}, end={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return mapToFullDtoWithViewsAndRequests(
                eventRepository.findEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByPublic(Long eventId, HttpServletRequest request) {
        Event event = checkIfPublishedEventExistsAndGet(eventId);
        log.info("Get event with id={} by public", eventId);
        statService.hit(request);
        return mapToFullDtoWithViewsAndRequests(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from,
                                                    Integer size, HttpServletRequest request) {

        checkIfStartBeforeEnd(rangeStart, rangeEnd);

        List<Event> events = eventRepository.findEventsByPublic(text, categories, paid, rangeStart, rangeEnd, from, size);

        Map<Long, Integer> eventLimits = new HashMap<>();
        events.forEach(e -> eventLimits.put(e.getId(), e.getParticipantLimit()));

        List<EventShortDto> eventsWithViewsAndRequests = mapToShortDtoWithViewsAndRequests(events);

        if (onlyAvailable) {
            eventsWithViewsAndRequests = eventsWithViewsAndRequests.stream()
                    .filter(e -> eventLimits.get(e.getId()) == 0 || eventLimits.get(e.getId()) > e.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case VIEWS:
                    eventsWithViewsAndRequests.sort(Comparator.comparing(EventShortDto::getViews));
                    break;
                case EVENT_DATE:
                    eventsWithViewsAndRequests.sort(Comparator.comparing(EventShortDto::getEventDate));
                    break;
            }
        }

        log.info("Get events by public with params: text={}, categories={}, paid={}, start={}, end={}, onlyAvailable={}," +
                "sort={}, from={}, size={}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        statService.hit(request);

        return eventsWithViewsAndRequests;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdAndInitiatorId(Long eventId, Long userId) {
        return mapToFullDtoWithViewsAndRequests(checkIfOwnEventExistsAndGet(eventId, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Event getById(Long eventId) {
        return checkIfEventExistsAndGet(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByIds(List<Long> ids) {
        return eventRepository.findEventsByIdIn(ids);
    }

    @Override
    public Event checkIfOwnEventExistsAndGet(Long eventId, Long userId) {
        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> mapToShortDtoWithViewsAndRequests(List<Event> events) {
        Map<Long, Long> views = statService.getViews(events);

        return events.stream()
                .map(e -> eventMapper.toEventShortDto(
                        e,
                        statService.getConfirmedRequests(e.getId()),
                        views.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private List<EventFullDto> mapToFullDtoWithViewsAndRequests(List<Event> events) {
        Map<Long, Long> views = statService.getViews(events);

        return events.stream()
                .map(e -> eventMapper.toEventFullDto(
                        e,
                        statService.getConfirmedRequests(e.getId()),
                        views.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private void checkNewLimit(Integer newLimit, Long confirmedReq) {
        if (newLimit != 0 && newLimit < confirmedReq) {
            throw new ForbiddenException("New limit cannot be less than the number of confirmed requests");
        }
    }

    private EventFullDto mapToFullDtoWithViewsAndRequests(Event event) {
        return mapToFullDtoWithViewsAndRequests(Collections.singletonList(event)).get(0);
    }

    private EventShortDto mapToShortDtoWithViewsAndRequests(Event event) {
        return mapToShortDtoWithViewsAndRequests(Collections.singletonList(event)).get(0);
    }

    private void checkIfStartBeforeEnd(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalStateException("Incorrect time interval. The start param should be earlier than the end param");
        }
    }

    private void checkIfUserCanUpdate(Event event) {
        if (event.getState().equals(EventState.PUBLISHED))
            throw new ForbiddenException("Only pending or canceled events can be changed");
    }

    private void checkIfAdminCanUpdate(Event event) {
        if (!event.getState().equals(EventState.PENDING))
            throw new ForbiddenException("Cannot publish the event because it's not in the right state: PUBLISHED");
    }

    private Event checkIfEventExistsAndGet(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }

    private Event checkIfPublishedEventExistsAndGet(Long eventId) {
        return eventRepository.getEventIfPublished(eventId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }

    private Location findLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));

    }

}
