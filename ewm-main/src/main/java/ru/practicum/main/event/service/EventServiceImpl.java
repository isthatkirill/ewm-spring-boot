package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.service.CategoryService;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.mapper.LocationMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.model.enums.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.event.repository.LocationRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

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

        return eventMapper.toEventFullDto(eventRepository.save(event), 0L, 0L);
    }

    private Location findLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));

    }

}
