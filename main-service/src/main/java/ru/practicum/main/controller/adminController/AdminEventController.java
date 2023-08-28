package ru.practicum.main.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Valid
@Validated
public class AdminEventController {

    @GetMapping
    public Object getEvents() {
        return null;
    }

    @PatchMapping("/{eventId}")
    public Object changeEvent(@PathVariable Long eventId) {
        return null;
    }
}
