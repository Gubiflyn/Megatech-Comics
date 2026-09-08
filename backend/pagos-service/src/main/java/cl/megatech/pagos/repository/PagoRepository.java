package cl.megatech.pagos.repository;

import cl.megatech.pagos.model.EstadoPago;
import cl.megatech.pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByPedidoIdOrderByFechaCreacionDesc(Long pedidoId);

    boolean existsByPedidoIdAndEstado(
            Long pedidoId,
            EstadoPago estado
    );
}