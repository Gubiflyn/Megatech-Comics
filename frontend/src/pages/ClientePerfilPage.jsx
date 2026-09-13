import { Navigate } from 'react-router-dom'

import { useClienteAuth } from '../context/ClienteAuthContext'

function ClientePerfilPage() {
  const { cliente, logout } = useClienteAuth()

  if (!cliente) {
    return <Navigate to="/cuenta-cliente" replace />
  }

  return (
    <main className="page-container">
      <span className="page-label">
        CUENTA
      </span>

      <h1>Mi perfil</h1>

      <p>
        <strong>Nombre:</strong>{' '}
        {cliente.nombreCompleto}
      </p>

      <p>
        <strong>Correo:</strong>{' '}
        {cliente.email}
      </p>

      <p>
        <strong>ID de cliente:</strong>{' '}
        {cliente.clienteUuid}
      </p>

      <button
        type="button"
        onClick={logout}
      >
        Cerrar sesión
      </button>
    </main>
  )
}

export default ClientePerfilPage
