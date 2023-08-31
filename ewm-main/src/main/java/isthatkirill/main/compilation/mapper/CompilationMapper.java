package isthatkirill.main.compilation.mapper;

import isthatkirill.main.compilation.dto.CompilationRequestDto;
import isthatkirill.main.compilation.dto.CompilationResponseDto;
import isthatkirill.main.compilation.model.Compilation;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.event.mapper.EventMapper;
import isthatkirill.main.event.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {


    @Mapping(target = "events", source = "eventDtos")
    CompilationResponseDto toCompilationDto(Compilation compilation, List<EventShortDto> eventDtos);

    @Mapping(target = "events", source = "events")
    Compilation toCompilation(CompilationRequestDto compilationRequestDto, Set<Event> events);

}
