package cl.megatech.kafkaadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicoInfoResponse {

    private String nombre;
    private List<ParticionInfo> particiones;
    private Map<String, String> configuraciones;

}
