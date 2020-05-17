package sls.transferenciaconsumidor.kafka.configuracao;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import lombok.extern.slf4j.Slf4j;
import sls.transferenciaconsumidor.kafka.servico.TrasnferenciaEventoService;

@Configuration
@EnableKafka
@Slf4j
public class TransferenciaEventoListener {

	@Autowired
	TrasnferenciaEventoService trasnferenciaEventoService;

	private static final String ATRIBUTO_RECORD = "record";

	@Bean
	ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
			ConsumerFactory<Object, Object> kafkaConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		configurer.configure(factory, kafkaConsumerFactory);
		factory.setErrorHandler(erroHandleLambda());
		factory.setRetryTemplate(retryTemplate());
		factory.setRecoveryCallback(recoveryCallBackLambda());
		return factory;
	}

	private RecoveryCallback<?> recoveryCallBackLambda() {
		return (context -> {
			if (context.getLastThrowable().getCause() instanceof RecoverableDataAccessException) {
				log.info("Inside the recoverable logic");
				ConsumerRecord<String, String> consumerRecord = (ConsumerRecord<String, String>) context.getAttribute(ATRIBUTO_RECORD);
				trasnferenciaEventoService.handleRecovery(consumerRecord);
			} else {
				log.info("Inside the non recoverable logic");
				throw new RuntimeException(context.getLastThrowable().getMessage());
			}
			return null;
		});
	}

	private ErrorHandler erroHandleLambda() {
		return ((thrownException, data) -> {
			log.info("Exception in consumerConfig is {} and the record is {}", thrownException.getMessage(), data);
		});
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
