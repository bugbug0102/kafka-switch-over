package org.b0102.kafka.producer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
class KafkaProducerConfig {

  @Value("${kafka.primary.bootstrap-servers}")
  private String primaryBootstrapServers;

  @Value("${kafka.secondary.bootstrap-servers}")
  private String secondaryBootstrapServers;

  private ProducerFactory<String, String> createProducerFactory(final String servers) {
    final Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Primary
  @Bean("producer.factory.primary")
  ProducerFactory<String, String> producerFactoryPrimary() {
    return createProducerFactory(primaryBootstrapServers);
  }

  @Bean("producer.factory.secondary")
  ProducerFactory<String, String> producerFactorySecondary() {
    return createProducerFactory(secondaryBootstrapServers);
  }

  @Primary
  @Bean("kafka.template.primary")
  KafkaTemplate<String, String> kafkaTemplatePrimary() {
    return new KafkaTemplate<>(producerFactoryPrimary());
  }

  @Bean("kafka.template.secondary")
  KafkaTemplate<String, String> kafkaTemplateSecondary() {
    return new KafkaTemplate<>(producerFactorySecondary());
  }
}