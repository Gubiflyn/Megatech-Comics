import { Link } from 'react-router-dom'

function ComicCard({ comic }) {
  const precioFormateado = new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
  }).format(comic.precio)

  return (
    <article className="comic-card">
      <Link to={`/catalogo/${comic.id}`} className="comic-cover">
        <div className="comic-cover-pattern" />

        <span className="comic-type">{comic.tipo}</span>

        <div className="comic-cover-content">
          <span>MEGATECH</span>

          <h3>{comic.titulo}</h3>

          <p>
            {comic.edicion}
            {comic.tomo && ` · Tomo ${comic.tomo}`}
          </p>
        </div>
      </Link>

      <div className="comic-info">
        <div className="comic-meta">
          <span>{comic.genero}</span>

          {comic.edicion && <span>{comic.edicion}</span>}
        </div>

        <Link to={`/catalogo/${comic.id}`}>
          <h2>{comic.titulo}</h2>
        </Link>

        <p className="comic-description">
          {comic.descripcion}
        </p>

        <div className="comic-card-footer">
          <strong>{precioFormateado}</strong>

          <Link
            to={`/catalogo/${comic.id}`}
            className="comic-detail-button"
          >
            Ver detalle
          </Link>
        </div>
      </div>
    </article>
  )
}

export default ComicCard