package ru.practicum.main.event.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.event.location.dto.LocationDto;
import ru.practicum.main.event.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toLocation(LocationDto locationDto);

}
