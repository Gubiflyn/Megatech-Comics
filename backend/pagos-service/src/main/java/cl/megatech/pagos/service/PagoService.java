package cl.megatech.pagos.service;

import cl.megatech.pagos.client.PedidosClient;
import cl.megatech.pagos.dto.PedidoResponse;
import cl.megatech.pagos.dto.ProcesarPagoRequest;
import cl.megatech.pagos.exception.PagoNoPermitidoException;
import cl.megatech.pagos.exception.RecursoNoEncontradoException;
import cl.megatech.pagos.model.EstadoPago;
import cl.megatech.pagos.model.MetodoPago;
import cl.megatech.pagos.model.Pago;
import cl.megatech.pagos.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidosClient pedidosClient;

    public PagoService(
            PagoRepository pagoRepository,
            PedidosClient pedidosClient) {

        this.pagoRepository = pagoRepository;
        this.pedidosClient = pedidosClient;
    }

    @Transactional
    public Pago procesarPago(
            Long pedidoId,
            ProcesarPagoRequest request,
            String authorizationHeader) {

        PedidoResponse pedido =
                pedidosClient.obtenerPedido(
                        pedidoId,
                        authorizationHeader
                );

        validarPedidoParaPago(pedido);

        if (pagoRepository.existsByPedidoIdAndEstado(
                pedidoId,
                EstadoPago.APROBADO)) {

            throw new PagoNoPermitidoException(
                    "El pedido ya posee un pago aprobado"
            );
        }

        MetodoPago metodoPago;

        try {

            metodoPago = MetodoPago.valueOf(
                    request.getMetodoPago()
                            .trim()
                            .toUpperCase()
            );

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Método de pago inválido. Valores permitidos: "
                            + "TARJETA, TRANSFERENCIA, WEBPAY_SIMULADO"
            );
        }

        Pago pago = new Pago();

        pago.setPedidoId(pedido.getId());
        pago.setUsuarioId(pedido.getUsuarioId());
        pago.setMetodoPago(metodoPago);

        if (Boolean.FALSE.equals(request.getAprobarPago())) {

            pago.setEstado(EstadoPago.RECHAZADO);
            pago.setMensaje("Pago rechazado en la simulación");

            return pagoRepository.save(pago);
        }

        pago.setEstado(EstadoPago.APROBADO);
        pago.setMensaje("Pago aprobado correctamente");

        Pago pagoGuardado =
                pagoRepository.save(pago);

        pedidosClient.marcarComoPagado(
                pedidoId,
                authorizationHeader
        );

        return pagoGuardado;
    }

    public Pago obtenerPorId(Long pagoId) {

        return pagoRepository.findById(pagoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el pago " + pagoId
                        )
                );
    }

    public List<Pago> obtenerPorPedido(
            Long pedidoId) {

        return pagoRepository
                .findByPedidoIdOrderByFechaCreacionDesc(
                        pedidoId
                );
    }

    private void validarPedidoParaPago(
            PedidoResponse pedido) {

        if (!"PENDIENTE_PAGO".equalsIgnoreCase(
                pedido.getEstado())) {

            throw new PagoNoPermitidoException(
                    "El pedido no se encuentra PENDIENTE_PAGO. "
                            + "Estado actual: "
                            + pedido.getEstado()
            );
        }
    }
}