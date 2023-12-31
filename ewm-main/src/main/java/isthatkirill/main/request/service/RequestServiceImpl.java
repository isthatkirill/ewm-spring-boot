package isthatkirill.main.request.service;

import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.error.exception.ForbiddenException;
import isthatkirill.main.event.model.Event;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.event.repository.EventRepository;
import isthatkirill.main.request.dto.EventRequestStatusUpdateRequestDto;
import isthatkirill.main.request.dto.EventRequestStatusUpdateResultDto;
import isthatkirill.main.request.dto.ParticipationRequestDto;
import isthatkirill.main.request.mapper.RequestMapper;
import isthatkirill.main.request.model.Request;
import isthatkirill.main.request.model.RequestState;
import isthatkirill.main.request.repository.RequestRepository;
import isthatkirill.main.user.model.User;
import isthatkirill.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        ParticipationRequestDto participationRequestDto = requestMapper
                .toParticipationRequestDto(requestRepository.save(request));

        log.info("Add new request --> id={}", request.getId());
        return participationRequestDto;

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

        switch (updateRequest.getStatus()) {
            case REJECTED:
                requests.forEach(r -> r.setStatus(RequestState.REJECTED));
                rejected = requestRepository.saveAll(requests);
                break;
            case CONFIRMED:
                checkParticipantLimit(ids.size(), event);
                requests.forEach(r -> r.setStatus(RequestState.CONFIRMED));
                confirmed = requestRepository.saveAll(requests);
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
        return eventRepository.findByIdAndInitiatorIdAndLock(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }

    private Event checkIfEventExistsAndGet(Long eventId) {
        return eventRepository.findByIdAndLock(eventId)
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
