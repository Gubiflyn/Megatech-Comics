import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react'

import apiClient from '../api/apiClient'

const OrderContext = createContext(null)

const API_BASE = `${
  import.meta.env.VITE_API_BASE_URL ||
  'http://localhost:8080'
}/api`

export function OrderProvider({ children }) {
  const [pedidos, setPedidos] = useState([])
  const [pagos, setPagos] = useState([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState('')

  const cargarPagosDePedidos = useCallback(
    async (listaPedidos) => {
      if (!listaPedidos?.length) {
        setPagos([])
        return
      }

      const resultados =
        await Promise.all(
          listaPedidos.map(async (pedido) => {
            try {
              const { data: respuesta } =
                await apiClient.get(
                  `${API_BASE}/pagos/pedido/${pedido.id}`,
                )

              return Array.isArray(respuesta)
                ? respuesta
                : []
            } catch (err) {
              if (err.response?.status === 404) {
                return []
              }

              console.error(
                `Error cargando pagos del pedido ${pedido.id}:`,
                err,
              )

              return []
            }
          }),
        )

      setPagos(resultados.flat())
    },
    [],
  )

  const cargarPedidos = useCallback(
    async () => {
      try {
        setCargando(true)
        setError('')

        const { data: respuesta } =
          await apiClient.get(
            `${API_BASE}/pedidos`,
          )

        const listaPedidos =
          Array.isArray(respuesta)
            ? respuesta
            : []

        setPedidos(listaPedidos)

        await cargarPagosDePedidos(
          listaPedidos,
        )
      } catch (err) {
        if (err.response?.status === 401) {
          setPedidos([])
          setPagos([])
          setError('')
          return
        }

        console.error(
          'Error cargando pedidos:',
          err,
        )

        setPedidos([])
        setPagos([])

        setError(
          err.response?.data?.message ||
            'No fue posible cargar tus pedidos.',
        )
      } finally {
        setCargando(false)
      }
    },
    [cargarPagosDePedidos],
  )

  useEffect(() => {
    cargarPedidos()
  }, [cargarPedidos])

  const crearPedido = async () => {
    try {
      setError('')

      const { data: pedido } =
        await apiClient.post(
          `${API_BASE}/pedidos`,
        )

      if (pedido) {
        setPedidos((actuales) => [
          pedido,
          ...actuales.filter(
            (item) =>
              item.id !== pedido.id,
          ),
        ])
      }

      return pedido
    } catch (err) {
      console.error(
        'Error creando pedido:',
        err,
      )

      setError(
        err.response?.data?.message ||
          'No fue posible crear el pedido.',
      )

      throw err
    }
  }

  const procesarPago = async (
    pedidoId,
    metodoPago,
    aprobarPago,
  ) => {
    try {
      setError('')

      const { data: pago } =
        await apiClient.post(
          `${API_BASE}/pagos/pedido/${pedidoId}`,
          { metodoPago, aprobarPago },
        )

      if (pago) {
        setPagos((actuales) => [
          pago,
          ...actuales.filter(
            (item) =>
              item.id !== pago.id,
          ),
        ])
      }

      /*
       * Un pago aprobado puede cambiar
       * el estado del pedido a PAGADO
       * en el backend, por lo que
       * recargamos los pedidos reales.
       */
      await cargarPedidos()

      return pago
    } catch (err) {
      console.error(
        'Error procesando pago:',
        err,
      )

      setError(
        err.response?.data?.message ||
          'No fue posible procesar el pago.',
      )

      throw err
    }
  }

  const obtenerPagosPedido = useCallback(
    (pedidoId) => {
      return pagos
        .filter(
          (pago) =>
            Number(pago.pedidoId) ===
            Number(pedidoId),
        )
        .sort(
          (a, b) =>
            new Date(b.fechaCreacion) -
            new Date(a.fechaCreacion),
        )
    },
    [pagos],
  )

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
    cargando,
    error,
    crearPedido,
    procesarPago,
    obtenerPagosPedido,
    cargarPedidos,
  }

  return (
    <OrderContext.Provider value={value}>
      {children}
    </OrderContext.Provider>
  )
}

export function useOrders() {
  const context =
    useContext(OrderContext)

  if (!context) {
    throw new Error(
      'useOrders debe utilizarse dentro de OrderProvider',
    )
  }

  return context
}
