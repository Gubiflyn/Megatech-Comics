import {
  useEffect,
  useMemo,
  useState,
} from 'react'

import { Link } from 'react-router-dom'

import {
  useOrders,
} from '../context/OrderContext'

const CATALOGO_API =
  'https://os3wsgjxhh.execute-api.us-east-1.amazonaws.com/api/catalogo'

function formatearFecha(fecha) {
  if (!fecha) {
    return 'Fecha no disponible'
  }

  return new Intl.DateTimeFormat(
    'es-CL',
    {
      dateStyle: 'medium',
      timeStyle: 'short',
    },
  ).format(new Date(fecha))
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
    <span
      className={`order-status ${clase}`}
    >
      {(estado || 'PENDIENTE').replaceAll(
        '_',
        ' ',
      )}
    </span>
  )
}

function OrdersPage() {
  const {
    pedidos,
    cargando: cargandoPedidos,
    error: errorPedidos,
    obtenerPagosPedido,
  } = useOrders()

  const [comics, setComics] =
    useState([])

  const [
    cargandoCatalogo,
    setCargandoCatalogo,
  ] = useState(true)

  const [
    errorCatalogo,
    setErrorCatalogo,
  ] = useState('')

  useEffect(() => {
    const cargarCatalogo = async () => {
      try {
        setCargandoCatalogo(true)
        setErrorCatalogo('')

        const response =
          await fetch(CATALOGO_API)

        if (!response.ok) {
          throw new Error(
            `Error consultando catálogo: ${response.status}`,
          )
        }

        const data =
          await response.json()

        setComics(
          Array.isArray(data)
            ? data
            : [],
        )
      } catch (err) {
        console.error(
          'Error cargando catálogo en Mis pedidos:',
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

  const comicsPorId = useMemo(() => {
    const mapa = new Map()

    comics.forEach((comic) => {
      mapa.set(
        Number(comic.id),
        comic,
      )
    })

    return mapa
  }, [comics])

  if (
    cargandoPedidos ||
    cargandoCatalogo
  ) {
    return (
      <main className="orders-page">
        <section className="orders-header">
          <span className="page-label">
            MI CUENTA
          </span>

          <h1>Mis pedidos</h1>

          <p>
            Cargando tus compras...
          </p>
        </section>
      </main>
    )
  }

  if (errorPedidos) {
    return (
      <main className="orders-page">
        <section className="orders-header">
          <span className="page-label">
            ERROR
          </span>

          <h1>
            No pudimos cargar tus pedidos
          </h1>

          <p>{errorPedidos}</p>
        </section>
      </main>
    )
  }

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
          <strong>
            {pedidos.length}
          </strong>

          <span>
            {pedidos.length === 1
              ? 'pedido'
              : 'pedidos'}
          </span>
        </div>
      </section>

      {errorCatalogo && (
        <p>
          {errorCatalogo}
        </p>
      )}

      <section className="orders-list">
        {pedidos.map((pedido) => {
          const pagosPedido =
            obtenerPagosPedido(
              pedido.id,
            )

          const ultimoPago =
            pagosPedido[0]

          const itemsPedido =
            Array.isArray(pedido.items)
              ? pedido.items
              : []

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
                {itemsPedido.map(
                  (item) => {
                    const comic =
                      comicsPorId.get(
                        Number(
                          item.productoId,
                        ),
                      )

                    return (
                      <div
                        key={
                          item.id ??
                          `${pedido.id}-${item.productoId}`
                        }
                        className="order-product"
                      >
                        <div>
                          <strong>
                            {comic
                              ? comic.titulo
                              : `Producto #${item.productoId}`}
                          </strong>

                          <span>
                            {comic
                              ? `${comic.edicion || 'Sin edición'}${
                                  comic.tomo
                                    ? ` · Tomo ${comic.tomo}`
                                    : ''
                                }`
                              : `Producto #${item.productoId}`}
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
                    <span>
                      Método
                    </span>

                    <strong>
                      {ultimoPago.metodoPago
                        ? ultimoPago.metodoPago.replaceAll(
                            '_',
                            ' ',
                          )
                        : 'Sin especificar'}
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