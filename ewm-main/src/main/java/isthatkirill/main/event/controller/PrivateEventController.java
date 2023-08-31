package isthatkirill.main.event.controller;

import isthatkirill.main.event.dto.EventFullDto;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.event.dto.NewEventDto;
import isthatkirill.main.event.dto.UpdateEventDto;
import isthatkirill.main.event.service.EventService;
import isthatkirill.main.request.dto.EventRequestStatusUpdateRequestDto;
import isthatkirill.main.request.dto.EventRequestStatusUpdateResultDto;
import isthatkirill.main.request.dto.ParticipationRequestDto;
import isthatkirill.main.request.service.RequestService;
import isthatkirill.main.validation.group.OnCreate;
import isthatkirill.main.validation.group.OnUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@Validated(OnCreate.class) @RequestBody NewEventDto newEventDto, @PathVariable Long userId) {
        return eventService.create(newEventDto, userId);
    }

    @GetMapping
    public List<EventShortDto> getAllByInitiatorId(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10", required = false) @Positive Integer size) {
        return eventService.getAllByInitiatorId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdAndInitiatorId(@PathVariable Long eventId, @PathVariable Long userId) {
        return eventService.getEventByIdAndInitiatorId(eventId, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByInitiator(@Validated(OnUpdate.class) @RequestBody UpdateEventDto updatedEvent,
                                          @PathVariable Long eventId,
                                          @PathVariable Long userId) {
        return eventService.updateByInitiator(updatedEvent, eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto processRequestsByInitiator(@RequestBody @Valid EventRequestStatusUpdateRequestDto updateRequest,
                                                                        @PathVariable Long userId,
                                                                        @PathVariable Long eventId) {
        return requestService.processRequestsByInitiator(updateRequest, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByInitiator(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getRequestsByInitiator(userId, eventId);
    }

}
