import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react'

import apiClient from '../api/apiClient'

const CartContext = createContext(null)

const API_URL = `${
  import.meta.env.VITE_API_BASE_URL ||
  'http://localhost:8080'
}/api/carrito`

export function CartProvider({ children }) {
  const [items, setItems] = useState([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState('')

  const cargarCarrito = useCallback(
    async () => {
      try {
        setCargando(true)
        setError('')

        const { data: carrito } =
          await apiClient.get(API_URL)

        setItems(carrito?.items || [])
      } catch (err) {
        if (
          err.response?.status === 404 ||
          err.response?.status === 401
        ) {
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
          err.response?.data?.message ||
            'No fue posible cargar el carrito.',
        )
      } finally {
        setCargando(false)
      }
    },
    [],
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

      const { data: carrito } =
        await apiClient.post(
          `${API_URL}/items`,
          { productoId, cantidad },
        )

      setItems(carrito?.items || [])

      return carrito
    } catch (err) {
      console.error(
        'Error agregando producto:',
        err,
      )

      setError(
        err.response?.data?.message ||
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

      const { data: carrito } =
        await apiClient.put(
          `${API_URL}/items/${productoId}`,
          { cantidad },
        )

      setItems(carrito?.items || [])

      return carrito
    } catch (err) {
      console.error(
        'Error actualizando cantidad:',
        err,
      )

      setError(
        err.response?.data?.message ||
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

      const { data: carrito } =
        await apiClient.delete(
          `${API_URL}/items/${productoId}`,
        )

      setItems(carrito?.items || [])

      return carrito
    } catch (err) {
      console.error(
        'Error eliminando producto:',
        err,
      )

      setError(
        err.response?.data?.message ||
          'No fue posible eliminar el producto.',
      )

      throw err
    }
  }

  const vaciarCarrito = async () => {
    try {
      setError('')

      await apiClient.delete(API_URL)

      setItems([])
    } catch (err) {
      console.error(
        'Error vaciando carrito:',
        err,
      )

      setError(
        err.response?.data?.message ||
          'No fue posible vaciar el carrito.',
      )

      throw err
    }
  }

  const cantidadTotal = useMemo(() => {
    return items.reduce(
      (total, item) =>
        total + (item.cantidad || 0),
      0,
    )
  }, [items])

  const value = {
    items,
    cargando,
    error,
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
