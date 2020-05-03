package sls.transferenciaconsumidor.kafka.configuracao;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class TransferenciaEventoListener {

    @Bean
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
                                                                                ConsumerFactory<Object, Object> kafkaConsumerFactory) {
	ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
	configurer.configure(factory, kafkaConsumerFactory);
	factory.setConcurrency(3);
	factory.setErrorHandler(((thrownException, data) -> log.info("Exception in consumerConfig is {} and the record is {}", thrownException.getMessage(), data)));
	factory.setRetryTemplate(retryTemplate());
	factory.setRecoveryCallback((context -> {
	    if (context.getLastThrowable().getCause() instanceof RecoverableDataAccessException) {
		log.info("Inside the recoverable logic");
		ConsumerRecord<String, String> consumerRecord = (ConsumerRecord<String, String>)context.getAttribute("record");
	    } else {
		log.info("Inside the non recoverable logic");
		throw new RuntimeException(context.getLastThrowable().getMessage());
	    }
	    return null;
	}));
	return factory;
    }

    private RetryTemplate retryTemplate() {
	FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
	fixedBackOffPolicy.setBackOffPeriod(1000);
	RetryTemplate retryTemplate = new RetryTemplate();
	retryTemplate.setRetryPolicy(simpleRetryPolicy());
	retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
	return retryTemplate;
    }

    private RetryPolicy simpleRetryPolicy() {
	Map<Class<? extends Throwable>, Boolean> exceptionsMap = new HashMap<>();
	exceptionsMap.put(IllegalArgumentException.class, false);
	exceptionsMap.put(RecoverableDataAccessException.class, true);
	SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(3, exceptionsMap, true);
	return simpleRetryPolicy;
    }

}
