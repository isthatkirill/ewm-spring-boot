package isthatkirill.main.compilation.service;

import isthatkirill.main.compilation.dto.CompilationRequestDto;
import isthatkirill.main.compilation.dto.CompilationResponseDto;
import isthatkirill.main.compilation.mapper.CompilationMapper;
import isthatkirill.main.compilation.model.Compilation;
import isthatkirill.main.compilation.repository.CompilationRepository;
import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.event.mapper.EventMapper;
import isthatkirill.main.event.model.Event;
import isthatkirill.main.event.repository.EventRepository;
import isthatkirill.main.event.service.StatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final StatService statService;

    @Override
    @Transactional
    public CompilationResponseDto create(CompilationRequestDto compilationRequestDto) {
        if (compilationRequestDto.getPinned() == null) {
            compilationRequestDto.setPinned(false);
        }
        log.info("Create new compilation --> {}", compilationRequestDto);
        Set<Event> events = listToSet(eventRepository.findEventsByIdIn(compilationRequestDto.getEvents()));
        checkIfAllEventsFound(events.size(), compilationRequestDto.getEvents().size());
        Compilation compilation = compilationRepository.save(compilationMapper.toCompilation(compilationRequestDto, events));
        return compilationMapper.toCompilationDto(compilation, findViewsAndRequestsForEvents(events));
    }

    @Override
    @Transactional
    public CompilationResponseDto update(CompilationRequestDto compilationRequestDto, Long compId) {
        log.info("Update compilation with id={} with params={}", compId, compilationRequestDto);
        Compilation compilation = checkIfCompExistsAndGet(compId);

        if (compilationRequestDto.getTitle() != null) {
            compilation.setTitle(compilationRequestDto.getTitle());
        }
        if (compilationRequestDto.getPinned() != null) {
            compilation.setPinned(compilationRequestDto.getPinned());
        }
        if (compilationRequestDto.getEvents() != null) {
            Set<Event> events = listToSet(eventRepository.findEventsByIdIn(compilationRequestDto.getEvents()));
            checkIfAllEventsFound(events.size(), compilationRequestDto.getEvents().size());
            compilation.setEvents(events);
        }

        return compilationMapper.toCompilationDto(compilation, findViewsAndRequestsForEvents(compilation.getEvents()));
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        checkIfCompExists(compId);
        log.info("Delete compilation with id={}", compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationResponseDto getById(Long compId) {
        log.info("Get compilation with id={}", compId);
        Compilation compilation = checkIfCompExistsAndGet(compId);
        Set<Event> events = compilation.getEvents();
        return compilationMapper.toCompilationDto(compilation, findViewsAndRequestsForEvents(events));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationResponseDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get compilations with params pinned={}, from={}, size={}", pinned, from, size);
        List<Compilation> compilations = (pinned != null)
                ? compilationRepository.findAllByPinnedIs(pinned, pageable)
                : compilationRepository.findAll(pageable).toList();

        return compilations.stream()
                .map(c -> compilationMapper.toCompilationDto(c, findViewsAndRequestsForEvents(c.getEvents())))
                .collect(Collectors.toList());
    }

    private List<EventShortDto> findViewsAndRequestsForEvents(Set<Event> events) {
        Map<Long, Long> views = statService.getViews(new ArrayList<>(events));
        Map<Long, Long> confirmedRequests = statService.getConfirmedRequests(new ArrayList<>(events));

        return events.stream()
                .map(e -> eventMapper.toEventShortDto(
                        e,
                        confirmedRequests.getOrDefault(e.getId(), 0L),
                        views.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private Compilation checkIfCompExistsAndGet(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(Compilation.class, compId));
    }

    private void checkIfCompExists(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new EntityNotFoundException(Compilation.class, compId);
        }
    }

    private void checkIfAllEventsFound(Integer found, Integer provided) {
        if (!found.equals(provided)) {
            throw new EntityNotFoundException("Not all compilations found");
        }
    }

    private Set<Event> listToSet(List<Event> events) {
        return new HashSet<>(events);
    }

}
