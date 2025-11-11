import React from 'react';
import './OrderTracking.css';

const OrderTracking = ({ trackingInfo }) => {
  const formatDateTime = (dateString) => {
    return new Date(dateString).toLocaleString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusIcon = (status) => {
    const icons = {
      info: 'ℹ️',
      picked: '📦',
      transit: '🚚',
      out: '📤',
      delivered: '✅'
    };
    return icons[status] || '●';
  };

  const getStatusColor = (status) => {
    const colors = {
      info: '#17a2b8',
      picked: '#007bff',
      transit: '#6f42c1',
      out: '#fd7e14',
      delivered: '#28a745'
    };
    return colors[status] || '#6c757d';
  };

  return (
    <div className="order-tracking">
      <h3>物流跟踪</h3>
      
      <div className="tracking-header">
        <div className="courier-info">
          <span className="courier-name">{trackingInfo.courier}</span>
          <span className="tracking-number">运单号: {trackingInfo.trackingNumber}</span>
        </div>
        <div className="estimated-delivery">
          预计送达: {trackingInfo.estimatedDelivery}
        </div>
      </div>

      <div className="tracking-timeline">
        {trackingInfo.events.map((event, index) => (
          <div key={index} className="tracking-event">
            <div 
              className="event-indicator"
              style={{ borderColor: getStatusColor(event.status) }}
            >
              <span 
                className="event-icon"
                style={{ color: getStatusColor(event.status) }}
              >
                {getStatusIcon(event.status)}
              </span>
            </div>
            
            <div className="event-content">
              <div className="event-description">
                {event.description}
              </div>
              <div className="event-time">
                {formatDateTime(event.time)}
              </div>
              {event.location && (
                <div className="event-location">
                  📍 {event.location}
                </div>
              )}
            </div>
            
            {index < trackingInfo.events.length - 1 && (
              <div className="event-connector" />
            )}
          </div>
        ))}
      </div>

      {/* 配送员信息 */}
      {trackingInfo.deliveryPerson && (
        <div className="delivery-person-info">
          <h4>配送员信息</h4>
          <div className="person-details">
            <div className="person-avatar">
              {trackingInfo.deliveryPerson.avatar ? (
                <img 
                  src={trackingInfo.deliveryPerson.avatar} 
                  alt={trackingInfo.deliveryPerson.name}
                />
              ) : (
                <div className="avatar-placeholder">
                  {trackingInfo.deliveryPerson.name.charAt(0)}
                </div>
              )}
            </div>
            <div className="person-info">
              <div className="person-name">
                {trackingInfo.deliveryPerson.name}
                {trackingInfo.deliveryPerson.rating && (
                  <span className="person-rating">
                    ⭐ {trackingInfo.deliveryPerson.rating}
                  </span>
                )}
              </div>
              <div className="person-phone">
                📞 {trackingInfo.deliveryPerson.phone}
              </div>
              {trackingInfo.deliveryPerson.vehicle && (
                <div className="person-vehicle">
                  🛵 {trackingInfo.deliveryPerson.vehicle}
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* 配送提示 */}
      <div className="delivery-tips">
        <h4>配送提示</h4>
        <ul>
          <li>请保持手机畅通，方便配送员联系</li>
          <li>如临时无法收货，可联系配送员协商放置位置</li>
          <li>收到商品后请及时检查商品完好性</li>
          <li>如有问题请在2小时内联系客服处理</li>
        </ul>
      </div>
    </div>
  );
};

export default OrderTracking;