package cl.megatech.kafkaadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterInfoResponse {

    private String id;
    private String controlador;
    private List<String> brokers;

}
