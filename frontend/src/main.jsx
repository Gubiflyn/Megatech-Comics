import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { MsalProvider } from '@azure/msal-react'

import App from './App.jsx'
import { msalInstance } from './auth/msalConfig.js'

import {
  CartProvider,
} from './context/CartContext.jsx'

import {
  OrderProvider,
} from './context/OrderContext.jsx'

import './index.css'

createRoot(
  document.getElementById('root'),
).render(
  <StrictMode>
    <MsalProvider instance={msalInstance}>
      <BrowserRouter>
        <CartProvider>
          <OrderProvider>
            <App />
          </OrderProvider>
        </CartProvider>
      </BrowserRouter>
    </MsalProvider>
  </StrictMode>,
)
