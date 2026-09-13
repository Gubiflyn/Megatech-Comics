import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { useClienteAuth } from '../context/ClienteAuthContext'

function ClienteAuthPage() {
  const navigate = useNavigate()
  const { login, registrar, cargando, error } =
    useClienteAuth()

  const [modo, setModo] = useState('login')

  const [nombreCompleto, setNombreCompleto] =
    useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const alternarModo = () => {
    setModo(
      modo === 'login' ? 'registro' : 'login',
    )
  }

  const manejarSubmit = async (event) => {
    event.preventDefault()

    try {
      if (modo === 'registro') {
        await registrar({
          email,
          password,
          nombreCompleto,
        })
      } else {
        await login({ email, password })
      }

      navigate('/perfil-cliente')
    } catch {
      // El error ya queda expuesto vía el contexto.
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-card">
        <span className="page-label">
          CUENTA
        </span>

        <h1>
          {modo === 'login'
            ? 'Iniciar sesión'
            : 'Crear cuenta'}
        </h1>

        <form onSubmit={manejarSubmit}>
          {modo === 'registro' && (
            <div className="form-field">
              <label htmlFor="nombreCompleto">
                Nombre completo
              </label>

              <input
                id="nombreCompleto"
                type="text"
                value={nombreCompleto}
                onChange={(event) =>
                  setNombreCompleto(
                    event.target.value,
                  )
                }
                className={
                  error ? 'has-error' : ''
                }
                required
              />
            </div>
          )}

          <div className="form-field">
            <label htmlFor="email">
              Email
            </label>

            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) =>
                setEmail(event.target.value)
              }
              className={
                error ? 'has-error' : ''
              }
              required
            />
          </div>

          <div className="form-field">
            <label htmlFor="password">
              Contraseña
            </label>

            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) =>
                setPassword(event.target.value)
              }
              className={
                error ? 'has-error' : ''
              }
              required
            />

            {modo === 'registro' && (
              <span className="field-hint">
                Mínimo 8 caracteres
              </span>
            )}
          </div>

          {error && (
            <p className="field-error-message">
              {error}
            </p>
          )}

          <button
            type="submit"
            className="primary-button auth-submit"
            disabled={cargando}
          >
            {cargando
              ? 'Procesando...'
              : modo === 'login'
                ? 'Iniciar sesión'
                : 'Crear cuenta'}
          </button>
        </form>

        <button
          type="button"
          className="link-button"
          onClick={alternarModo}
        >
          {modo === 'login'
            ? '¿No tienes cuenta? Regístrate'
            : '¿Ya tienes cuenta? Inicia sesión'}
        </button>
      </div>
    </main>
  )
}

export default ClienteAuthPage
