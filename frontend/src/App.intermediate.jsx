import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import Home from './pages/Home';
import NotFound from './pages/NotFound';
import Login from './pages/Login';
import Register from './pages/Register';
import AlipayCallback from './pages/AlipayCallback';
import Profile from './pages/Profile';

// 导入通用页面组件
import FruitDetailPage from './pages/FruitDetailPage';
import VegetablePage from './pages/vegetables/VegetablePage';

// 导入后台管理界面
import AppleManagement from './pages/admin/AppleManagement';

// 导入分类页面
import AppleCategoryPage from './pages/fruits/AppleCategoryPage';
import KiwiCategoryPage from './pages/fruits/KiwiCategoryPage';

// 导入其他页面
import About from './pages/AboutUS';
import Contact from './pages/ContactUS';
import PrivacyPolicy from './pages/Pravicypolicy';
import TermsOfService from './pages/TermsOfService';

// 购物车相关页面
import Cart from './pages/CartPage'; // 修改这里：从 './pages/Cart' 改为 './pages/CartPage'
import Checkout from './pages/Checkout';
import OrderHistory from './pages/OrderHistory';
import OrderDetail from './pages/OrderDetail';

// 导入 ProductList 组件
import ProductList from './pages/ProductList';

// 导入搜索结果页面
import ProductSearchResults from './pages/ProductSearchResults';

import Header from './components/common/Header';
import Footer from './components/common/Footer';

function IntermediateApp() {
  return (
    <AuthProvider>
      <CartProvider>
        <Router>
          <div className="App" style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Header />
            <main style={{ 
              flex: 1, 
              backgroundColor: '#f8f9fa',
              padding: '1rem 0'
            }}>
              <Routes>
                {/* 首页 */}
                <Route path="/" element={<Home />} />
                
                {/* 用户认证相关 */}
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/alipay-callback" element={<AlipayCallback />} />
                <Route path="/profile" element={<Profile />} />
                
                {/* 信息页面路由 */}
                <Route path="/about" element={<About />} />
                <Route path="/contact" element={<Contact />} />
                <Route path="/privacy" element={<PrivacyPolicy />} />
                <Route path="/terms" element={<TermsOfService />} />
                
                {/* 🍎 分类页面路由 */}
                <Route path="/fruits/apples" element={<AppleCategoryPage />} />
                <Route path="/fruits/kiwis" element={<KiwiCategoryPage />} />
                
                {/* 产品详情页面路由 */}
                <Route path="/fruit/:productName" element={<FruitDetailPage />} />
                <Route path="/vegetable/:productName" element={<VegetablePage />} />
                
                {/* 购物车和订单相关 */}
                <Route path="/cart" element={<Cart />} />
                <Route path="/checkout" element={<Checkout />} />
                <Route path="/orders" element={<OrderHistory />} />
                <Route path="/orders/:orderId" element={<OrderDetail />} />
                
                {/* ✅ 修改：产品列表页面 */}
                <Route path="/products" element={<ProductList />} />
                
                {/* ✅ 新增：搜索结果页面 */}
                <Route path="/search" element={<ProductSearchResults />} />
                
                {/* 后台管理页面 */}
                <Route path="/admin/apples" element={<AppleManagement />} />
                
                {/* 404页面 */}
                <Route path="*" element={<NotFound />} />
              </Routes>
            </main>
            <Footer />
          </div>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
}

export default IntermediateApp;