package ru.practicum.main.event.service;

import ru.practicum.main.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface StatService {

    void hit(HttpServletRequest httpServletRequest);

    Map<Long, Long> getViews(List<Event> events);

}
