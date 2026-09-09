import { NavLink } from 'react-router-dom'
import {
  useIsAuthenticated,
  useMsal,
} from '@azure/msal-react'

import { useCart } from '../../context/CartContext'
import { loginRequest } from '../../auth/msalConfig'

function Navbar() {
  const { cantidadTotal } = useCart()
  const { instance, accounts } = useMsal()
  const isAuthenticated = useIsAuthenticated()

  const iniciarSesion = async () => {
    try {
      await instance.loginRedirect(loginRequest)
    } catch (error) {
      console.error('Error al iniciar sesión:', error)
    }
  }

  const cerrarSesion = async () => {
    try {
      await instance.logoutRedirect({
        postLogoutRedirectUri: 'http://localhost:5173',
      })
    } catch (error) {
      console.error('Error al cerrar sesión:', error)
    }
  }

  const account = accounts[0]

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

          {isAuthenticated ? (
            <>
              <NavLink
                to="/perfil"
                className="login-button"
              >
                {account?.name || 'Mi perfil'}
              </NavLink>

              <button
                type="button"
                className="login-button"
                onClick={cerrarSesion}
              >
                Cerrar sesión
              </button>
            </>
          ) : (
            <button
              type="button"
              className="login-button"
              onClick={iniciarSesion}
            >
              Iniciar sesión
            </button>
          )}
        </div>
      </div>
    </header>
  )
}

export default Navbar
