package sls.transferenciaconsumidor.kafka.modelo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Data
@Slf4j
public class ParametroEnvioKafka {

    private String key;
    private String value;

    public ParametroEnvioKafka comKey(String key) {
	this.key = key;
	return this;
    }

    public ParametroEnvioKafka comValue(String value) {
	    this.value = value;
	return this;
    }

}

