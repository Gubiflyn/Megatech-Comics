import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'

import { useCart } from '../context/CartContext'

const CATALOGO_API =
  'https://os3wsgjxhh.execute-api.us-east-1.amazonaws.com/api/catalogo'

function formatearPrecio(precio) {
  return new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
  }).format(precio)
}

function CartPage() {
  const {
    items,
    cargando: cargandoCarrito,
    error: errorCarrito,
    actualizarCantidad,
    eliminarItem,
    vaciarCarrito,
  } = useCart()

  const [comics, setComics] = useState([])
  const [cargandoCatalogo, setCargandoCatalogo] =
    useState(true)
  const [errorCatalogo, setErrorCatalogo] =
    useState('')
  const [procesando, setProcesando] =
    useState(false)

  useEffect(() => {
    const cargarCatalogo = async () => {
      try {
        setCargandoCatalogo(true)
        setErrorCatalogo('')

        const response = await fetch(CATALOGO_API)

        if (!response.ok) {
          throw new Error(
            `Error consultando catálogo: ${response.status}`,
          )
        }

        const data = await response.json()

        setComics(Array.isArray(data) ? data : [])
      } catch (err) {
        console.error(
          'Error cargando catálogo para carrito:',
          err,
        )

        setErrorCatalogo(
          'No fue posible obtener la información de los productos.',
        )
      } finally {
        setCargandoCatalogo(false)
      }
    }

    cargarCatalogo()
  }, [])

  const itemsCompletos = useMemo(() => {
    return items
      .map((item) => {
        const comic = comics.find(
          (producto) =>
            Number(producto.id) ===
            Number(item.productoId),
        )

        if (!comic) {
          return null
        }

        return {
          ...item,
          comic,
        }
      })
      .filter(Boolean)
  }, [items, comics])

  const subtotal = useMemo(() => {
    return itemsCompletos.reduce(
      (total, item) =>
        total +
        Number(item.comic.precio) *
          Number(item.cantidad),
      0,
    )
  }, [itemsCompletos])

  const cambiarCantidad = async (
    productoId,
    cantidad,
  ) => {
    if (cantidad < 1 || procesando) {
      return
    }

    try {
      setProcesando(true)

      await actualizarCantidad(
        productoId,
        cantidad,
      )
    } catch (err) {
      console.error(
        'Error cambiando cantidad:',
        err,
      )
    } finally {
      setProcesando(false)
    }
  }

  const quitarProducto = async (
    productoId,
  ) => {
    if (procesando) {
      return
    }

    try {
      setProcesando(true)

      await eliminarItem(productoId)
    } catch (err) {
      console.error(
        'Error eliminando producto:',
        err,
      )
    } finally {
      setProcesando(false)
    }
  }

  const limpiarCarrito = async () => {
    if (procesando) {
      return
    }

    try {
      setProcesando(true)

      await vaciarCarrito()
    } catch (err) {
      console.error(
        'Error vaciando carrito:',
        err,
      )
    } finally {
      setProcesando(false)
    }
  }

  if (
    cargandoCarrito ||
    cargandoCatalogo
  ) {
    return (
      <main className="cart-page">
        <section className="cart-header">
          <span className="page-label">
            TU COMPRA
          </span>

          <h1>Carrito</h1>

          <p>
            Cargando los productos de tu carrito...
          </p>
        </section>
      </main>
    )
  }

  if (errorCarrito) {
    return (
      <main className="cart-page">
        <section className="cart-header">
          <span className="page-label">
            ERROR
          </span>

          <h1>No pudimos cargar tu carrito</h1>

          <p>{errorCarrito}</p>
        </section>
      </main>
    )
  }

  if (errorCatalogo) {
    return (
      <main className="cart-page">
        <section className="cart-header">
          <span className="page-label">
            ERROR
          </span>

          <h1>
            No pudimos cargar los productos
          </h1>

          <p>{errorCatalogo}</p>

          <Link
            to="/catalogo"
            className="primary-button"
          >
            Volver al catálogo
          </Link>
        </section>
      </main>
    )
  }

  if (itemsCompletos.length === 0) {
    return (
      <main className="cart-page">
        <section className="cart-header">
          <span className="page-label">
            TU COMPRA
          </span>

          <h1>Carrito</h1>

          <p>
            Revisa los productos que has agregado
            antes de continuar con tu compra.
          </p>
        </section>

        <section className="empty-cart">
          <div className="empty-cart-icon">
            🛒
          </div>

          <h2>Tu carrito está vacío</h2>

          <p>
            Todavía no has agregado ningún cómic.
          </p>

          <Link
            to="/catalogo"
            className="primary-button"
          >
            Explorar catálogo
          </Link>
        </section>
      </main>
    )
  }

  return (
    <main className="cart-page">
      <section className="cart-header">
        <div>
          <span className="page-label">
            TU COMPRA
          </span>

          <h1>Carrito</h1>

          <p>
            Revisa tus cómics antes de continuar
            con el pedido.
          </p>
        </div>

        <button
          type="button"
          className="clear-cart-button"
          onClick={limpiarCarrito}
          disabled={procesando}
        >
          {procesando
            ? 'Procesando...'
            : 'Vaciar carrito'}
        </button>
      </section>

      <div className="cart-layout">
        <section className="cart-items">
          {itemsCompletos.map(
            ({ comic, cantidad }) => {
              const totalItem =
                Number(comic.precio) *
                Number(cantidad)

              return (
                <article
                  key={comic.id}
                  className="cart-item"
                >
                  <Link
                    to={`/catalogo/${comic.id}`}
                    className="cart-item-cover"
                  >
                    <div className="comic-cover-pattern" />

                    <span>
                      {comic.tipo}
                    </span>

                    <strong>
                      {comic.titulo}
                    </strong>
                  </Link>

                  <div className="cart-item-info">
                    <div className="cart-item-top">
                      <div>
                        <span>
                          {comic.genero}
                        </span>

                        <Link
                          to={`/catalogo/${comic.id}`}
                        >
                          <h2>
                            {comic.titulo}
                          </h2>
                        </Link>

                        <p>
                          {comic.edicion}
                          {comic.tomo
                            ? ` · Tomo ${comic.tomo}`
                            : ''}
                        </p>
                      </div>

                      <button
                        type="button"
                        className="remove-item-button"
                        onClick={() =>
                          quitarProducto(
                            comic.id,
                          )
                        }
                        disabled={procesando}
                        aria-label={`Eliminar ${comic.titulo}`}
                      >
                        ✕
                      </button>
                    </div>

                    <div className="cart-item-bottom">
                      <div className="quantity-control">
                        <button
                          type="button"
                          onClick={() =>
                            cambiarCantidad(
                              comic.id,
                              cantidad - 1,
                            )
                          }
                          disabled={
                            cantidad <= 1 ||
                            procesando
                          }
                        >
                          −
                        </button>

                        <span>
                          {cantidad}
                        </span>

                        <button
                          type="button"
                          onClick={() =>
                            cambiarCantidad(
                              comic.id,
                              cantidad + 1,
                            )
                          }
                          disabled={procesando}
                        >
                          +
                        </button>
                      </div>

                      <div className="cart-item-price">
                        <span>
                          {formatearPrecio(
                            comic.precio,
                          )}{' '}
                          c/u
                        </span>

                        <strong>
                          {formatearPrecio(
                            totalItem,
                          )}
                        </strong>
                      </div>
                    </div>
                  </div>
                </article>
              )
            },
          )}
        </section>

        <aside className="cart-summary">
          <span className="summary-label">
            RESUMEN
          </span>

          <h2>Resumen del pedido</h2>

          <div className="summary-row">
            <span>Productos</span>

            <strong>
              {itemsCompletos.reduce(
                (total, item) =>
                  total +
                  Number(item.cantidad),
                0,
              )}
            </strong>
          </div>

          <div className="summary-row">
            <span>Subtotal</span>

            <strong>
              {formatearPrecio(subtotal)}
            </strong>
          </div>

          <div className="summary-row">
            <span>Envío</span>

            <strong>
              Por definir
            </strong>
          </div>

          <div className="summary-total">
            <span>Total productos</span>

            <strong>
              {formatearPrecio(subtotal)}
            </strong>
          </div>

          <Link
            to="/checkout"
            className="checkout-button"
          >
            Continuar compra
          </Link>

          <Link
            to="/catalogo"
            className="continue-shopping"
          >
            ← Seguir comprando
          </Link>
        </aside>
      </div>
    </main>
  )
}

export default CartPage