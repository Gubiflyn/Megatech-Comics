package cl.megatech.kafkaadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerGroupDetalleResponse {

    private String id;
    private String estado;
    private String coordinador;
    private List<MiembroConsumerGroup> miembros;
    private List<LagTopicoParticion> lag;

}
