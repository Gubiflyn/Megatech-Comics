import { Link } from 'react-router-dom'

import { comics } from '../data/comics'

import {
  useOrders,
} from '../context/OrderContext'

function formatearFecha(fecha) {
  return new Intl.DateTimeFormat(
    'es-CL',
    {
      dateStyle: 'medium',
      timeStyle: 'short',
    },
  ).format(new Date(fecha))
}

function obtenerComic(productoId) {
  return comics.find(
    (comic) => comic.id === productoId,
  )
}

function EstadoBadge({ estado }) {
  let clase = 'status-pending'

  if (estado === 'PAGADO') {
    clase = 'status-paid'
  }

  if (estado === 'CANCELADO') {
    clase = 'status-cancelled'
  }

  return (
    <span className={`order-status ${clase}`}>
      {estado.replaceAll('_', ' ')}
    </span>
  )
}

function OrdersPage() {
  const {
    pedidos,
    obtenerPagosPedido,
  } = useOrders()

  if (pedidos.length === 0) {
    return (
      <main className="orders-page">
        <section className="orders-header">
          <span className="page-label">
            MI CUENTA
          </span>

          <h1>Mis pedidos</h1>

          <p>
            Consulta aquí el estado de tus
            compras.
          </p>
        </section>

        <section className="orders-empty">
          <span>📦</span>

          <h2>
            Todavía no tienes pedidos
          </h2>

          <p>
            Cuando realices una compra
            aparecerá aquí.
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
    <main className="orders-page">
      <section className="orders-header">
        <div>
          <span className="page-label">
            MI CUENTA
          </span>

          <h1>Mis pedidos</h1>

          <p>
            Revisa tus compras y el estado
            de cada pago.
          </p>
        </div>

        <div className="orders-count">
          <strong>{pedidos.length}</strong>
          <span>
            {pedidos.length === 1
              ? 'pedido'
              : 'pedidos'}
          </span>
        </div>
      </section>

      <section className="orders-list">
        {pedidos.map((pedido) => {
          const pagosPedido =
            obtenerPagosPedido(pedido.id)

          const ultimoPago =
            pagosPedido[0]

          return (
            <article
              key={pedido.id}
              className="order-card"
            >
              <header className="order-card-header">
                <div>
                  <span>
                    PEDIDO #{pedido.id}
                  </span>

                  <strong>
                    {formatearFecha(
                      pedido.fechaCreacion,
                    )}
                  </strong>
                </div>

                <EstadoBadge
                  estado={pedido.estado}
                />
              </header>

              <div className="order-products">
                {pedido.items.map(
                  (item) => {
                    const comic =
                      obtenerComic(
                        item.productoId,
                      )

                    return (
                      <div
                        key={item.id}
                        className="order-product"
                      >
                        <div>
                          <strong>
                            {comic
                              ? comic.titulo
                              : `Producto #${item.productoId}`}
                          </strong>

                          <span>
                            Producto #
                            {item.productoId}
                          </span>
                        </div>

                        <span>
                          Cantidad:{' '}
                          {item.cantidad}
                        </span>
                      </div>
                    )
                  },
                )}
              </div>

              <footer className="order-card-footer">
                <div>
                  <span>
                    Usuario
                  </span>

                  <strong>
                    {pedido.usuarioId}
                  </strong>
                </div>

                {ultimoPago ? (
                  <div>
                    <span>
                      Último pago
                    </span>

                    <strong
                      className={
                        ultimoPago.estado ===
                        'APROBADO'
                          ? 'payment-approved'
                          : 'payment-rejected'
                      }
                    >
                      {ultimoPago.estado}
                    </strong>
                  </div>
                ) : (
                  <div>
                    <span>Pago</span>
                    <strong>
                      Sin procesar
                    </strong>
                  </div>
                )}

                {ultimoPago && (
                  <div>
                    <span>Método</span>

                    <strong>
                      {ultimoPago.metodoPago
                        .replaceAll(
                          '_',
                          ' ',
                        )}
                    </strong>
                  </div>
                )}
              </footer>
            </article>
          )
        })}
      </section>
    </main>
  )
}

export default OrdersPage