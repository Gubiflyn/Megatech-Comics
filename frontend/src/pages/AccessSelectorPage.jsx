import { useNavigate } from 'react-router-dom'
import { useMsal } from '@azure/msal-react'

import { loginRequest } from '../auth/msalConfig'

function AccessSelectorPage() {
  const navigate = useNavigate()
  const { instance } = useMsal()

  const accederComoStaff = async () => {
    try {
      await instance.loginRedirect(loginRequest)
    } catch (error) {
      console.error('Error al iniciar sesión:', error)
    }
  }

  const accederComoCliente = () => {
    navigate('/cuenta-cliente')
  }

  return (
    <main className="access-page">
      <span className="page-label">
        ACCESO
      </span>

      <h1>¿Cómo quieres ingresar?</h1>

      <p>
        Elige el tipo de cuenta con la que quieres
        acceder a Megatech Comics.
      </p>

      <div className="access-options">
        <button
          type="button"
          className="access-option"
          onClick={accederComoStaff}
        >
          <span className="access-icon">
            🏢
          </span>

          <div>
            <strong>
              Soy administrador / staff
            </strong>

            <span>
              Accede con tu cuenta institucional
              (Microsoft).
            </span>
          </div>
        </button>

        <button
          type="button"
          className="access-option"
          onClick={accederComoCliente}
        >
          <span className="access-icon">
            🙋
          </span>

          <div>
            <strong>
              Soy cliente
            </strong>

            <span>
              Regístrate o inicia sesión con
              tu email.
            </span>
          </div>
        </button>
      </div>
    </main>
  )
}

export default AccessSelectorPage
