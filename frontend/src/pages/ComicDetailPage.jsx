import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'

import { comics } from '../data/comics'
import { useCart } from '../context/CartContext'

function ComicDetailPage() {
  const { id } = useParams()

  const { agregarItem } = useCart()

  const [agregado, setAgregado] = useState(false)

  const comic = comics.find(
    (item) => item.id === Number(id),
  )

  if (!comic) {
    return (
      <main className="page-container comic-not-found">
        <span className="page-label">
          ERROR
        </span>

        <h1>Cómic no encontrado</h1>

        <p>
          El cómic que intentas consultar no existe.
        </p>

        <Link
          to="/catalogo"
          className="primary-button"
        >
          Volver al catálogo
        </Link>
      </main>
    )
  }

  const precioFormateado =
    new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP',
    }).format(comic.precio)

  const manejarAgregar = () => {
    agregarItem(comic.id, 1)

    setAgregado(true)

    setTimeout(() => {
      setAgregado(false)
    }, 2000)
  }

  return (
    <main className="comic-detail-page">
      <Link
        to="/catalogo"
        className="back-link"
      >
        ← Volver al catálogo
      </Link>

      <section className="comic-detail">
        <div className="detail-cover">
          <div className="comic-cover-pattern" />

          <span className="comic-type">
            {comic.tipo}
          </span>

          <div className="detail-cover-content">
            <span>MEGATECH COMICS</span>

            <h1>{comic.titulo}</h1>

            <p>{comic.edicion}</p>
          </div>
        </div>

        <div className="detail-info">
          <span className="page-label">
            {comic.genero}
          </span>

          <h1>{comic.titulo}</h1>

          <p className="detail-description">
            {comic.descripcion}
          </p>

          <div className="detail-properties">
            <div>
              <span>Tipo</span>
              <strong>{comic.tipo}</strong>
            </div>

            <div>
              <span>Edición</span>
              <strong>
                {comic.edicion || 'Sin especificar'}
              </strong>
            </div>

            <div>
              <span>Tomo</span>
              <strong>{comic.tomo || '-'}</strong>
            </div>

            <div>
              <span>Género</span>
              <strong>
                {comic.genero || 'Sin especificar'}
              </strong>
            </div>
          </div>

          <div className="detail-purchase">
            <div>
              <span>Precio</span>

              <strong>
                {precioFormateado}
              </strong>
            </div>

            <button
              type="button"
              className="add-cart-button"
              onClick={manejarAgregar}
            >
              🛒 Agregar al carrito
            </button>
          </div>

          {agregado && (
            <div className="cart-success">
              ✓ Cómic agregado correctamente al carrito.
            </div>
          )}

          <p className="integration-note">
            Posteriormente el stock será validado
            mediante inventario-service.
          </p>
        </div>
      </section>
    </main>
  )
}

export default ComicDetailPage