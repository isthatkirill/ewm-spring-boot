package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationRequestDto;
import ru.practicum.main.compilation.dto.CompilationResponseDto;
import ru.practicum.main.compilation.mapper.CompilationMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.error.exception.EntityNotFoundException;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

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
        return compilationMapper.toCompilationDto(compilation);
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

        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
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
        return compilationMapper.toCompilationDto(checkIfCompExistsAndGet(compId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationResponseDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get compilations with params pinned={}, from={}, size={}", pinned, from, size);
        List<Compilation> compilations = (pinned != null)
                ? compilationRepository.findAllByPinnedIs(pinned, pageable)
                : compilationRepository.findAll(pageable).toList();
        return compilationMapper.toCompilationDto(compilations);
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
