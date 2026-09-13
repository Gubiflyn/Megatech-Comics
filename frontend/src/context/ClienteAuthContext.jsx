import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from 'react'

import {
  loginCliente,
  registrarCliente,
} from '../api/clientesAuthApi'

const ClienteAuthContext = createContext(null)

const STORAGE_KEY = 'clienteAuth'

export function ClienteAuthProvider({ children }) {
  const [cliente, setCliente] = useState(null)
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    const guardado =
      localStorage.getItem(STORAGE_KEY)

    if (guardado) {
      setCliente(JSON.parse(guardado))
    }
  }, [])

  const guardarSesion = useCallback((datos) => {
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify(datos),
    )

    setCliente(datos)
  }, [])

  const login = useCallback(async (credenciales) => {
    try {
      setCargando(true)
      setError('')

      const datos =
        await loginCliente(credenciales)

      guardarSesion(datos)

      return datos
    } catch (err) {
      const mensaje =
        err.response?.status === 401
          ? 'Email o contraseña incorrectos'
          : 'No fue posible iniciar sesión.'

      setError(mensaje)

      throw err
    } finally {
      setCargando(false)
    }
  }, [guardarSesion])

  const registrar = useCallback(async (datos) => {
    try {
      setCargando(true)
      setError('')

      await registrarCliente(datos)

      const sesion = await loginCliente({
        email: datos.email,
        password: datos.password,
      })

      guardarSesion(sesion)

      return sesion
    } catch (err) {
      const mensaje =
        err.response?.status === 409
          ? 'Ya existe una cuenta con ese email.'
          : 'No fue posible completar el registro.'

      setError(mensaje)

      throw err
    } finally {
      setCargando(false)
    }
  }, [guardarSesion])

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY)
    setCliente(null)
    setError('')
  }, [])

  const value = {
    cliente,
    cargando,
    error,
    login,
    registrar,
    logout,
  }

  return (
    <ClienteAuthContext.Provider value={value}>
      {children}
    </ClienteAuthContext.Provider>
  )
}

export function useClienteAuth() {
  const context = useContext(ClienteAuthContext)

  if (!context) {
    throw new Error(
      'useClienteAuth debe usarse dentro de ClienteAuthProvider',
    )
  }

  return context
}
