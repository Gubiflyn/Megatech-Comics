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

const CartContext = createContext(null)

const API_URL =
  'https://os3wsgjxhh.execute-api.us-east-1.amazonaws.com/api/carritos'

export function CartProvider({ children }) {
  const { instance, accounts } = useMsal()
  const isAuthenticated = useIsAuthenticated()

  const [items, setItems] = useState([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState('')

  const account = accounts[0]

  const usuarioId =
    account?.localAccountId || null

  const obtenerAccessToken = useCallback(async () => {
    if (!isAuthenticated || !account) {
      throw new Error(
        'Debes iniciar sesión para utilizar el carrito.',
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

  const cargarCarrito = useCallback(
    async () => {
      if (!isAuthenticated || !usuarioId) {
        setItems([])
        setCargando(false)
        setError('')
        return
      }

      try {
        setCargando(true)
        setError('')

        const carrito =
          await realizarPeticion(
            `${API_URL}/${encodeURIComponent(
              usuarioId,
            )}`,
          )

        setItems(carrito?.items || [])
      } catch (err) {
        if (err.status === 404) {
          setItems([])
          setError('')
          return
        }

        console.error(
          'Error cargando carrito:',
          err,
        )

        setItems([])
        setError(
          err.message ||
            'No fue posible cargar el carrito.',
        )
      } finally {
        setCargando(false)
      }
    },
    [
      isAuthenticated,
      realizarPeticion,
      usuarioId,
    ],
  )

  useEffect(() => {
    cargarCarrito()
  }, [cargarCarrito])

  const agregarItem = async (
    productoId,
    cantidad = 1,
  ) => {
    try {
      setError('')

      const carrito =
        await realizarPeticion(
          `${API_URL}/${encodeURIComponent(
            usuarioId,
          )}/items`,
          {
            method: 'POST',
            body: JSON.stringify({
              productoId,
              cantidad,
            }),
          },
        )

      setItems(carrito?.items || [])

      return carrito
    } catch (err) {
      console.error(
        'Error agregando producto:',
        err,
      )

      setError(
        err.message ||
          'No fue posible agregar el producto.',
      )

      throw err
    }
  }

  const actualizarCantidad = async (
    productoId,
    cantidad,
  ) => {
    if (cantidad < 1) {
      return
    }

    try {
      setError('')

      const carrito =
        await realizarPeticion(
          `${API_URL}/${encodeURIComponent(
            usuarioId,
          )}/items/${productoId}`,
          {
            method: 'PUT',
            body: JSON.stringify({
              cantidad,
            }),
          },
        )

      setItems(carrito?.items || [])

      return carrito
    } catch (err) {
      console.error(
        'Error actualizando cantidad:',
        err,
      )

      setError(
        err.message ||
          'No fue posible actualizar la cantidad.',
      )

      throw err
    }
  }

  const eliminarItem = async (
    productoId,
  ) => {
    try {
      setError('')

      const carrito =
        await realizarPeticion(
          `${API_URL}/${encodeURIComponent(
            usuarioId,
          )}/items/${productoId}`,
          {
            method: 'DELETE',
          },
        )

      setItems(carrito?.items || [])

      return carrito
    } catch (err) {
      console.error(
        'Error eliminando producto:',
        err,
      )

      setError(
        err.message ||
          'No fue posible eliminar el producto.',
      )

      throw err
    }
  }

  const vaciarCarrito = async () => {
    try {
      setError('')

      await realizarPeticion(
        `${API_URL}/${encodeURIComponent(
          usuarioId,
        )}`,
        {
          method: 'DELETE',
        },
      )

      setItems([])
    } catch (err) {
      console.error(
        'Error vaciando carrito:',
        err,
      )

      setError(
        err.message ||
          'No fue posible vaciar el carrito.',
      )

      throw err
    }
  }

  const cantidadTotal = useMemo(() => {
    return items.reduce(
      (total, item) =>
        total + item.cantidad,
      0,
    )
  }, [items])

  const value = {
    items,
    cargando,
    error,
    usuarioId,
    agregarItem,
    actualizarCantidad,
    eliminarItem,
    vaciarCarrito,
    cargarCarrito,
    cantidadTotal,
  }

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  )
}

export function useCart() {
  const context =
    useContext(CartContext)

  if (!context) {
    throw new Error(
      'useCart debe utilizarse dentro de CartProvider',
    )
  }

  return context
}