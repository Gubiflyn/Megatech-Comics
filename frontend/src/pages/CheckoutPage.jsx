import {
  useEffect,
  useMemo,
  useState,
} from 'react'

import {
  Link,
  useNavigate,
} from 'react-router-dom'

import {
  useCart,
} from '../context/CartContext'

import {
  useOrders,
} from '../context/OrderContext'

const CATALOGO_API =
  'https://os3wsgjxhh.execute-api.us-east-1.amazonaws.com/api/catalogo'

function formatearPrecio(precio) {
  return new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
  }).format(precio)
}

function CheckoutPage() {
  const navigate = useNavigate()

  const {
    items,
    cargando: cargandoCarrito,
    error: errorCarrito,
    cargarCarrito,
  } = useCart()

  const {
    crearPedido,
    procesarPago,
  } = useOrders()

  const [comics, setComics] = useState([])
  const [cargandoCatalogo, setCargandoCatalogo] =
    useState(true)
  const [errorCatalogo, setErrorCatalogo] =
    useState('')

  const [metodoPago, setMetodoPago] =
    useState('WEBPAY_SIMULADO')

  const [
    resultadoSimulado,
    setResultadoSimulado,
  ] = useState('APROBADO')

  const [procesando, setProcesando] =
    useState(false)

  const [error, setError] =
    useState('')

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
          'Error cargando catálogo en checkout:',
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
        const comic =
          comics.find(
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

  const total = useMemo(() => {
    return itemsCompletos.reduce(
      (acumulado, item) =>
        acumulado +
        Number(item.comic.precio) *
          Number(item.cantidad),
      0,
    )
  }, [itemsCompletos])

  const confirmarCompra = async () => {
    if (
      itemsCompletos.length === 0 ||
      procesando
    ) {
      return
    }

    try {
      setProcesando(true)
      setError('')

      /*
       * 1. El BFF obtiene el usuario
       * desde el oid del Access Token.
       *
       * 2. pedidos-service obtiene
       * el carrito, valida stock,
       * descuenta inventario,
       * crea el pedido y vacía
       * el carrito en el backend.
       */
      const pedido =
        await crearPedido()

      if (!pedido?.id) {
        throw new Error(
          'El servidor no devolvió un pedido válido.',
        )
      }

      /*
       * 3. Procesamos el pago real
       * mediante pagos-service.
       */
      await procesarPago(
        pedido.id,
        metodoPago,
        resultadoSimulado ===
          'APROBADO',
      )

      /*
       * 4. Sincronizamos el contexto
       * local porque pedidos-service
       * ya vació el carrito real.
       */
      await cargarCarrito()

      /*
       * 5. Mostramos el pedido
       * recién creado.
       */
      navigate(
        `/pedidos?creado=${pedido.id}`,
      )
    } catch (err) {
      console.error(
        'Error completando compra:',
        err,
      )

      setError(
        err.message ||
          'No fue posible completar la compra.',
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
      <main className="checkout-page">
        <section className="checkout-empty">
          <span>⌛</span>

          <h1>Cargando checkout</h1>

          <p>
            Estamos obteniendo la información
            de tu carrito.
          </p>
        </section>
      </main>
    )
  }

  if (
    errorCarrito ||
    errorCatalogo
  ) {
    return (
      <main className="checkout-page">
        <section className="checkout-empty">
          <span>⚠️</span>

          <h1>
            No pudimos cargar el checkout
          </h1>

          <p>
            {errorCarrito ||
              errorCatalogo}
          </p>

          <Link
            to="/carrito"
            className="primary-button"
          >
            Volver al carrito
          </Link>
        </section>
      </main>
    )
  }

  if (itemsCompletos.length === 0) {
    return (
      <main className="checkout-page">
        <section className="checkout-empty">
          <span>🛒</span>

          <h1>
            No hay productos para comprar
          </h1>

          <p>
            Agrega algún cómic al carrito
            antes de continuar.
          </p>

          <Link
            to="/catalogo"
            className="primary-button"
          >
            Ir al catálogo
          </Link>
        </section>
      </main>
    )
  }

  return (
    <main className="checkout-page">
      <div className="checkout-title">
        <Link
          to="/carrito"
          className="back-link"
        >
          ← Volver al carrito
        </Link>

        <span className="page-label">
          CHECKOUT
        </span>

        <h1>Finalizar compra</h1>

        <p>
          Revisa tu pedido y selecciona
          un método de pago.
        </p>
      </div>

      <div className="checkout-layout">
        <section className="checkout-main">

          <div className="checkout-section">
            <div className="checkout-section-title">
              <span>01</span>

              <div>
                <h2>Productos</h2>

                <p>
                  Resumen de los cómics que
                  comprarás.
                </p>
              </div>
            </div>

            <div className="checkout-products">
              {itemsCompletos.map(
                ({
                  comic,
                  cantidad,
                }) => (
                  <article
                    key={comic.id}
                    className="checkout-product"
                  >
                    <div>
                      <strong>
                        {comic.titulo}
                      </strong>

                      <span>
                        {comic.edicion ||
                          'Sin especificar'}
                      </span>
                    </div>

                    <div>
                      <span>
                        {cantidad} ×{' '}
                        {formatearPrecio(
                          comic.precio,
                        )}
                      </span>

                      <strong>
                        {formatearPrecio(
                          Number(
                            comic.precio,
                          ) *
                            Number(
                              cantidad,
                            ),
                        )}
                      </strong>
                    </div>
                  </article>
                ),
              )}
            </div>
          </div>

          <div className="checkout-section">
            <div className="checkout-section-title">
              <span>02</span>

              <div>
                <h2>
                  Método de pago
                </h2>

                <p>
                  Selecciona cómo deseas
                  pagar tu pedido.
                </p>
              </div>
            </div>

            <div className="payment-methods">
              <label
                className={
                  metodoPago === 'TARJETA'
                    ? 'payment-option selected'
                    : 'payment-option'
                }
              >
                <input
                  type="radio"
                  name="metodoPago"
                  value="TARJETA"
                  checked={
                    metodoPago === 'TARJETA'
                  }
                  onChange={(event) =>
                    setMetodoPago(
                      event.target.value,
                    )
                  }
                />

                <span className="payment-icon">
                  💳
                </span>

                <div>
                  <strong>
                    Tarjeta
                  </strong>

                  <span>
                    Pago mediante tarjeta
                  </span>
                </div>
              </label>

              <label
                className={
                  metodoPago ===
                  'TRANSFERENCIA'
                    ? 'payment-option selected'
                    : 'payment-option'
                }
              >
                <input
                  type="radio"
                  name="metodoPago"
                  value="TRANSFERENCIA"
                  checked={
                    metodoPago ===
                    'TRANSFERENCIA'
                  }
                  onChange={(event) =>
                    setMetodoPago(
                      event.target.value,
                    )
                  }
                />

                <span className="payment-icon">
                  🏦
                </span>

                <div>
                  <strong>
                    Transferencia
                  </strong>

                  <span>
                    Transferencia bancaria
                  </span>
                </div>
              </label>

              <label
                className={
                  metodoPago ===
                  'WEBPAY_SIMULADO'
                    ? 'payment-option selected'
                    : 'payment-option'
                }
              >
                <input
                  type="radio"
                  name="metodoPago"
                  value="WEBPAY_SIMULADO"
                  checked={
                    metodoPago ===
                    'WEBPAY_SIMULADO'
                  }
                  onChange={(event) =>
                    setMetodoPago(
                      event.target.value,
                    )
                  }
                />

                <span className="payment-icon">
                  🌐
                </span>

                <div>
                  <strong>
                    Webpay simulado
                  </strong>

                  <span>
                    Simulación para la
                    evaluación
                  </span>
                </div>
              </label>
            </div>
          </div>

          <div className="checkout-section">
            <div className="checkout-section-title">
              <span>03</span>

              <div>
                <h2>
                  Resultado de prueba
                </h2>

                <p>
                  pagos-service permite
                  simular un pago aprobado
                  o rechazado.
                </p>
              </div>
            </div>

            <div className="simulation-options">
              <button
                type="button"
                className={
                  resultadoSimulado ===
                  'APROBADO'
                    ? 'simulation-button success active'
                    : 'simulation-button success'
                }
                onClick={() =>
                  setResultadoSimulado(
                    'APROBADO',
                  )
                }
                disabled={procesando}
              >
                ✓ Simular aprobado
              </button>

              <button
                type="button"
                className={
                  resultadoSimulado ===
                  'RECHAZADO'
                    ? 'simulation-button rejected active'
                    : 'simulation-button rejected'
                }
                onClick={() =>
                  setResultadoSimulado(
                    'RECHAZADO',
                  )
                }
                disabled={procesando}
              >
                ✕ Simular rechazado
              </button>
            </div>
          </div>

          {error && (
            <div className="checkout-error">
              {error}
            </div>
          )}
        </section>

        <aside className="checkout-summary">
          <span className="summary-label">
            TU PEDIDO
          </span>

          <h2>Resumen</h2>

          <div className="summary-row">
            <span>Productos</span>

            <strong>
              {itemsCompletos.reduce(
                (
                  cantidad,
                  item,
                ) =>
                  cantidad +
                  Number(
                    item.cantidad,
                  ),
                0,
              )}
            </strong>
          </div>

          <div className="summary-row">
            <span>Método</span>

            <strong>
              {metodoPago.replaceAll(
                '_',
                ' ',
              )}
            </strong>
          </div>

          <div className="summary-total">
            <span>Total</span>

            <strong>
              {formatearPrecio(total)}
            </strong>
          </div>

          <button
            type="button"
            className="confirm-order-button"
            onClick={confirmarCompra}
            disabled={procesando}
          >
            {procesando
              ? 'Procesando...'
              : 'Confirmar y pagar'}
          </button>

          <div className="checkout-security">
            🔒 El pago se encuentra simulado
            para fines académicos.
          </div>
        </aside>
      </div>
    </main>
  )
}

export default CheckoutPage