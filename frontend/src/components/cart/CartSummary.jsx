import React from 'react';

const CartSummary = ({ items, onCheckout, onContinueShopping }) => {
  // 计算总价
  const calculateTotals = () => {
    const subtotal = items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const originalSubtotal = items.reduce((sum, item) => sum + (item.originalPrice * item.quantity), 0);
    const savings = originalSubtotal - subtotal;
    const shipping = subtotal > 59 ? 0 : 8; // 满59免运费
    const total = subtotal + shipping;

    return {
      subtotal: subtotal.toFixed(2),
      originalSubtotal: originalSubtotal.toFixed(2),
      savings: savings.toFixed(2),
      shipping: shipping.toFixed(2),
      total: total.toFixed(2),
      freeShipping: subtotal > 59
    };
  };

  const totals = calculateTotals();
  const totalItems = items.reduce((sum, item) => sum + item.quantity, 0);

  return (
    <div className="cart-summary">
      <h3>订单汇总</h3>
      
      <div className="summary-details">
        <div className="summary-row">
          <span>商品数量</span>
          <span>{totalItems} 件</span>
        </div>
        
        <div className="summary-row">
          <span>商品总价</span>
          <div className="price-comparison">
            {totals.savings > 0 && (
              <span className="original-price">¥{totals.originalSubtotal}</span>
            )}
            <span className="current-price">¥{totals.subtotal}</span>
          </div>
        </div>

        {totals.savings > 0 && (
          <div className="summary-row savings">
            <span>节省金额</span>
            <span className="savings-amount">-¥{totals.savings}</span>
          </div>
        )}

        <div className="summary-row shipping">
          <span>运费</span>
          <span className={totals.freeShipping ? 'free-shipping' : ''}>
            {totals.freeShipping ? '免运费' : `¥${totals.shipping}`}
          </span>
        </div>

        {!totals.freeShipping && (
          <div className="shipping-notice">
            再买 ¥{(59 - parseFloat(totals.subtotal)).toFixed(2)} 免运费
          </div>
        )}

        <div className="summary-divider"></div>

        <div className="summary-row total">
          <span>应付总额</span>
          <span className="total-price">¥{totals.total}</span>
        </div>
      </div>

      <div className="checkout-actions">
        <button className="checkout-btn" onClick={onCheckout}>
          立即结算
        </button>
        <button className="continue-shopping-btn" onClick={onContinueShopping}>
          继续购物
        </button>
      </div>

      <div className="shipping-benefits">
        <div className="benefit-item">
          <span className="benefit-icon">🚚</span>
          <span>满59元免运费</span>
        </div>
        <div className="benefit-item">
          <span className="benefit-icon">🏪</span>
          <span>24小时内发货</span>
        </div>
        <div className="benefit-item">
          <span className="benefit-icon">🔒</span>
          <span>安全支付保障</span>
        </div>
      </div>
    </div>
  );
};

export default CartSummary;