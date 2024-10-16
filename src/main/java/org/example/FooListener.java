package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class FooListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FooListener.class);

    @ApplicationModuleListener
    void handle(SomethingHappened event) {
        LOGGER.info("Handling: {}", event);
    }
}
