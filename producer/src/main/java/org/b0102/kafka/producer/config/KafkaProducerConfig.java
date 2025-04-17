package org.b0102.kafka.producer.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.b0102.kafka.producer.KafkaSelector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
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

  private static class KafkaTemplateInterceptor implements MethodInterceptor
  {
    private final KafkaSelector kafkaSelector;
    private final KafkaTemplate<?,?> primary;
    private final KafkaTemplate<?,?> secondary;

    KafkaTemplateInterceptor(final KafkaSelector kafkaSelector, final KafkaTemplate<?,?> primary, final KafkaTemplate<?,?> secondary)
    {
      this.kafkaSelector = kafkaSelector;
      this.primary = primary;
      this.secondary = secondary;
    }

    @Override
    public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy)
        throws Throwable {
      final Method m = Arrays.stream(KafkaTemplate.class.getMethods()).filter( p -> p.equals(method)).findFirst().orElse(null);
      final Object o = kafkaSelector.getTarget() == KafkaSelector.Target.PRIMARY ? primary : secondary;
      return m.invoke(o, args);
    }
  }


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

  @Bean
  KafkaTemplate<String, String> kafkaTemplate(final KafkaSelector kafkaSelector)
  {
    final KafkaTemplate<?,?> primary = new KafkaTemplate<>(producerFactoryPrimary());
    final KafkaTemplate<?,?> secondary = new KafkaTemplate<>(producerFactorySecondary());
    final KafkaTemplateInterceptor kti = new KafkaTemplateInterceptor(kafkaSelector, primary, secondary);

    final Enhancer e = new Enhancer();
    e.setSuperclass(KafkaTemplate.class);
    e.setCallback(kti);
    e.setInterceptDuringConstruction(false);
    return (KafkaTemplate<String, String>) e.create(new Class[]{ProducerFactory.class}, new Object[]{new DefaultKafkaProducerFactory<String, String>(new HashMap<>())});
  }

}