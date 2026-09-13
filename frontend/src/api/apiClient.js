import axios from 'axios'

import { loginRequest, msalInstance } from '../auth/msalConfig'

/*
 * Cliente axios centralizado con interceptor de autenticación.
 * Antes de adquirir un token vía MSAL (staff/Azure AD), revisa si
 * existe una sesión de cliente propio ("clienteAuth" en localStorage)
 * y, de ser así, usa ese token directamente sin llamar a MSAL.
 */
const apiClient = axios.create({
  baseURL:
    import.meta.env.VITE_API_BASE_URL ||
    'http://localhost:8080',
})

apiClient.interceptors.request.use(async (config) => {
  const clienteAuthRaw =
    localStorage.getItem('clienteAuth')

  if (clienteAuthRaw) {
    const clienteAuth = JSON.parse(clienteAuthRaw)

    config.headers.Authorization = `Bearer ${clienteAuth.token}`

    return config
  }

  const account = msalInstance.getAllAccounts()[0]

  if (account) {
    const response =
      await msalInstance.acquireTokenSilent({
        ...loginRequest,
        account,
      })

    config.headers.Authorization = `Bearer ${response.accessToken}`
  }

  return config
})

export default apiClient
