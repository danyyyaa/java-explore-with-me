package ru.practicum.main.controller.publicController;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.service.EventService;

import javax.validation.Valid;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Valid
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public Object getEvents() {
        return null;
    }

    @GetMapping("/{id}")
    public Object getEventById(@PathVariable Long id) {
        return null;
    }
}
