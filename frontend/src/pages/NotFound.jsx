import React from 'react';
import { Link } from 'react-router-dom';

const NotFound = () => {
  return (
    <div style={containerStyle}>
      <div style={contentStyle}>
        <div style={errorCodeStyle}>404</div>
        <h1 style={titleStyle}>Page Not Found</h1>
        <p style={messageStyle}>
          Sorry, the page you are looking for doesn't exist or has been moved.
        </p>
        <div style={actionsStyle}>
          <Link to="/" style={primaryButtonStyle}>
            Go Home
          </Link>
          <Link to="/products" style={secondaryButtonStyle}>
            Browse Products
          </Link>
        </div>
        <div style={helpSectionStyle}>
          <h3>Looking for something specific?</h3>
          <div style={linksStyle}>
            <Link to="/products" style={linkStyle}>All Products</Link>
            <Link to="/cart" style={linkStyle}>Shopping Cart</Link>
            <Link to="/login" style={linkStyle}>Sign In</Link>
            <Link to="/register" style={linkStyle}>Create Account</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

const containerStyle = {
  minHeight: 'calc(100vh - 200px)',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  padding: '2rem 1rem',
  backgroundColor: '#f8f9fa',
};

const contentStyle = {
  textAlign: 'center',
  maxWidth: '600px',
  width: '100%',
};

const errorCodeStyle = {
  fontSize: '8rem',
  fontWeight: 'bold',
  color: '#2c5aa0',
  lineHeight: 1,
  marginBottom: '1rem',
  opacity: 0.1,
};

const titleStyle = {
  fontSize: '2.5rem',
  marginBottom: '1rem',
  color: '#333',
};

const messageStyle = {
  fontSize: '1.2rem',
  color: '#666',
  marginBottom: '2rem',
  lineHeight: 1.6,
};

const actionsStyle = {
  display: 'flex',
  gap: '1rem',
  justifyContent: 'center',
  marginBottom: '3rem',
  flexWrap: 'wrap',
};

const primaryButtonStyle = {
  padding: '1rem 2rem',
  backgroundColor: '#2c5aa0',
  color: 'white',
  textDecoration: 'none',
  borderRadius: '4px',
  fontWeight: '600',
  fontSize: '1.1rem',
  transition: 'background-color 0.3s',
};

const secondaryButtonStyle = {
  padding: '1rem 2rem',
  backgroundColor: 'transparent',
  color: '#2c5aa0',
  textDecoration: 'none',
  border: '2px solid #2c5aa0',
  borderRadius: '4px',
  fontWeight: '600',
  fontSize: '1.1rem',
  transition: 'background-color 0.3s',
};

const helpSectionStyle = {
  padding: '2rem',
  backgroundColor: 'white',
  borderRadius: '8px',
  boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
};

const linksStyle = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
  gap: '1rem',
  marginTop: '1rem',
};

const linkStyle = {
  padding: '0.75rem 1rem',
  backgroundColor: '#f8f9fa',
  color: '#2c5aa0',
  textDecoration: 'none',
  borderRadius: '4px',
  transition: 'background-color 0.3s',
  fontSize: '0.9rem',
};

export default NotFound;
