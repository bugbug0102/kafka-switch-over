package org.b0102.kafka.consumer.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;


@Component
class SecondaryKafkaListenerAnnotationBeanPostProcessor<K,V> extends
    KafkaListenerAnnotationBeanPostProcessor<K,V> {

  private final static Logger logger = LoggerFactory.getLogger(
      SecondaryKafkaListenerAnnotationBeanPostProcessor.class);

  protected synchronized void processKafkaListener(KafkaListener kafkaListener, Method method, Object bean,
      String beanName) {
    
    final KafkaListener kl = new KafkaListener() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return kafkaListener.annotationType();
      }

      @Override
      public String id() {
        return UUID.randomUUID().toString();
      }

      @Override
      public String containerFactory() {
        return "listener.secondary";
      }

      @Override
      public String[] topics() {
        return kafkaListener.topics();
      }

      @Override
      public String topicPattern() {
        return kafkaListener.topicPattern();
      }

      @Override
      public TopicPartition[] topicPartitions() {
        return kafkaListener.topicPartitions();
      }

      @Override
      public String containerGroup() {
        return kafkaListener.containerGroup();
      }

      @Override
      public String errorHandler() {
        return kafkaListener.errorHandler();
      }

      @Override
      public String groupId() {
        return kafkaListener.groupId();
      }

      @Override
      public boolean idIsGroup() {
        return kafkaListener.idIsGroup();
      }

      @Override
      public String clientIdPrefix() {
        return kafkaListener.clientIdPrefix();
      }

      @Override
      public String beanRef() {
        return kafkaListener.beanRef();
      }

      @Override
      public String concurrency() {
        return kafkaListener.concurrency();
      }

      @Override
      public String autoStartup() {
        return kafkaListener.autoStartup();
      }

      @Override
      public String[] properties() {
        return kafkaListener.properties();
      }

      @Override
      public boolean splitIterables() {
        return kafkaListener.splitIterables();
      }

      @Override
      public String contentTypeConverter() {
        return kafkaListener.contentTypeConverter();
      }

      @Override
      public String batch() {
        return kafkaListener.batch();
      }

      @Override
      public String filter() {
        return kafkaListener.filter();
      }

      @Override
      public String info() {
        return kafkaListener.info();
      }

      @Override
      public String containerPostProcessor() {
        return kafkaListener.containerPostProcessor();
      }
    };

    /** Secondary */
    super.processKafkaListener(kl, method, bean, beanName);
  }

}
