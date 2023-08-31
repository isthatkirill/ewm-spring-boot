package isthatkirill.main.location.mapper;

import isthatkirill.main.location.dto.LocationDto;
import isthatkirill.main.location.model.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toLocation(LocationDto locationDto);

}
