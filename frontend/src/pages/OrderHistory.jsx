import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { OrderCard } from '../components/order';
import './OrderHistory.css';

const OrderHistory = () => {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({
    status: '',
    dateRange: 'all'
  });
  const [pagination, setPagination] = useState({
    page: 1,
    pageSize: 10,
    total: 0,
    totalPages: 0
  });

  useEffect(() => {
    fetchOrders();
  }, [filters, pagination.page]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);
      const token = localStorage.getItem('token');
      
      if (!token) {
        navigate('/login');
        return;
      }

      // 构建查询参数
      const params = new URLSearchParams({
        page: (pagination.page - 1).toString(),
        size: pagination.pageSize.toString()
      });

      if (filters.status) {
        params.append('status', filters.status);
      }

      // 使用相对路径，让反向代理处理
      const response = await fetch(`/api/orders?${params}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
          return;
        }
        throw new Error('获取订单列表失败');
      }

      const data = await response.json();
      
      // 转换数据格式
      const transformedOrders = data.content ? data.content.map(transformOrderData) : [];
      setOrders(transformedOrders);
      
      // 更新分页信息
      if (data) {
        setPagination(prev => ({
          ...prev,
          total: data.totalElements || data.length || 0,
          totalPages: data.totalPages || Math.ceil((data.totalElements || data.length || 0) / prev.pageSize)
        }));
      }
      
    } catch (err) {
      setError(err.message);
      console.error('获取订单列表错误:', err);
    } finally {
      setLoading(false);
    }
  };

  // 转换订单数据为前端格式
  const transformOrderData = (orderData) => {
    // 计算商品总数
    const totalItems = orderData.items ? orderData.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
    
    return {
      id: orderData.id,
      orderNumber: orderData.orderNumber,
      status: mapOrderStatus(orderData.status),
      createdAt: orderData.createdAt,
      updatedAt: orderData.updatedAt,
      totalAmount: orderData.totalAmount,
      items: orderData.items ? orderData.items.slice(0, 3).map(item => ({
        id: item.productId,
        name: item.productName,
        image: item.imageUrl || '/images/default-product.jpg',
        quantity: item.quantity,
        price: parseFloat(item.unitPrice)
      })) : [],
      totalItems: totalItems,
      // 配送信息摘要
      shippingInfo: {
        recipientName: `${orderData.shippingFirstName || ''} ${orderData.shippingLastName || ''}`.trim() || orderData.shippingName,
        city: orderData.shippingCity
      }
    };
  };

  // 映射订单状态
  const mapOrderStatus = (status) => {
    const statusMap = {
      'PENDING': 'pending',
      'CONFIRMED': 'paid',
      'PROCESSING': 'processing',
      'SHIPPED': 'shipped',
      'DELIVERED': 'delivered',
      'CANCELLED': 'cancelled'
    };
    return statusMap[status] || 'pending';
  };

  const handleFilterChange = (filterType, value) => {
    setFilters(prev => ({
      ...prev,
      [filterType]: value
    }));
    setPagination(prev => ({ ...prev, page: 1 })); // 重置到第一页
  };

  const handlePageChange = (newPage) => {
    setPagination(prev => ({ ...prev, page: newPage }));
  };

  const handleOrderClick = (order) => {
    navigate(`/orders/${order.id}`);
  };

  const handleCreateOrder = () => {
    navigate('/cart');
  };

  const statusOptions = [
    { value: '', label: '全部状态' },
    { value: 'PENDING', label: '待支付' },
    { value: 'CONFIRMED', label: '已支付' },
    { value: 'PROCESSING', label: '处理中' },
    { value: 'SHIPPED', label: '已发货' },
    { value: 'DELIVERED', label: '已完成' },
    { value: 'CANCELLED', label: '已取消' }
  ];

  const dateRangeOptions = [
    { value: 'all', label: '全部时间' },
    { value: 'week', label: '最近一周' },
    { value: 'month', label: '最近一月' },
    { value: 'quarter', label: '最近三月' }
  ];

  if (loading && orders.length === 0) {
    return (
      <div className="order-history-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>加载订单列表中...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="order-history-page">
      <div className="order-history-header">
        <h1>我的订单</h1>
        <button 
          className="btn-create-order"
          onClick={handleCreateOrder}
        >
          继续购物
        </button>
      </div>

      {/* 筛选器 */}
      <div className="order-filters">
        <div className="filter-group">
          <label htmlFor="status-filter">订单状态</label>
          <select
            id="status-filter"
            value={filters.status}
            onChange={(e) => handleFilterChange('status', e.target.value)}
          >
            {statusOptions.map(option => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label htmlFor="date-filter">时间范围</label>
          <select
            id="date-filter"
            value={filters.dateRange}
            onChange={(e) => handleFilterChange('dateRange', e.target.value)}
          >
            {dateRangeOptions.map(option => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-actions">
          <button 
            className="btn-reset"
            onClick={() => setFilters({ status: '', dateRange: 'all' })}
          >
            重置筛选
          </button>
        </div>
      </div>

      {/* 错误提示 */}
      {error && (
        <div className="error-message">
          <p>{error}</p>
          <button onClick={fetchOrders} className="btn-retry">
            重试
          </button>
        </div>
      )}

      {/* 订单列表 */}
      <div className="orders-container">
        {orders.length === 0 ? (
          <div className="empty-orders">
            <div className="empty-icon">📦</div>
            <h3>暂无订单</h3>
            <p>您还没有任何订单，快去选购心仪的商品吧！</p>
            <button 
              className="btn-shopping"
              onClick={handleCreateOrder}
            >
              去购物
            </button>
          </div>
        ) : (
          <>
            <div className="orders-list">
              {orders.map(order => (
                <OrderCard
                  key={order.id}
                  order={order}
                  onClick={handleOrderClick}
                />
              ))}
            </div>

            {/* 分页控件 */}
            {pagination.totalPages > 1 && (
              <div className="pagination">
                <button
                  className="pagination-btn"
                  disabled={pagination.page === 1}
                  onClick={() => handlePageChange(pagination.page - 1)}
                >
                  上一页
                </button>
                
                <div className="pagination-info">
                  第 {pagination.page} 页，共 {pagination.totalPages} 页
                </div>
                
                <button
                  className="pagination-btn"
                  disabled={pagination.page === pagination.totalPages}
                  onClick={() => handlePageChange(pagination.page + 1)}
                >
                  下一页
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {/* 统计信息 */}
      {orders.length > 0 && (
        <div className="order-stats">
          <div className="stat-item">
            <span className="stat-label">订单总数</span>
            <span className="stat-value">{pagination.total}</span>
          </div>
          <div className="stat-item">
            <span className="stat-label">待处理</span>
            <span className="stat-value">
              {orders.filter(order => ['pending', 'paid'].includes(order.status)).length}
            </span>
          </div>
          <div className="stat-item">
            <span className="stat-label">已完成</span>
            <span className="stat-value">
              {orders.filter(order => order.status === 'delivered').length}
            </span>
          </div>
        </div>
      )}
    </div>
  );
};

export default OrderHistory;