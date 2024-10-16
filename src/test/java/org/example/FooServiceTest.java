package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.events.EventPublication;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.core.TargetEventPublication;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FooServiceTest {
    @Autowired
    private FooService fooService;
    @Autowired
    private BarService barService;
    @Autowired
    private EventPublicationRepository eventPublicationRepository;

    @BeforeEach
    void init() {
        var ids = eventPublicationRepository.findIncompletePublications().stream().map(EventPublication::getIdentifier).toList();
        eventPublicationRepository.deletePublications(ids);
    }

    @Test
    void foo() {
        fooService.foo();

        allEventsAreCompleted();
    }

    @RepeatedTest(100)
    void bar() {
        barService.bar();
        allEventsAreCompleted();
    }

    private void allEventsAreCompleted() {
        await().atMost(Duration.ofMillis(150)).untilAsserted(() -> {
            assertThat(eventPublicationRepository.findIncompletePublications()).isEmpty();
        });
    }

}