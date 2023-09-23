package isthatkirill.main.user.mapper;

import isthatkirill.main.user.dto.UserDto;
import isthatkirill.main.user.model.User;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDto(Page<User> users);

    List<UserDto> toUserDto(List<User> users);

}
