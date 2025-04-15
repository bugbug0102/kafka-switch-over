package org.b0102.kafka.producer.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProducerService {

  private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

  enum Target
  {
    PRIMARY,
    SECONDARY
  }

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplatePrimary;

  @Autowired
  @Qualifier("kafka.template.secondary")
  private KafkaTemplate<String, String> kafkaTemplateSecondary;

  private final AtomicLong counter = new AtomicLong(1);
  private Target target = Target.PRIMARY;
  private KafkaTemplate<String, String> template = null;

  @PostConstruct
  void initialize()
  {
    template = kafkaTemplatePrimary;
  }

  public synchronized void switchOver()
  {
    target = target == Target.PRIMARY ? Target.SECONDARY : Target.PRIMARY;
    template = target == Target.PRIMARY ? kafkaTemplatePrimary : kafkaTemplateSecondary;
  }

  @Scheduled(fixedRate = 1000) // Send every second
  void sendMessage() {
    final String message = String.valueOf(counter.getAndIncrement());
    template.send("org.b0102.one.event", message + " " + target.name());
    logger.info("Produced: {}", message);
  }
}