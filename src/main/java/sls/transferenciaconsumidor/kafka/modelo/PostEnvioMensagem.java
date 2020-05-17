package sls.transferenciaconsumidor.kafka.modelo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public class PostEnvioMensagem implements ListenableFutureCallback<SendResult<String, String>> {

	private final String key;
	private final String value;

	public PostEnvioMensagem(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public void onFailure(Throwable ex) {
		log.error("Error Sending the Message and the exception is {}", ex.getMessage());
		try {
			throw ex;
		} catch (Throwable throwable) {
			log.error("Error in OnFailure: {}", throwable.getMessage());
		}
	}

	@Override
	public void onSuccess(SendResult<String, String> result) {
		log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", this.key,
				this.value, result.getRecordMetadata().partition());
	}

}
