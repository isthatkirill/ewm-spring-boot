package ru.practicum.main.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.service.EventService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public abstract class CompilationMapper {

    @Autowired
    protected EventService eventService;

    @Mapping(target = "events", expression = "java(eventService.mapToShortDtoWithViewsAndRequests(compilation.getEvents()))")
    public abstract CompilationDto toCompilationDto(Compilation compilation);

    public abstract List<CompilationDto> compilationDto(List<Compilation> compilations);

    @Mapping(target = "events", source = "events")
    public abstract Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events);

}
