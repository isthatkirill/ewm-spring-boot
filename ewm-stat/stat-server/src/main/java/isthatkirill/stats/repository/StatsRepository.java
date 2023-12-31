package isthatkirill.stats.repository;

import isthatkirill.stats.dto.ViewStatsDto;
import isthatkirill.stats.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new isthatkirill.stats.dto.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit as e " +
            "WHERE e.timestamp between ?1 AND ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new isthatkirill.stats.dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit as e " +
            "WHERE e.timestamp between ?1 AND ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStatsDto> getStatsUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new isthatkirill.stats.dto.ViewStatsDto(e.app, e.uri,COUNT(e.ip)) " +
            "FROM EndpointHit as e " +
            "WHERE e.timestamp between ?1 AND ?2 AND e.uri IN(?3)" +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStatsDto> getStatsForUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new isthatkirill.stats.dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit as e " +
            "WHERE e.timestamp between ?1 AND ?2 AND e.uri IN(?3)" +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStatsDto> getStatsUniqueIpForUris(LocalDateTime start, LocalDateTime end, List<String> uris);

}
