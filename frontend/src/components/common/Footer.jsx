import React from 'react';

const Footer = () => {
  const currentYear = new Date().getFullYear(); // 添加这行
  
  return (
    <footer style={footerStyle}>
      <div style={containerStyle}>
        <div style={contentStyle}>
          <p>&copy; {currentYear} 普罗米修甄选 Designed by Pengchao Ma,All rights reserved.</p>
          <div style={linksStyle}>
            <a href="/about" style={linkStyle}>关于我们</a>
            <a href="/contact" style={linkStyle}>联系我们</a>
            <a href="/privacy" style={linkStyle}>隐私政策</a>
            <a href="/terms" style={linkStyle}>服务条款</a>
          </div>
        </div>
      </div>
    </footer>
  );
};

const footerStyle = {
  backgroundColor: '#f8f9fa',
  padding: '2rem 0',
  borderTop: '1px solid #e9ecef',
  marginTop: 'auto',
};

const containerStyle = {
  maxWidth: '1200px',
  margin: '0 auto',
  padding: '0 1rem',
};

const contentStyle = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  flexWrap: 'wrap',
  gap: '1rem',
};

const linksStyle = {
  display: 'flex',
  gap: '1.5rem',
};

const linkStyle = {
  color: '#6c757d',
  textDecoration: 'none',
  fontSize: '0.9rem',
};

export default Footer;