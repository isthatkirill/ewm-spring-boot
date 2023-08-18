package ru.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.error.exception.EntityNotFoundException;
import ru.practicum.main.error.exception.ForbiddenException;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestState;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {

        User user = checkIfUserExistsAndGet(userId);
        Event event = checkIfEventExistsAndGet(eventId);
        checkIfNotRepeated(userId, eventId);
        checkIfNotOwnEvent(userId, event);
        checkIfPublished(event);
        checkParticipantLimit(1, event);

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(RequestState.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestState.CONFIRMED);
        }

        log.info("Add new request --> id={}", request.getId());
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));

    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        checkIfUserExists(userId);
        log.info("Get requests for user with id={}", userId);
        return requestMapper.toParticipationRequestDto(requestRepository.findRequestsByRequesterId(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        checkIfUserExists(userId);
        Request request = findByUserIdAndRequestId(userId, requestId);
        request.setStatus(RequestState.CANCELED);
        log.info("User with id={} cancelled request with id={}", userId, requestId);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto processRequestsByInitiator(EventRequestStatusUpdateRequestDto updateRequest,
                                                                        Long userId, Long eventId) {
        log.info("Processing requests --> {} by user id={} for event id={}", updateRequest, userId, eventId);

        checkIfUserExists(userId);
        Event event = checkIfOwnEventExistsAndGet(eventId, userId);
        List<Long> ids = updateRequest.getRequestIds();

        if (shouldSkipProcessing(ids, event)) {
            return new EventRequestStatusUpdateResultDto(
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        List<Request> requests = requestRepository.findRequestsByIdIn(ids);
        if (ids.size() != requests.size()) {
            throw new EntityNotFoundException("Not all requests were found");
        }

        checkIfPending(requests);

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        log.info("REQ : {}", requests); //TODO DELETE

        switch (updateRequest.getStatus()) {
            case REJECTED:
                requests.forEach(r -> r.setStatus(RequestState.REJECTED));
                rejected = requestRepository.saveAll(requests);
                log.info("REJECTED {}\n\n", rejected);
                break;
            case CONFIRMED:
                checkParticipantLimit(ids.size(), event);
                requests.forEach(r -> r.setStatus(RequestState.CONFIRMED));
                confirmed = requestRepository.saveAll(requests);
                log.info("confirmed {}\n\n", confirmed); //TODO DELETE
                break;
        }

        return new EventRequestStatusUpdateResultDto(
                requestMapper.toParticipationRequestDto(confirmed),
                requestMapper.toParticipationRequestDto(rejected)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByInitiator(Long userId, Long eventId) {
        log.info("Get requests by user id={} for event id={}", userId, eventId);
        return requestMapper.toParticipationRequestDto(requestRepository.findRequestsByEventInitiatorIdAndEventId(userId, eventId));
    }

    private Request findByUserIdAndRequestId(Long userId, Long requestId) {
        return requestRepository.findRequestsByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new EntityNotFoundException(Request.class, requestId));
    }

    private void checkParticipantLimit(Integer requestToAdd, Event event) {
        if (requestRepository.getConfirmedRequests(event.getId()) + requestToAdd > event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ForbiddenException("Exceeded the limit of participants");
        }
    }

    private void checkIfPending(List<Request> requests) {
        if (!requests.stream()
                .map(Request::getStatus)
                .allMatch(s -> s.equals(RequestState.PENDING))) {
            throw new ForbiddenException("Confirmed or cancelled requests cannot be modified");
        }
    }

    private boolean shouldSkipProcessing(List<Long> ids, Event event) {
        return ids.isEmpty() || event.getParticipantLimit() == 0 || !event.getRequestModeration();
    }

    private Event checkIfOwnEventExistsAndGet(Long eventId, Long userId) {
        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }

    private Event checkIfEventExistsAndGet(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }

    private void checkIfNotOwnEvent(Long userId, Event event) {
        if (userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Cannot create a request for your own event");
        }
    }

    private void checkIfPublished(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Cannot create a request for an unpublished event");
        }
    }

    private void checkIfNotRepeated(Long userId, Long eventId) {
        if (requestRepository.findRequestByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ForbiddenException("Cannot create same participation requests twice");
        }
    }

    private User checkIfUserExistsAndGet(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId));
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class, userId);
        }
    }

}
