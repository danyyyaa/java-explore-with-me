package ru.practicum.main.entity;

import lombok.*;
import ru.practicum.main.entity.enums.EventPublishedStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation")
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @ToString.Exclude
    private Location location;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "participant_limit")
    private long participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @ToString.Exclude
    private User initiator;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventPublishedStatus state;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @PrePersist
    public void prePersist() {
        /*if (paid == null) {
            paid = false;
        }
        if (participantLimit == null) {
            participantLimit = 0;
        }*/
        if (requestModeration == null) {
            requestModeration = true;
        }
    }
}
