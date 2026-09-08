import { useMemo, useState } from 'react'
import ComicCard from '../components/ComicCard'
import { comics } from '../data/comics'

function CatalogPage() {
  const [busqueda, setBusqueda] = useState('')
  const [genero, setGenero] = useState('Todos')
  const [tipo, setTipo] = useState('Todos')

  const generos = [
    'Todos',
    ...new Set(comics.map((comic) => comic.genero)),
  ]

  const tipos = [
    'Todos',
    ...new Set(comics.map((comic) => comic.tipo)),
  ]

  const comicsFiltrados = useMemo(() => {
    return comics.filter((comic) => {
      const coincideBusqueda = comic.titulo
        .toLowerCase()
        .includes(busqueda.toLowerCase())

      const coincideGenero =
        genero === 'Todos' || comic.genero === genero

      const coincideTipo =
        tipo === 'Todos' || comic.tipo === tipo

      return coincideBusqueda && coincideGenero && coincideTipo
    })
  }, [busqueda, genero, tipo])

  const limpiarFiltros = () => {
    setBusqueda('')
    setGenero('Todos')
    setTipo('Todos')
  }

  return (
    <main className="catalog-page">
      <section className="catalog-header">
        <div>
          <span className="page-label">TIENDA</span>

          <h1>Explora nuestros cómics</h1>

          <p>
            Descubre nuevas historias, héroes, universos y colecciones
            disponibles en Megatech Comics.
          </p>
        </div>

        <div className="catalog-counter">
          <strong>{comicsFiltrados.length}</strong>
          <span>
            {comicsFiltrados.length === 1
              ? 'resultado'
              : 'resultados'}
          </span>
        </div>
      </section>

      <section className="catalog-toolbar">
        <div className="search-box">
          <span>⌕</span>

          <input
            type="text"
            placeholder="Buscar cómic por nombre..."
            value={busqueda}
            onChange={(event) => setBusqueda(event.target.value)}
          />
        </div>

        <select
          value={genero}
          onChange={(event) => setGenero(event.target.value)}
        >
          {generos.map((item) => (
            <option key={item} value={item}>
              {item === 'Todos' ? 'Todos los géneros' : item}
            </option>
          ))}
        </select>

        <select
          value={tipo}
          onChange={(event) => setTipo(event.target.value)}
        >
          {tipos.map((item) => (
            <option key={item} value={item}>
              {item === 'Todos' ? 'Todos los tipos' : item}
            </option>
          ))}
        </select>
      </section>

      {comicsFiltrados.length > 0 ? (
        <section className="comic-grid">
          {comicsFiltrados.map((comic) => (
            <ComicCard
              key={comic.id}
              comic={comic}
            />
          ))}
        </section>
      ) : (
        <section className="catalog-empty">
          <span>📚</span>

          <h2>No encontramos cómics</h2>

          <p>
            Prueba utilizando otra búsqueda o cambiando los filtros.
          </p>

          <button onClick={limpiarFiltros}>
            Limpiar filtros
          </button>
        </section>
      )}
    </main>
  )
}

export default CatalogPage