package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BarService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BarService.class);
    private final FooService fooService;

    public BarService(FooService fooService) {
        this.fooService = fooService;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    void bar() {
        LOGGER.info("enter bar");
        fooService.foo();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("exit bar");
    }
}
