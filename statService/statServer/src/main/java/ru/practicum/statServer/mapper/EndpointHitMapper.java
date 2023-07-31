package ru.practicum.statServer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.statDto.dto.EndpointHitDto;
import ru.practicum.statServer.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    @Mapping(target = "id", ignore = true)
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

}
