package sls.transferenciaconsumidor.kafka.consumidor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sls.transferenciaconsumidor.kafka.servico.TrasnferenciaEventoService;

import java.io.IOException;

@Component
@Slf4j
public class TransferenciaConsumidor {

    @Autowired
    private TrasnferenciaEventoService trasnferenciaEventoService;

    @KafkaListener(topics = {"transfer-events"}, id = "transferencia-grupo")
    public void onMessage(ConsumerRecord<String,String> consumerRecord) throws IOException {
	log.info("ConsumerRecord : {} ", consumerRecord );
	trasnferenciaEventoService.processarTransferenciaEvento(consumerRecord);
    }

}
