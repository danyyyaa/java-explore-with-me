package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.entity.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
