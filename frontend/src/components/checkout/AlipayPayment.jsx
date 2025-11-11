import React, { useState } from 'react';
import './AlipayPayment.css';

const AlipayPayment = ({ 
  orderPreview, 
  onPaymentSuccess, 
  onStepChange,
  loading 
}) => {
  const [selectedPayment, setSelectedPayment] = useState('alipay');
  const [agreeTerms, setAgreeTerms] = useState(false);

  const paymentMethods = [
    {
      id: 'alipay',
      name: '支付宝',
      icon: '💰',
      description: '推荐使用'
    },
    {
      id: 'wechat',
      name: '微信支付',
      icon: '💬',
      description: '便捷支付'
    },
    {
      id: 'bank',
      name: '银行卡支付',
      icon: '💳',
      description: '支持储蓄卡/信用卡'
    }
  ];

  const handlePaymentSubmit = async () => {
    if (!agreeTerms) {
      alert('请同意用户协议和隐私政策');
      return;
    }

    // 模拟支付处理
    try {
      const paymentData = {
        paymentMethod: selectedPayment,
        paymentId: `PAY_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        amount: orderPreview.totalAmount
      };

      // 在实际项目中，这里会调用支付网关API
      setTimeout(() => {
        onPaymentSuccess(paymentData);
      }, 2000);
      
    } catch (error) {
      console.error('支付处理失败:', error);
      alert('支付处理失败，请重试');
    }
  };

  const handleBackToReview = () => {
    onStepChange(2);
  };

  return (
    <div className="alipay-payment">
      <h2>选择支付方式</h2>
      
      {/* 订单金额显示 */}
      <div className="payment-amount">
        <div className="amount-label">应付金额</div>
        <div className="amount-value">¥{orderPreview?.totalAmount.toFixed(2)}</div>
      </div>

      {/* 支付方式选择 */}
      <div className="payment-methods">
        <h3>选择支付方式</h3>
        <div className="methods-list">
          {paymentMethods.map(method => (
            <div 
              key={method.id}
              className={`method-item ${selectedPayment === method.id ? 'selected' : ''}`}
              onClick={() => setSelectedPayment(method.id)}
            >
              <div className="method-icon">{method.icon}</div>
              <div className="method-info">
                <div className="method-name">{method.name}</div>
                <div className="method-description">{method.description}</div>
              </div>
              <div className="method-radio">
                <div className={`radio-dot ${selectedPayment === method.id ? 'active' : ''}`}></div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 支付说明 */}
      <div className="payment-instructions">
        <h4>支付说明</h4>
        <div className="instructions-content">
          {selectedPayment === 'alipay' && (
            <div className="instruction-alipay">
              <p>• 推荐使用支付宝扫码支付</p>
              <p>• 支付完成后会自动跳转</p>
              <p>• 如遇问题请联系客服</p>
            </div>
          )}
          {selectedPayment === 'wechat' && (
            <div className="instruction-wechat">
              <p>• 请使用微信扫一扫</p>
              <p>• 支持微信支付余额、银行卡</p>
              <p>• 支付完成请勿关闭页面</p>
            </div>
          )}
          {selectedPayment === 'bank' && (
            <div className="instruction-bank">
              <p>• 支持储蓄卡、信用卡支付</p>
              <p>• 请确保银行卡余额充足</p>
              <p>• 支付过程请勿刷新页面</p>
            </div>
          )}
        </div>
      </div>

      {/* 协议确认 */}
      <div className="terms-agreement">
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={agreeTerms}
            onChange={(e) => setAgreeTerms(e.target.checked)}
          />
          <span className="checkmark"></span>
          我已阅读并同意
          <a href="/terms" target="_blank" rel="noopener noreferrer">《用户协议》</a>
          和
          <a href="/privacy" target="_blank" rel="noopener noreferrer">《隐私政策》</a>
        </label>
      </div>

      {/* 支付按钮 */}
      <div className="payment-actions">
        <button 
          type="button" 
          className="back-btn"
          onClick={handleBackToReview}
          disabled={loading}
        >
          返回上一步
        </button>
        <button 
          type="button" 
          className="pay-btn"
          onClick={handlePaymentSubmit}
          disabled={loading || !agreeTerms}
        >
          {loading ? (
            <>
              <div className="loading-spinner"></div>
              支付处理中...
            </>
          ) : (
            `确认支付 ¥${orderPreview?.totalAmount.toFixed(2)}`
          )}
        </button>
      </div>

      {/* 安全提示 */}
      <div className="security-notice">
        <div className="security-icon">🔒</div>
        <div className="security-text">
          <strong>安全支付保障</strong>
          <span>您的支付信息已加密处理</span>
        </div>
      </div>
    </div>
  );
};

export default AlipayPayment;