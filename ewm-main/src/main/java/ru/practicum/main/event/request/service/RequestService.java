package ru.practicum.main.event.request.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.main.event.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.event.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.event.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

public interface RequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    ParticipationRequestDto cancel(Long userId, Long requestId);

    EventRequestStatusUpdateResult processRequestsByInitiator(EventRequestStatusUpdateRequest updateRequest,
                                                              Long userId,
                                                              Long eventId);

    List<ParticipationRequestDto> getRequestsByInitiator(Long userId, Long eventId);

}
