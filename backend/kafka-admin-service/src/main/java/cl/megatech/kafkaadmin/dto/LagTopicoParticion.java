package cl.megatech.kafkaadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LagTopicoParticion {

    private String topico;
    private int particion;
    private long offsetFinal;
    private long offsetConfirmado;
    private long lag;

}
