package sls.transferenciaconsumidor.kafka.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Transferencia {

    private Long id;

    private LocalDate dataAgendamento;

    private LocalDate dataTransferencia;

    private String contaOrigem;

    private String contaDestino;

    private BigDecimal valor;

    private BigDecimal valorTaxa;

    private StatusTransferencia status;

}

