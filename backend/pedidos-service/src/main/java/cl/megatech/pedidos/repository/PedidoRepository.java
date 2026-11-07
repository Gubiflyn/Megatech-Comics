package cl.megatech.pedidos.repository;

import cl.megatech.pedidos.model.Pedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.id = :pedidoId")
    Optional<Pedido> buscarPorIdParaActualizar(@Param("pedidoId") Long pedidoId);

    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(String usuarioId);
}