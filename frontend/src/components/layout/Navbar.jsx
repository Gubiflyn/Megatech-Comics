import { NavLink } from 'react-router-dom'
import {
  useIsAuthenticated,
  useMsal,
} from '@azure/msal-react'

import { useCart } from '../../context/CartContext'
import { useClienteAuth } from '../../context/ClienteAuthContext'

function Navbar() {
  const { cantidadTotal } = useCart()
  const { instance, accounts } = useMsal()
  const isAuthenticated = useIsAuthenticated()
  const { cliente, logout: logoutCliente } =
    useClienteAuth()

  const cerrarSesionStaff = async () => {
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
                onClick={cerrarSesionStaff}
              >
                Cerrar sesión
              </button>
            </>
          ) : cliente ? (
            <>
              <NavLink
                to="/perfil-cliente"
                className="login-button"
              >
                {cliente.nombreCompleto || 'Mi cuenta'}
              </NavLink>

              <button
                type="button"
                className="login-button"
                onClick={logoutCliente}
              >
                Cerrar sesión
              </button>
            </>
          ) : (
            <NavLink
              to="/acceso"
              className="login-button"
            >
              Iniciar sesión
            </NavLink>
          )}
        </div>
      </div>
    </header>
  )
}

export default Navbar
