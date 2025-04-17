package org.b0102.kafka.producer.controller;

import org.b0102.kafka.producer.KafkaSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
class SwitchOverController {

  private static final Logger logger = LoggerFactory.getLogger(SwitchOverController.class);

  @Autowired
  private KafkaSelector kafkaSelector;

  @RequestMapping("/switch-over")
  void switchOver() {
    kafkaSelector.switchOver();
    logger.info("Switched to another Kafka cluster");
  }
}