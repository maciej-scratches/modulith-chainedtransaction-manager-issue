package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FooService {
    private static final Logger log = LoggerFactory.getLogger(FooService.class);
    private final ApplicationEventPublisher eventPublisher;

    public FooService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    void foo() {
        log.info("Enter foo");
        SomethingHappened event = new SomethingHappened(UUID.randomUUID().toString());
        log.info("Publishing: {}", event);
        eventPublisher.publishEvent(event);
        log.info("Exit foo");
    }
}
