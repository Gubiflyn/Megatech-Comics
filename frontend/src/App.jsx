import {
  Routes,
  Route,
} from 'react-router-dom'

import Navbar from './components/layout/Navbar'
import Footer from './components/layout/Footer'

import HomePage from './pages/HomePage'
import CatalogPage from './pages/CatalogPage'
import ComicDetailPage from './pages/ComicDetailPage'
import CartPage from './pages/CartPage'
import CheckoutPage from './pages/CheckoutPage'
import OrdersPage from './pages/OrdersPage'
import ProfilePage from './pages/ProfilePage'
import ClienteAuthPage from './pages/ClienteAuthPage'
import ClientePerfilPage from './pages/ClientePerfilPage'
import AccessSelectorPage from './pages/AccessSelectorPage'

function App() {
  return (
    <div className="app">
      <Navbar />

      <Routes>
        <Route
          path="/"
          element={<HomePage />}
        />

        <Route
          path="/catalogo"
          element={<CatalogPage />}
        />

        <Route
          path="/catalogo/:id"
          element={<ComicDetailPage />}
        />

        <Route
          path="/carrito"
          element={<CartPage />}
        />

        <Route
          path="/checkout"
          element={<CheckoutPage />}
        />

        <Route
          path="/pedidos"
          element={<OrdersPage />}
        />

        <Route
          path="/perfil"
          element={<ProfilePage />}
        />

        <Route
          path="/cuenta-cliente"
          element={<ClienteAuthPage />}
        />

        <Route
          path="/perfil-cliente"
          element={<ClientePerfilPage />}
        />

        <Route
          path="/acceso"
          element={<AccessSelectorPage />}
        />
      </Routes>

      <Footer />
    </div>
  )
}

export default App