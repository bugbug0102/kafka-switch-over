package org.b0102.kafka.producer.service;

import org.b0102.kafka.producer.KafkaSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
class ProducerService {

  private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

  private final AtomicLong counter = new AtomicLong(1);

  @Autowired
  private KafkaTemplate<String, String> template;

  @Autowired
  private KafkaSelector kafkaSelector;

  @Scheduled(fixedRate = 1000) // Send every second
  void sendMessage() {
    final String message = String.valueOf(counter.getAndIncrement());
    template.send("org.b0102.one.event", message + " " + kafkaSelector.getTarget().name());
    logger.info("Produced: {}", message);
  }
}