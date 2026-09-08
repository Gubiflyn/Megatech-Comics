import { Link } from 'react-router-dom'

import { comics } from '../data/comics'
import { useCart } from '../context/CartContext'

function formatearPrecio(precio) {
  return new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
  }).format(precio)
}

function CartPage() {
  const {
    items,
    actualizarCantidad,
    eliminarItem,
    vaciarCarrito,
  } = useCart()

  const itemsCompletos = items
    .map((item) => {
      const comic = comics.find(
        (producto) =>
          producto.id === item.productoId,
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

  const subtotal = itemsCompletos.reduce(
    (total, item) =>
      total +
      item.comic.precio * item.cantidad,
    0,
  )

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
          onClick={vaciarCarrito}
        >
          Vaciar carrito
        </button>
      </section>

      <div className="cart-layout">
        <section className="cart-items">
          {itemsCompletos.map(
            ({ comic, cantidad }) => {
              const totalItem =
                comic.precio * cantidad

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
                          eliminarItem(comic.id)
                        }
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
                            actualizarCantidad(
                              comic.id,
                              cantidad - 1,
                            )
                          }
                          disabled={
                            cantidad <= 1
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
                            actualizarCantidad(
                              comic.id,
                              cantidad + 1,
                            )
                          }
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
                  total + item.cantidad,
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