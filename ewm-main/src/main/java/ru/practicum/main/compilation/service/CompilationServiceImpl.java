package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.mapper.CompilationMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.error.exception.EntityNotFoundException;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.service.EventService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        log.info("Create new compilation --> {}", newCompilationDto);
        List<Event> events = eventService.getEventsByIds(newCompilationDto.getEvents());
        checkIfAllEventsFound(events.size(), newCompilationDto.getEvents().size());
        Compilation compilation = compilationRepository.save(compilationMapper.toCompilation(newCompilationDto, events));
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(UpdateCompilationRequest updateCompilation, Long compId) {
        log.info("Update compilation with id={} with params={}", compId, updateCompilation);
        Compilation compilation = checkIfCompExistsAndGet(compId);

        if (updateCompilation.getTitle() != null) compilation.setTitle(updateCompilation.getTitle());
        if (updateCompilation.getPinned() != null) compilation.setPinned(updateCompilation.getPinned());
        if (updateCompilation.getEvents() != null) {
            List<Event> events = eventService.getEventsByIds(updateCompilation.getEvents());
            checkIfAllEventsFound(events.size(), updateCompilation.getEvents().size());
            compilation.setEvents(events);
        }

        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        checkIfCompExistsAndGet(compId);
        log.info("Delete compilation with id={}", compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        log.info("Get compilation with id={}", compId);
        return compilationMapper.toCompilationDto(checkIfCompExistsAndGet(compId));
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get compilations with params pinned={}, from={}, size={}", pinned, from, size);
        List<Compilation> compilations = (pinned != null)
                ? compilationRepository.findAllByPinnedIs(pinned, pageable)
                : compilationRepository.findAll(pageable).toList();
        return compilationMapper.compilationDto(compilations);
    }


    private Compilation checkIfCompExistsAndGet(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(Compilation.class, compId));
    }

    private void checkIfAllEventsFound(Integer found, Integer provided) {
        if (found != provided) throw new EntityNotFoundException("Not all compilations found");
    }

}
