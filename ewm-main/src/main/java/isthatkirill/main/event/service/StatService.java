package isthatkirill.main.event.service;

import isthatkirill.main.event.model.Event;

import java.util.List;
import java.util.Map;

public interface StatService {

    void hit(String uri, String ip);

    Map<Long, Long> getViews(List<Event> events);

    Map<Long, Long> getConfirmedRequests(List<Event> events);

}
