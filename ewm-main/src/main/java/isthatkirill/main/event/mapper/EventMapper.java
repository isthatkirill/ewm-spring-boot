package isthatkirill.main.event.mapper;

import isthatkirill.main.category.mapper.CategoryMapper;
import isthatkirill.main.category.model.Category;
import isthatkirill.main.event.dto.EventFullDto;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.event.dto.NewEventDto;
import isthatkirill.main.event.model.Event;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.location.mapper.LocationMapper;
import isthatkirill.main.location.model.Location;
import isthatkirill.main.user.mapper.UserMapper;
import isthatkirill.main.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class,
        CategoryMapper.class,
        LocationMapper.class
})
public interface EventMapper {

    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    Event toEvent(NewEventDto newEventDto, User initiator, Category category, Location location, EventState state);

    EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views);

    EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views);

}
