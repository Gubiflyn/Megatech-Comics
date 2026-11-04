package cl.megatech.kafkaadmin.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AumentarParticionesRequest {

    @Min(value = 1, message = "debe ser mayor o igual a 1")
    private int nuevoNumeroParticiones;

}
