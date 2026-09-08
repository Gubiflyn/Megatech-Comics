import { NavLink } from 'react-router-dom'

import { useCart } from '../../context/CartContext'

function Navbar() {
  const { cantidadTotal } = useCart()

  return (
    <header className="navbar">
      <div className="navbar-container">
        <NavLink
          to="/"
          className="brand"
        >
          <span className="brand-logo">
            M
          </span>

          <div>
            <strong>MEGATECH</strong>
            <span>COMICS</span>
          </div>
        </NavLink>

        <nav className="nav-links">
          <NavLink to="/">
            Inicio
          </NavLink>

          <NavLink to="/catalogo">
            Catálogo
          </NavLink>

          <NavLink to="/pedidos">
            Mis pedidos
          </NavLink>
        </nav>

        <div className="nav-actions">
          <NavLink
            to="/carrito"
            className="cart-button"
          >
            <span>🛒</span>

            Carrito

            {cantidadTotal > 0 && (
              <span className="cart-count">
                {cantidadTotal > 99
                  ? '99+'
                  : cantidadTotal}
              </span>
            )}
          </NavLink>

          <NavLink
            to="/perfil"
            className="login-button"
          >
            Iniciar sesión
          </NavLink>
        </div>
      </div>
    </header>
  )
}

export default Navbar