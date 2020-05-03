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

        String json = "\"{\"id\":7,\"dataAgendamento\":{\"year\":2020,\"month\":\"MAY\",\"monthValue\":5,\"chronology\":{\"calendarType\":\"iso8601\",\"id\":\"ISO\"},\"dayOfMonth\":2,\"dayOfWeek\":\"SATURDAY\",\"era\":\"CE\",\"dayOfYear\":123,\"leapYear\":true},\"dataTransferencia\":{\"year\":2020,\"month\":\"MAY\",\"monthValue\":5,\"chronology\":{\"calendarType\":\"iso8601\",\"id\":\"ISO\"},\"dayOfMonth\":9,\"dayOfWeek\":\"SATURDAY\",\"era\":\"CE\",\"dayOfYear\":130,\"leapYear\":true},\"contaOrigem\":\"104736\",\"contaDestino\":\"204789\",\"valor\":10.0,\"valorTaxa\":84.0,\"status\":\"AGUARDANDO\"}\"; line: 1, column: 28] (through reference chain: sls.transferenciaconsumidor.kafka.modelo.Transferencia[\"dataAgendamento\"]) and the record is ConsumerRecord(topic = transfer-events, partition = 0, leaderEpoch = 0, offset = 1, CreateTime = 1588467983282, serialized key size = 1, serialized value size = 495, headers = RecordHeaders(headers = [RecordHeader(key = event-source, value = [115, 99, 97, 110, 110, 101, 114])], isReadOnly = false), key = 7, value = {\"id\":7,\"dataAgendamento\":{\"year\":2020,\"month\":\"MAY\",\"monthValue\":5,\"chronology\":{\"calendarType\":\"iso8601\",\"id\":\"ISO\"},\"dayOfMonth\":2,\"dayOfWeek\":\"SATURDAY\",\"era\":\"CE\",\"dayOfYear\":123,\"leapYear\":true},\"dataTransferencia\":{\"year\":2020,\"month\":\"MAY\",\"monthValue\":5,\"chronology\":{\"calendarType\":\"iso8601\",\"id\":\"ISO\"},\"dayOfMonth\":9,\"dayOfWeek\":\"SATURDAY\",\"era\":\"CE\",\"dayOfYear\":130,\"leapYear\":true},\"contaOrigem\":\"104736\",\"contaDestino\":\"204789\",\"valor\":10.0,\"valorTaxa\":84.0,\"status\":\"AGUARDANDO\"})";
        kafkaTemplate.sendDefault(json);

        new CountDownLatch(1).await(3, TimeUnit.SECONDS);

        verify(transferenciaConsumidor,times(1)).onMessage(isA(ConsumerRecord.class));
        verify(trasnferenciaEventoService,times(1)).processarTransferenciaEvento(isA(ConsumerRecord.class));

	//when

	//then
    }
}