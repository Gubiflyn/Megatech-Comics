import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react'

const CartContext = createContext(null)

const STORAGE_KEY = 'megatech_cart'

function cargarCarrito() {
  try {
    const carritoGuardado = localStorage.getItem(STORAGE_KEY)

    return carritoGuardado
      ? JSON.parse(carritoGuardado)
      : []
  } catch {
    return []
  }
}

export function CartProvider({ children }) {
  const [items, setItems] = useState(cargarCarrito)

  useEffect(() => {
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify(items),
    )
  }, [items])

  const agregarItem = (productoId, cantidad = 1) => {
    setItems((actuales) => {
      const existente = actuales.find(
        (item) => item.productoId === productoId,
      )

      if (existente) {
        return actuales.map((item) =>
          item.productoId === productoId
            ? {
                ...item,
                cantidad: item.cantidad + cantidad,
              }
            : item,
        )
      }

      return [
        ...actuales,
        {
          productoId,
          cantidad,
        },
      ]
    })
  }

  const actualizarCantidad = (
    productoId,
    cantidad,
  ) => {
    if (cantidad < 1) {
      return
    }

    setItems((actuales) =>
      actuales.map((item) =>
        item.productoId === productoId
          ? {
              ...item,
              cantidad,
            }
          : item,
      ),
    )
  }

  const eliminarItem = (productoId) => {
    setItems((actuales) =>
      actuales.filter(
        (item) => item.productoId !== productoId,
      ),
    )
  }

  const vaciarCarrito = () => {
    setItems([])
  }

  const cantidadTotal = useMemo(() => {
    return items.reduce(
      (total, item) => total + item.cantidad,
      0,
    )
  }, [items])

  const value = {
    items,
    agregarItem,
    actualizarCantidad,
    eliminarItem,
    vaciarCarrito,
    cantidadTotal,
  }

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  )
}

export function useCart() {
  const context = useContext(CartContext)

  if (!context) {
    throw new Error(
      'useCart debe utilizarse dentro de CartProvider',
    )
  }

  return context
}