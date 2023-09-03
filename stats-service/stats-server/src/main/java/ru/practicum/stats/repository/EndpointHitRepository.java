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

    @Query("select new ru.practicum.stats.dto.ViewStatsResponseDto(" +
            "   eh.app, " +
            "   eh.uri, " +
            "   case when :unique = true " +
            "       then count(distinct(eh.ip)) " +
            "       else count(eh.ip) " +
            "   end " +
            ") " +
            "from EndpointHit eh " +
            "where eh.timestamp between :start and :end" +
            "   and (coalesce(:uris, null) is null or eh.uri in :uris) " +
            "group by eh.app, eh.uri " +
            "order by 3 desc")
    List<ViewStatsResponseDto> getStats(@Param("uris") Set<String> uris,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("unique") boolean unique);
}

