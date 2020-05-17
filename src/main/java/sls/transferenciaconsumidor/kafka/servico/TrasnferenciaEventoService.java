package sls.transferenciaconsumidor.kafka.servico;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import sls.transferenciaconsumidor.kafka.modelo.ParametroEnvioKafka;
import sls.transferenciaconsumidor.kafka.modelo.PostEnvioMensagem;
import sls.transferenciaconsumidor.kafka.modelo.Transferencia;

import java.io.IOException;

@Service
@Slf4j
public class TrasnferenciaEventoService {

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	public void processarTransferenciaEvento(ConsumerRecord<String, String> consumerRecord) throws IOException {
		Transferencia transferencia = new ObjectMapper().readValue(consumerRecord.value(), Transferencia.class);
		log.info("transferencia : {} ", transferencia);
		if (transferencia.getId() != null && transferencia.getId() == 000) {
			throw new RecoverableDataAccessException("Temporary Network Issue");
		}
	}

	public void handleRecovery(ConsumerRecord<String, String> consumerRecord) {
		ParametroEnvioKafka parametroEnvioKafka = criarParametroKafka(consumerRecord);
		ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate
				.sendDefault(parametroEnvioKafka.getKey(), parametroEnvioKafka.getValue());
		listenableFuture
				.addCallback(new PostEnvioMensagem(parametroEnvioKafka.getKey(), parametroEnvioKafka.getValue()));
	}

	private ParametroEnvioKafka criarParametroKafka(ConsumerRecord<String, String> consumerRecord) {
		return new ParametroEnvioKafka().comKey(consumerRecord.key()).comValue(consumerRecord.value());
	}

}
