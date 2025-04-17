package org.b0102.kafka.consumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
class ConsumerService {

  private static Logger logger = LoggerFactory.getLogger(ConsumerService.class);

  @KafkaListener(topics = "org.b0102.one.event", groupId = "consumer")
  //@KafkaListener(topics = "org.b0102.one.event", containerFactory = "listener.secondary")
  public void consume(String message) {
    logger.info("Received : {}", message);
  }
}