import React from 'react';
import './OrderStatus.css';

const OrderStatus = ({ status, timeline = [] }) => {
  const statusSteps = [
    { key: 'pending', label: '待支付', icon: '⏰' },
    { key: 'paid', label: '已支付', icon: '✅' },
    { key: 'processing', label: '备货中', icon: '📦' },
    { key: 'shipped', label: '已发货', icon: '🚚' },
    { key: 'delivered', label: '已送达', icon: '🏠' }
  ];

  const getCurrentStepIndex = () => {
    return statusSteps.findIndex(step => step.key === status);
  };

  const currentStepIndex = getCurrentStepIndex();

  const formatTimelineDate = (dateString) => {
    return new Date(dateString).toLocaleString('zh-CN', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="order-status">
      <h3>订单状态</h3>
      
      <div className="status-timeline">
        {statusSteps.map((step, index) => (
          <div key={step.key} className="timeline-step">
            <div className={`step-indicator ${index <= currentStepIndex ? 'completed' : ''} ${index === currentStepIndex ? 'current' : ''}`}>
              <span className="step-icon">{step.icon}</span>
            </div>
            
            <div className="step-content">
              <div className="step-label">{step.label}</div>
              {index <= currentStepIndex && timeline[index] && (
                <div className="step-time">
                  {formatTimelineDate(timeline[index].time)}
                </div>
              )}
              {index === currentStepIndex && timeline[index]?.description && (
                <div className="step-description">
                  {timeline[index].description}
                </div>
              )}
            </div>
            
            {index < statusSteps.length - 1 && (
              <div className={`step-connector ${index < currentStepIndex ? 'completed' : ''}`} />
            )}
          </div>
        ))}
      </div>

      {/* 当前状态说明 */}
      <div className="current-status-info">
        <h4>当前状态说明</h4>
        {status === 'pending' && (
          <p>订单已创建，请在30分钟内完成支付，超时订单将自动取消。</p>
        )}
        {status === 'paid' && (
          <p>支付成功！我们正在为您准备商品，预计2小时内发货。</p>
        )}
        {status === 'processing' && (
          <p>商品正在出库打包中，我们的工作人员会仔细检查商品质量。</p>
        )}
        {status === 'shipped' && (
          <p>商品已发出，正在配送途中，请保持手机畅通。</p>
        )}
        {status === 'delivered' && (
          <p>商品已送达，感谢您的购买！如有问题请及时联系客服。</p>
        )}
      </div>

      {/* 客服信息 */}
      <div className="customer-service">
        <h4>需要帮助？</h4>
        <p>如有任何问题，请联系我们的客服团队：</p>
        <div className="contact-info">
          <span>📞 客服电话: 400-123-4567</span>
          <span>🕒 服务时间: 8:00-22:00</span>
        </div>
      </div>
    </div>
  );
};

export default OrderStatus;