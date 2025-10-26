package org.example.contestrunner.amqp;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.*;

@Configuration
public class AmqpConfig {
    public static final String QUEUE_SUBMISSIONS = "submissions.queue";

    @Bean Queue submissionsQueue() { return QueueBuilder.durable(QUEUE_SUBMISSIONS).build(); }
}