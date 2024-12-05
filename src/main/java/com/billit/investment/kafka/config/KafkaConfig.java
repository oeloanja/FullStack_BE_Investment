package com.billit.investment.kafka.config;

import com.billit.investment.kafka.event.ExcessRefundEvent;
import com.billit.investment.kafka.event.SettlementCalculationEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public Map<String, Object> baseConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    @Bean
    public ConsumerFactory<String, SettlementCalculationEvent> settlementCalculationEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>(baseConsumerConfigs());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, CustomJsonDeserializer.class);
        props.put("value.deserializer.type", SettlementCalculationEvent.class);  // 추가된 부분

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new CustomJsonDeserializer<>(SettlementCalculationEvent.class)
        );
    }

    @Bean
    public ConsumerFactory<String, ExcessRefundEvent> excessRefundEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>(baseConsumerConfigs());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, CustomJsonDeserializer.class);
        props.put("value.deserializer.type", ExcessRefundEvent.class);  // 추가된 부분

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new CustomJsonDeserializer<>(ExcessRefundEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SettlementCalculationEvent>
    settlementCalculationEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SettlementCalculationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(settlementCalculationEventConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ExcessRefundEvent>
    excessRefundEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ExcessRefundEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(excessRefundEventConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                (consumerRecord, exception) -> {
                    log.error("Error in process with Exception {} and the record is {}",
                            exception, consumerRecord);
                },
                new FixedBackOff(3000L, 3)
        );
    }

    @Bean
    public ConsumerFactory<String, Integer> investmentDateUpdateEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>(baseConsumerConfigs());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, CustomJsonDeserializer.class);
        props.put("value.deserializer.type", Integer.class);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new CustomJsonDeserializer<>(Integer.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Integer>
    investmentDateUpdateEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Integer> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(investmentDateUpdateEventConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }
}

