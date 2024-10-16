package org.example;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.context.annotation.Primary;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.core.EventSerializer;
import org.springframework.modulith.events.core.PublicationTargetIdentifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Instant;

/**
 * Wrapper around {@link org.springframework.modulith.events.jpa.JpaEventPublicationRepository} only to throw exception
 * when marking event as completed fails.
 */
@Component
@Primary
@RequiredArgsConstructor
public class CustomEventPublicationRepository implements EventPublicationRepository {
    private static final String MARK_COMPLETED_BY_EVENT_AND_LISTENER_ID = """
			update JpaEventPublication p
			   set p.completionDate = ?3
			 where p.serializedEvent = ?1
			   and p.listenerId = ?2
			   and p.completionDate is null
			""";

    @Delegate
    private final EventPublicationRepository delegate;
    private final EntityManager entityManager;
    private final EventSerializer serializer;

    @Override
    public void markCompleted(Object event, PublicationTargetIdentifier identifier, Instant completionDate) {
        int updatedRows = entityManager.createQuery(MARK_COMPLETED_BY_EVENT_AND_LISTENER_ID)
                .setParameter(1, serializeEvent(event))
                .setParameter(2, identifier.getValue())
                .setParameter(3, completionDate)
                .executeUpdate();
        Assert.isTrue(updatedRows > 0, "did not update completion date for event: " + event);
    }

    private String serializeEvent(Object event) {
        return serializer.serialize(event).toString();
    }
}
