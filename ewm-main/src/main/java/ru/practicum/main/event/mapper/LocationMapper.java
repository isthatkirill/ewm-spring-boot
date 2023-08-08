package ru.practicum.main.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.event.dto.LocationDto;
import ru.practicum.main.event.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toLocation(LocationDto locationDto);

}
