package sls.transferenciaconsumidor.kafka.servico;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;
import sls.transferenciaconsumidor.kafka.consumidor.TransferenciaConsumidor;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(topics = {"transfer-events"},partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                                  "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
class TrasnferenciaEventoServiceTest {

    @Autowired EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired KafkaTemplate<String, String> kafkaTemplate;

    @Autowired KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean TrasnferenciaEventoService trasnferenciaEventoService;

    @SpyBean TransferenciaConsumidor transferenciaConsumidor;

    @BeforeEach
    void setaup(){
        endpointRegistry.getListenerContainers().forEach(messageListenerContainer ->
		ContainerTestUtils.waitForAssignment(messageListenerContainer,
		                                     embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    @Test void publicaNovaTransferencia()
                    throws InterruptedException, IOException {
        //given
        String json = "{\"id\":14,\"dataAgendamento\":\"03/05/2020\",\"dataTransferencia\":\"09/05/2020\",\"contaOrigem\":\"104736\",\"contaDestino\":\"204789\",\"valor\":10.0,\"valorTaxa\":72.0,\"status\":\"AGUARDANDO\"})";

	//when
        kafkaTemplate.sendDefault(json);
        new CountDownLatch(1).await(3, TimeUnit.SECONDS);

	//then
        verify(transferenciaConsumidor,times(1)).onMessage(isA(ConsumerRecord.class));
        verify(trasnferenciaEventoService,times(1)).processarTransferenciaEvento(isA(ConsumerRecord.class));

    }

    @Test void publicaNovaTransferenciaComRetentativa()
                    throws InterruptedException, IOException {
        //given
        Integer idTransferencia = 000;
        String json = "{\"id\":" + idTransferencia + ",\"dataAgendamento\":\"03/05/2020\",\"dataTransferencia\":\"09/05/2020\",\"contaOrigem\":\"104736\",\"contaDestino\":\"204789\",\"valor\":10.0,\"valorTaxa\":72.0,\"status\":\"AGUARDANDO\"})";

        //when
        kafkaTemplate.sendDefault(json);
        new CountDownLatch(1).await(3, TimeUnit.SECONDS);

        //then
        verify(trasnferenciaEventoService,times(4)).processarTransferenciaEvento(isA(ConsumerRecord.class));
        verify(trasnferenciaEventoService,times(1)).handleRecovery(isA(ConsumerRecord.class));

    }
}