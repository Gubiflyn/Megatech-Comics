import {
  createContext,
  useContext,
  useMemo,
  useState,
} from 'react'

const OrderContext = createContext(null)

const ORDERS_STORAGE_KEY = 'megatech_orders'
const PAYMENTS_STORAGE_KEY = 'megatech_payments'

function cargarStorage(key) {
  try {
    const data = localStorage.getItem(key)

    return data ? JSON.parse(data) : []
  } catch {
    return []
  }
}

function generarId(lista) {
  if (lista.length === 0) {
    return 1
  }

  return Math.max(...lista.map((item) => item.id)) + 1
}

export function OrderProvider({ children }) {
  const [pedidos, setPedidos] = useState(() =>
    cargarStorage(ORDERS_STORAGE_KEY),
  )

  const [pagos, setPagos] = useState(() =>
    cargarStorage(PAYMENTS_STORAGE_KEY),
  )

  const guardarPedidos = (nuevosPedidos) => {
    localStorage.setItem(
      ORDERS_STORAGE_KEY,
      JSON.stringify(nuevosPedidos),
    )

    setPedidos(nuevosPedidos)
  }

  const guardarPagos = (nuevosPagos) => {
    localStorage.setItem(
      PAYMENTS_STORAGE_KEY,
      JSON.stringify(nuevosPagos),
    )

    setPagos(nuevosPagos)
  }

  const crearPedido = (
    usuarioId,
    itemsCarrito,
  ) => {
    /*
     * Leemos directamente localStorage para
     * evitar usar un estado antiguo de React.
     */
    const pedidosActuales =
      cargarStorage(ORDERS_STORAGE_KEY)

    const ahora = new Date().toISOString()

    const nuevoPedido = {
      id: generarId(pedidosActuales),
      usuarioId,
      estado: 'PENDIENTE_PAGO',
      fechaCreacion: ahora,
      fechaActualizacion: ahora,

      items: itemsCarrito.map(
        (item, index) => ({
          id: Date.now() + index,
          productoId: item.productoId,
          cantidad: item.cantidad,
        }),
      ),
    }

    const nuevosPedidos = [
      nuevoPedido,
      ...pedidosActuales,
    ]

    guardarPedidos(nuevosPedidos)

    return nuevoPedido
  }

  const procesarPago = (
    pedidoId,
    metodoPago,
    aprobarPago,
  ) => {
    /*
     * IMPORTANTE:
     *
     * Volvemos a leer localStorage porque
     * crearPedido() puede haberse ejecutado
     * inmediatamente antes y React todavía
     * podría no haber actualizado el estado.
     */
    const pedidosActuales =
      cargarStorage(ORDERS_STORAGE_KEY)

    const pagosActuales =
      cargarStorage(PAYMENTS_STORAGE_KEY)

    const pedido = pedidosActuales.find(
      (item) => item.id === pedidoId,
    )

    if (!pedido) {
      throw new Error('El pedido no existe')
    }

    if (pedido.estado !== 'PENDIENTE_PAGO') {
      throw new Error(
        'El pedido no se encuentra pendiente de pago',
      )
    }

    const existePagoAprobado =
      pagosActuales.some(
        (pago) =>
          pago.pedidoId === pedidoId &&
          pago.estado === 'APROBADO',
      )

    if (existePagoAprobado) {
      throw new Error(
        'El pedido ya posee un pago aprobado',
      )
    }

    const nuevoPago = {
      id: generarId(pagosActuales),
      pedidoId: pedido.id,
      usuarioId: pedido.usuarioId,
      metodoPago,

      estado: aprobarPago
        ? 'APROBADO'
        : 'RECHAZADO',

      codigoTransaccion:
        crypto.randomUUID(),

      mensaje: aprobarPago
        ? 'Pago aprobado correctamente'
        : 'Pago rechazado en la simulación',

      fechaCreacion:
        new Date().toISOString(),
    }

    const nuevosPagos = [
      nuevoPago,
      ...pagosActuales,
    ]

    guardarPagos(nuevosPagos)

    /*
     * Solo un pago aprobado cambia
     * el pedido a PAGADO.
     *
     * Si es rechazado se mantiene
     * PENDIENTE_PAGO.
     */
    if (aprobarPago) {
      const pedidosActualizados =
        pedidosActuales.map((item) =>
          item.id === pedidoId
            ? {
                ...item,
                estado: 'PAGADO',
                fechaActualizacion:
                  new Date().toISOString(),
              }
            : item,
        )

      guardarPedidos(pedidosActualizados)
    }

    return nuevoPago
  }

  const obtenerPagosPedido = (pedidoId) => {
    return pagos.filter(
      (pago) => pago.pedidoId === pedidoId,
    )
  }

  const pedidosOrdenados = useMemo(() => {
    return [...pedidos].sort(
      (a, b) =>
        new Date(b.fechaCreacion) -
        new Date(a.fechaCreacion),
    )
  }, [pedidos])

  const value = {
    pedidos: pedidosOrdenados,
    pagos,
    crearPedido,
    procesarPago,
    obtenerPagosPedido,
  }

  return (
    <OrderContext.Provider value={value}>
      {children}
    </OrderContext.Provider>
  )
}

export function useOrders() {
  const context = useContext(OrderContext)

  if (!context) {
    throw new Error(
      'useOrders debe utilizarse dentro de OrderProvider',
    )
  }

  return context
}