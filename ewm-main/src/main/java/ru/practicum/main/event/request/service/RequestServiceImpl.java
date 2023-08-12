package ru.practicum.main.event.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.error.exception.EntityNotFoundException;
import ru.practicum.main.error.exception.ForbiddenException;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.enums.EventState;
import ru.practicum.main.event.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.event.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.event.request.dto.ParticipationRequestDto;
import ru.practicum.main.event.request.mapper.RequestMapper;
import ru.practicum.main.event.request.model.Request;
import ru.practicum.main.event.request.model.enums.RequestState;
import ru.practicum.main.event.request.repository.RequestRepository;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.event.service.StatService;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;
    private final RequestMapper requestMapper;
    private final StatService statService;

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {

        User user = userService.getById(userId);
        Event event = eventService.getById(eventId);
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

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0)
            request.setStatus(RequestState.CONFIRMED);

        log.info("Add new request --> {}", request);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));

    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        userService.getById(userId);
        log.info("Get requests for user with id={}", userId);
        return requestMapper.toParticipationRequestDto(requestRepository.findRequestsByRequesterId(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        userService.getById(userId);
        Request request = findByUserIdAndRequestId(userId, requestId);
        request.setStatus(RequestState.CANCELED);
        log.info("User with id={} cancelled request with id={}", userId, requestId);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult processRequestsByInitiator(EventRequestStatusUpdateRequest updateRequest,
                                                                     Long userId, Long eventId) {
        log.info("Processing requests --> {} by user id={} for event id={}", updateRequest, userId, eventId);

        userService.getById(userId);
        Event event = eventService.checkIfOwnEventExistsAndGet(eventId, userId);
        List<Long> ids = updateRequest.getRequestIds();

        if (shouldSkipProcessing(ids, event)) {
            return new EventRequestStatusUpdateResult(
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

        return new EventRequestStatusUpdateResult(
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
        if (statService.getConfirmedRequests(event.getId()) + requestToAdd > event.getParticipantLimit() && event.getParticipantLimit() != 0) {
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


}
