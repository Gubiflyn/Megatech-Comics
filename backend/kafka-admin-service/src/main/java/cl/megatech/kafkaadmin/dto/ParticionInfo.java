package cl.megatech.kafkaadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticionInfo {

    private int particion;
    private int lider;
    private List<Integer> replicas;
    private List<Integer> isr;

}
