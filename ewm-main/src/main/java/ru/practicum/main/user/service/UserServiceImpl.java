package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.error.exception.EntityNotFoundException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

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
        log.info("New user added --> {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        checkIfUserExistsAndGet(userId);
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

    @Override
    @Transactional(readOnly = true)
    public User getById(Long userId) {
        return checkIfUserExistsAndGet(userId);
    }

    private User checkIfUserExistsAndGet(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId));
    }

}
