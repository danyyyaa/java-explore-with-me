package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.dto.ViewStatsResponseDto;
import ru.practicum.stats.entity.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.stats.dto.ViewStatsResponseDto(eh.app, eh.uri, count(eh.ip))" +
            " from EndpointHit eh" +
            " where eh.timestamp between :start and :end" +
            " and (coalesce(:uris, null) is null or eh.uri in :uris)" +
            " group by eh.app, eh.uri" +
            " order by count(eh.ip) desc")
    List<ViewStatsResponseDto> getAllByTimestampAndUriNotUnique(@Param("uris") Set<String> uris,
                                                                @Param("start") LocalDateTime start,
                                                                @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.stats.dto.ViewStatsResponseDto(eh.app, eh.uri, count(distinct eh.ip))" +
            " from EndpointHit eh" +
            " where eh.timestamp between :start and :end" +
            " and (coalesce(:uris, null) is null or eh.uri in :uris)" +
            " group by eh.app, eh.uri" +
            " order by count(distinct eh.ip) desc")
    List<ViewStatsResponseDto> getAllHitsByTimestampAndUriUnique(@Param("uris") Set<String> uris,
                                                                 @Param("start") LocalDateTime start,
                                                                 @Param("end") LocalDateTime end);
}

