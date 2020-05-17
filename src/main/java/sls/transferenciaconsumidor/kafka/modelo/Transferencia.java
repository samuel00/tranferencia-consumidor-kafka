package sls.transferenciaconsumidor.kafka.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import sls.transferenciaconsumidor.kafka.modelo.util.LocalDateDeserializer;
import sls.transferenciaconsumidor.kafka.modelo.util.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Transferencia {

    private Long id;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataAgendamento;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataTransferencia;

    private String contaOrigem;

    private String contaDestino;

    private BigDecimal valor;

    private BigDecimal valorTaxa;

    private StatusTransferencia status;

}

