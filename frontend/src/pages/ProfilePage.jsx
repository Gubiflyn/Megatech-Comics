import { useEffect, useState } from 'react'
import {
  useIsAuthenticated,
  useMsal,
} from '@azure/msal-react'

import { loginRequest } from '../auth/msalConfig'

function decodeJwtPayload(token) {
  const payload = token.split('.')[1]

  const base64 = payload
    .replace(/-/g, '+')
    .replace(/_/g, '/')

  const padded = base64.padEnd(
    base64.length + ((4 - (base64.length % 4)) % 4),
    '=',
  )

  return JSON.parse(
    decodeURIComponent(
      atob(padded)
        .split('')
        .map(
          (char) =>
            `%${char
              .charCodeAt(0)
              .toString(16)
              .padStart(2, '0')}`,
        )
        .join(''),
    ),
  )
}

function ProfilePage() {
  const { instance, accounts } = useMsal()
  const isAuthenticated = useIsAuthenticated()

  const [claims, setClaims] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    const obtenerToken = async () => {
      if (!isAuthenticated || accounts.length === 0) {
        return
      }

      try {
        const response =
          await instance.acquireTokenSilent({
            ...loginRequest,
            account: accounts[0],
          })

        const payload =
          decodeJwtPayload(response.accessToken)

        setClaims(payload)
        setError('')
      } catch (tokenError) {
        console.error(
          'Error obteniendo Access Token:',
          tokenError,
        )

        setError(
          'No fue posible obtener el Access Token.',
        )
      }
    }

    obtenerToken()
  }, [
    accounts,
    instance,
    isAuthenticated,
  ])

  if (!isAuthenticated) {
    return (
      <main className="page-container">
        <span className="page-label">
          CUENTA
        </span>

        <h1>Mi perfil</h1>

        <p>
          Debes iniciar sesión para ver tu perfil.
        </p>
      </main>
    )
  }

  const account = accounts[0]

  return (
    <main className="page-container">
      <span className="page-label">
        CUENTA
      </span>

      <h1>Mi perfil</h1>

      <p>
        <strong>Nombre:</strong>{' '}
        {account?.name || 'Usuario'}
      </p>

      <p>
        <strong>Correo:</strong>{' '}
        {account?.username || '-'}
      </p>

      {error && (
        <p>
          {error}
        </p>
      )}

      {claims && (
        <>
          <h2>Información del Access Token</h2>

          <p>
            <strong>Audience:</strong>{' '}
            {claims.aud || '-'}
          </p>

          <p>
            <strong>Issuer:</strong>{' '}
            {claims.iss || '-'}
          </p>

          <p>
            <strong>Scope:</strong>{' '}
            {claims.scp || '-'}
          </p>

          <p>
            <strong>Roles:</strong>{' '}
            {Array.isArray(claims.roles)
              ? claims.roles.join(', ')
              : claims.roles || 'Sin roles asignados'}
          </p>
        </>
      )}
    </main>
  )
}

export default ProfilePage