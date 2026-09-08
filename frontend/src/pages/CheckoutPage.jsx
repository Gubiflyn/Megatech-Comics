import { useMemo, useState } from 'react'
import {
  Link,
  useNavigate,
} from 'react-router-dom'

import { comics } from '../data/comics'

import {
  useCart,
} from '../context/CartContext'

import {
  useOrders,
} from '../context/OrderContext'

const USUARIO_TEMPORAL = 'usuario-demo'

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
    vaciarCarrito,
  } = useCart()

  const {
    crearPedido,
    procesarPago,
  } = useOrders()

  const [metodoPago, setMetodoPago] =
    useState('WEBPAY_SIMULADO')

  const [resultadoSimulado, setResultadoSimulado] =
    useState('APROBADO')

  const [procesando, setProcesando] =
    useState(false)

  const [error, setError] =
    useState('')

  const itemsCompletos = useMemo(() => {
    return items
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
  }, [items])

  const total = itemsCompletos.reduce(
    (acumulado, item) =>
      acumulado +
      item.comic.precio * item.cantidad,
    0,
  )

  const confirmarCompra = () => {
    if (itemsCompletos.length === 0) {
      return
    }

    try {
      setProcesando(true)
      setError('')

      /*
       * En la integración real:
       *
       * POST /api/pedidos/{usuarioId}
       *
       * El backend obtiene el carrito,
       * valida inventario y crea el pedido.
       */

      const pedido = crearPedido(
        USUARIO_TEMPORAL,
        items,
      )

      /*
       * El pedidos-service real vacía
       * carrito-service después de crear
       * correctamente el pedido.
       */

      vaciarCarrito()

      /*
       * Luego:
       *
       * POST /api/pagos/pedido/{pedidoId}
       *
       * {
       *   metodoPago,
       *   aprobarPago
       * }
       */

      procesarPago(
        pedido.id,
        metodoPago,
        resultadoSimulado === 'APROBADO',
      )

      navigate(
        `/pedidos?creado=${pedido.id}`,
      )
    } catch (err) {
      setError(
        err.message ||
          'No fue posible completar la compra.',
      )
    } finally {
      setProcesando(false)
    }
  }

  if (itemsCompletos.length === 0) {
    return (
      <main className="checkout-page">
        <section className="checkout-empty">
          <span>🛒</span>

          <h1>No hay productos para comprar</h1>

          <p>
            Agrega algún cómic al carrito antes de
            continuar.
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
          Revisa tu pedido y selecciona un
          método de pago.
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
                ({ comic, cantidad }) => (
                  <article
                    key={comic.id}
                    className="checkout-product"
                  >
                    <div>
                      <strong>
                        {comic.titulo}
                      </strong>

                      <span>
                        {comic.edicion}
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
                          comic.precio *
                            cantidad,
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
                <h2>Método de pago</h2>

                <p>
                  Selecciona cómo deseas pagar
                  tu pedido.
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
                  <strong>Tarjeta</strong>
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
                  pagos-service permite simular
                  un pago aprobado o rechazado.
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
                (cantidad, item) =>
                  cantidad + item.cantidad,
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