package isthatkirill.stats.mapper;

import isthatkirill.stats.dto.EndpointHitDto;
import isthatkirill.stats.model.EndpointHit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    @Mapping(target = "id", ignore = true)
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

}
