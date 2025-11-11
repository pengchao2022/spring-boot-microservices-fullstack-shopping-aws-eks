import React from 'react';
import './CheckoutSteps.css';

const CheckoutSteps = ({ currentStep, steps }) => {
  return (
    <div className="checkout-steps">
      {steps.map((step, index) => (
        <div 
          key={index} 
          className={`step ${index + 1 === currentStep ? 'active' : ''} ${
            index + 1 < currentStep ? 'completed' : ''
          }`}
        >
          <div className="step-number">
            {index + 1 < currentStep ? '✓' : index + 1}
          </div>
          <div className="step-title">{step}</div>
          {index < steps.length - 1 && <div className="step-connector"></div>}
        </div>
      ))}
    </div>
  );
};

export default CheckoutSteps;