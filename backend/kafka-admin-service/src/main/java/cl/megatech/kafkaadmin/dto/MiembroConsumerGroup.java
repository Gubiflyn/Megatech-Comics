package cl.megatech.kafkaadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiembroConsumerGroup {

    private String clientId;
    private String host;
    private List<String> particionesAsignadas;

}
