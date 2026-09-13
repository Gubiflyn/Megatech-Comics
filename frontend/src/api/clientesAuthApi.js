import axios from 'axios'

/*
 * Cliente axios separado de apiClient.js: estas dos llamadas
 * (registro/login de clientes) son públicas y nunca deben llevar
 * un token (ni MSAL ni clienteAuth).
 */
const clientesAuthClient = axios.create({
  baseURL:
    import.meta.env.VITE_API_BASE_URL ||
    'http://localhost:8080',
})

export async function registrarCliente({
  email,
  password,
  nombreCompleto,
}) {
  const response = await clientesAuthClient.post(
    '/api/auth/clientes/registro',
    { email, password, nombreCompleto },
  )

  return response.data
}

export async function loginCliente({
  email,
  password,
}) {
  const response = await clientesAuthClient.post(
    '/api/auth/clientes/login',
    { email, password },
  )

  return response.data
}
