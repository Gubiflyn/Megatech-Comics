import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react'

import {
  useIsAuthenticated,
  useMsal,
} from '@azure/msal-react'

import { loginRequest } from '../auth/msalConfig'

const OrderContext = createContext(null)

const API_BASE =
  'https://os3wsgjxhh.execute-api.us-east-1.amazonaws.com/api'

export function OrderProvider({ children }) {
  const { instance, accounts } = useMsal()
  const isAuthenticated = useIsAuthenticated()

  const [pedidos, setPedidos] = useState([])
  const [pagos, setPagos] = useState([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState('')

  const account = accounts[0]

  const obtenerAccessToken = useCallback(async () => {
    if (!isAuthenticated || !account) {
      throw new Error(
        'Debes iniciar sesión para consultar tus pedidos.',
      )
    }

    const response =
      await instance.acquireTokenSilent({
        ...loginRequest,
        account,
      })

    return response.accessToken
  }, [
    account,
    instance,
    isAuthenticated,
  ])

  const realizarPeticion = useCallback(
    async (url, options = {}) => {
      const accessToken =
        await obtenerAccessToken()

      const response = await fetch(url, {
        ...options,
        headers: {
          Authorization: `Bearer ${accessToken}`,
          ...(options.body
            ? {
                'Content-Type':
                  'application/json',
              }
            : {}),
          ...options.headers,
        },
      })

      if (response.status === 204) {
        return null
      }

      let data = null

      try {
        data = await response.json()
      } catch {
        data = null
      }

      if (!response.ok) {
        const requestError = new Error(
          data?.message ||
            `Error HTTP ${response.status}`,
        )

        requestError.status = response.status

        throw requestError
      }

      return data
    },
    [obtenerAccessToken],
  )

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
              const respuesta =
                await realizarPeticion(
                  `${API_BASE}/pagos/pedido/${pedido.id}`,
                )

              return Array.isArray(respuesta)
                ? respuesta
                : []
            } catch (err) {
              if (err.status === 404) {
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
    [realizarPeticion],
  )

  const cargarPedidos = useCallback(
    async () => {
      if (!isAuthenticated || !account) {
        setPedidos([])
        setPagos([])
        setError('')
        setCargando(false)
        return
      }

      try {
        setCargando(true)
        setError('')

        const respuesta =
          await realizarPeticion(
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
        console.error(
          'Error cargando pedidos:',
          err,
        )

        setPedidos([])
        setPagos([])

        setError(
          err.message ||
            'No fue posible cargar tus pedidos.',
        )
      } finally {
        setCargando(false)
      }
    },
    [
      account,
      cargarPagosDePedidos,
      isAuthenticated,
      realizarPeticion,
    ],
  )

  useEffect(() => {
    cargarPedidos()
  }, [cargarPedidos])

  const crearPedido = async () => {
    try {
      setError('')

      const pedido =
        await realizarPeticion(
          `${API_BASE}/pedidos`,
          {
            method: 'POST',
          },
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
        err.message ||
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

      const pago =
        await realizarPeticion(
          `${API_BASE}/pagos/pedido/${pedidoId}`,
          {
            method: 'POST',
            body: JSON.stringify({
              metodoPago,
              aprobarPago,
            }),
          },
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
        err.message ||
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