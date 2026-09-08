import { Link } from 'react-router-dom'

function HomePage() {
  return (
    <main>
      <section className="hero-section">
        <div className="hero-content">
          <span className="hero-badge">NUEVAS HISTORIAS CADA SEMANA</span>

          <h1>
            DESCUBRE TU
            <span> PRÓXIMA AVENTURA</span>
          </h1>

          <p>
            Explora cómics, descubre nuevos mundos y encuentra tus historias
            favoritas en Megatech Comics.
          </p>

          <div className="hero-actions">
            <Link to="/catalogo" className="primary-button">
              Explorar catálogo
            </Link>

            <a href="#destacados" className="secondary-button">
              Ver destacados
            </a>
          </div>
        </div>

        <div className="hero-card">
          <div className="comic-decoration">
            <span>POW!</span>
          </div>

          <h2>MEGATECH</h2>
          <p>COMICS</p>
        </div>
      </section>

      <section className="features">
        <article>
          <div className="feature-icon">📚</div>
          <h3>Gran catálogo</h3>
          <p>Encuentra cómics de diferentes editoriales y géneros.</p>
        </article>

        <article>
          <div className="feature-icon">⚡</div>
          <h3>Compra sencilla</h3>
          <p>Agrega tus cómics favoritos al carrito de forma rápida.</p>
        </article>

        <article>
          <div className="feature-icon">🔒</div>
          <h3>Compra segura</h3>
          <p>Accede mediante autenticación segura con Microsoft.</p>
        </article>
      </section>

      <section id="destacados" className="featured-section">
        <div className="section-heading">
          <div>
            <span>COLECCIÓN</span>
            <h2>Cómics destacados</h2>
          </div>

          <Link to="/catalogo">Ver catálogo →</Link>
        </div>

        <div className="empty-featured">
          <span>📖</span>
          <h3>Próximamente aparecerán nuestros cómics aquí</h3>
          <p>
            Después conectaremos esta sección directamente con
            catálogo-service.
          </p>
        </div>
      </section>
    </main>
  )
}

export default HomePage