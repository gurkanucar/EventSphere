package com.gucardev.eventsphere.infrastructure.config.kafka;


import com.gucardev.eventsphere.infrastructure.exception.ExceptionLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaErrorConfiguration {

    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                this::handleKafkaError,
                new FixedBackOff(1000L, 3) // 3 retries with 1 second interval
        );

        // Don't retry on certain exceptions
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                NullPointerException.class
        );

        return errorHandler;
    }

    private void handleKafkaError(ConsumerRecord<?, ?> record, Exception ex) {
        log.error("[KAFKA] Failed to process message - topic: {}, partition: {}, offset: {}, key: {}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key());

        ExceptionLogger.logError(log, "KAFKA", ex);

        // Optional: send to dead letter topic or alert
    }
}