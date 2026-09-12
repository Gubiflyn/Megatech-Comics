import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

import { useCart } from '../context/CartContext'

const API_URL =
  'https://os3wsgjxhh.execute-api.us-east-1.amazonaws.com/api/catalogo'

function ComicDetailPage() {
  const { id } = useParams()
  const { agregarItem } = useCart()

  const [comic, setComic] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [agregado, setAgregado] = useState(false)

  const [agregando, setAgregando] = useState(false)
  const [errorCarrito, setErrorCarrito] = useState('')

  useEffect(() => {
    const cargarComic = async () => {
      try {
        setCargando(true)
        setError('')

        const response = await fetch(`${API_URL}/${id}`)

        if (!response.ok) {
          if (response.status === 404) {
            throw new Error('El cómic solicitado no existe.')
          }

          throw new Error(
            'No fue posible obtener el cómic desde el servidor.',
          )
        }

        const data = await response.json()

        setComic(data)
      } catch (err) {
        setError(
          err.message ||
            'Ocurrió un error al cargar el cómic.',
        )
      } finally {
        setCargando(false)
      }
    }

    cargarComic()
  }, [id])

  if (cargando) {
    return (
      <main className="page-container comic-not-found">
        <span className="page-label">
          CARGANDO
        </span>

        <h1>Cargando cómic...</h1>

        <p>
          Estamos consultando la información del catálogo.
        </p>
      </main>
    )
  }

  if (error || !comic) {
    return (
      <main className="page-container comic-not-found">
        <span className="page-label">
          ERROR
        </span>

        <h1>Cómic no encontrado</h1>

        <p>
          {error ||
            'El cómic que intentas consultar no existe.'}
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

  const manejarAgregar = async () => {
    try {
      setAgregando(true)
      setAgregado(false)
      setErrorCarrito('')

      await agregarItem(comic.id, 1)

      setAgregado(true)

      setTimeout(() => {
        setAgregado(false)
      }, 2000)
    } catch (err) {
      console.error(
        'Error al agregar el cómic al carrito:',
        err,
      )

      setErrorCarrito(
        err.message ||
          'No fue posible agregar el cómic al carrito.',
      )
    } finally {
      setAgregando(false)
    }
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

            <p>
              {comic.edicion || 'Sin especificar'}
            </p>
          </div>
        </div>

        <div className="detail-info">
          <span className="page-label">
            {comic.genero || 'Sin género'}
          </span>

          <h1>{comic.titulo}</h1>

          <p className="detail-description">
            {comic.descripcion}
          </p>

          <div className="detail-properties">
            <div>
              <span>Tipo</span>

              <strong>
                {comic.tipo || 'Sin especificar'}
              </strong>
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
              disabled={agregando}
            >
              {agregando
                ? 'Agregando...'
                : '🛒 Agregar al carrito'}
            </button>
          </div>

          {agregado && (
            <div className="cart-success">
              ✓ Cómic agregado correctamente al carrito.
            </div>
          )}

          {errorCarrito && (
            <p>
              {errorCarrito}
            </p>
          )}

          <p className="integration-note">
            Información obtenida desde el catálogo
            conectado a AWS RDS.
          </p>
        </div>
      </section>
    </main>
  )
}

export default ComicDetailPage