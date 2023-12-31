package isthatkirill.main.user.service;

import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.user.dto.UserDto;
import isthatkirill.main.user.mapper.UserMapper;
import isthatkirill.main.user.model.User;
import isthatkirill.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(userMapper.toUser(userDto));
        log.info("New user added --> id={}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        checkIfUserExists(userId);
        userRepository.deleteById(userId);
        log.info("User with id={} has been deleted", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get all users ids={}, from={}, size={}", ids, from, size);
        return (ids == null || ids.isEmpty()) ? userMapper.toUserDto(userRepository.findAll(pageable)) :
                userMapper.toUserDto(userRepository.findAllByIdIn(ids, pageable));
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class, userId);
        }
    }

}
