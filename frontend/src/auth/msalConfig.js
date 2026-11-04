
import { PublicClientApplication } from '@azure/msal-browser'

export const msalConfig = {
  auth: {
    clientId: '8df25ac7-e8c9-41fc-a9f8-7fa1e8940f1e',
    authority:
      'https://login.microsoftonline.com/f296b836-9dcb-49b9-ab6c-8e74696608f6',
    redirectUri:
      window.location.hostname === 'localhost'
        ? 'http://localhost:5173'
        : 'https://54-204-203-32.nip.io/',
    postLogoutRedirectUri:
      window.location.hostname === 'localhost'
        ? 'http://localhost:5173'
        : 'https://54-204-203-32.nip.io/',
  },
  cache: {
    cacheLocation: 'localStorage',
    storeAuthStateInCookie: false,
  },
}

export const loginRequest = {
  scopes: [
    'api://79e27497-9b28-4c3c-8e33-47b3de560ce1/access_as_user',
  ],
}

export const msalInstance =
  new PublicClientApplication(msalConfig)
