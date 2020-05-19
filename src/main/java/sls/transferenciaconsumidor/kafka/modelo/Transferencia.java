package sls.transferenciaconsumidor.kafka.modelo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

