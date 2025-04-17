package org.b0102.kafka.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
class KafkaConsumerConfig {

  @Value("${kafka.primary.bootstrap-servers}")
  private String primaryBootstrapServers;

  @Value("${kafka.secondary.bootstrap-servers}")
  private String secondaryBootstrapServers;

  @Bean
  ConsumerFactory<String, String> primaryConsumerFactory() {
    final Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, primaryBootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  ConsumerFactory<String, String> secondaryConsumerFactory() {
    final Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, secondaryBootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  @Primary
  ConcurrentKafkaListenerContainerFactory<String, String> primaryKafkaListenerContainerFactory() {
    final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(primaryConsumerFactory());
    return factory;
  }

  @Bean("listener.secondary")
  ConcurrentKafkaListenerContainerFactory<String, String> secondaryKafkaListenerContainerFactory() {
    final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(secondaryConsumerFactory());
    return factory;
  }

}