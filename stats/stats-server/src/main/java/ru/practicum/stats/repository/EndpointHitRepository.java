package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stats.entity.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
}
