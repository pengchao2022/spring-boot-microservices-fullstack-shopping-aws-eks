import React, { useState } from 'react';

const ContactUs = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    subject: '',
    message: ''
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // 处理表单提交逻辑
    console.log('表单数据:', formData);
    alert('感谢您的留言，我们会尽快回复您！');
    setFormData({
      name: '',
      email: '',
      phone: '',
      subject: '',
      message: ''
    });
  };

  return (
    <div style={containerStyle}>
      <div style={contentStyle}>
        {/* 页面头部 */}
        <div style={headerStyle}>
          <h1 style={titleStyle}>联系我们</h1>
          <p style={subtitleStyle}>我们很乐意听到您的声音</p>
        </div>

        <div style={mainContentStyle}>
          {/* 联系信息 */}
          <div style={infoSectionStyle}>
            <h2 style={sectionTitleStyle}>联系信息</h2>
            <div style={contactListStyle}>
              <div style={contactItemStyle}>
                <div style={contactIconStyle}>📧</div>
                <div style={contactInfoStyle}>
                  <h4 style={contactTitleStyle}>邮箱</h4>
                  <p style={contactDetailStyle}>contact@example.com</p>
                  <p style={contactDetailStyle}>support@example.com</p>
                </div>
              </div>
              <div style={contactItemStyle}>
                <div style={contactIconStyle}>📞</div>
                <div style={contactInfoStyle}>
                  <h4 style={contactTitleStyle}>电话</h4>
                  <p style={contactDetailStyle}>+8618510656167</p>
                  <p style={contactDetailStyle}>+86 21 5345 6781</p>
                </div>
              </div>
              <div style={contactItemStyle}>
                <div style={contactIconStyle}>🏢</div>
                <div style={contactInfoStyle}>
                  <h4 style={contactTitleStyle}>地址</h4>
                  <p style={contactDetailStyle}>上海市杨浦区尚浦中心A座4楼401</p>
                  <p style={contactDetailStyle}>邮编:2180411</p>
                </div>
              </div>
              <div style={contactItemStyle}>
                <div style={contactIconStyle}>🕒</div>
                <div style={contactInfoStyle}>
                  <h4 style={contactTitleStyle}>工作时间</h4>
                  <p style={contactDetailStyle}>周一至周五: 9:00-18:00</p>
                  <p style={contactDetailStyle}>周六: 9:00-17:00</p>
                </div>
              </div>
            </div>

            {/* 社交媒体 */}
            <div style={socialSectionStyle}>
              <h4 style={socialTitleStyle}>关注我们</h4>
              <div style={socialLinksStyle}>
                <a href="#" style={socialLinkStyle}>微信</a>
                <a href="#" style={socialLinkStyle}>微博</a>
                <a href="#" style={socialLinkStyle}>知乎</a>
                <a href="#" style={socialLinkStyle}>抖音</a>
              </div>
            </div>
          </div>

          {/* 联系表单 */}
          <div style={formSectionStyle}>
            <h2 style={sectionTitleStyle}>发送消息</h2>
            <form onSubmit={handleSubmit} style={formStyle}>
              <div style={formRowStyle}>
                <div style={inputGroupStyle}>
                  <label htmlFor="name" style={labelStyle}>姓名 *</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    required
                    style={inputStyle}
                    placeholder="请输入您的姓名"
                  />
                </div>
                <div style={inputGroupStyle}>
                  <label htmlFor="email" style={labelStyle}>邮箱 *</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required
                    style={inputStyle}
                    placeholder="请输入您的邮箱"
                  />
                </div>
              </div>

              <div style={formRowStyle}>
                <div style={inputGroupStyle}>
                  <label htmlFor="phone" style={labelStyle}>电话</label>
                  <input
                    type="tel"
                    id="phone"
                    name="phone"
                    value={formData.phone}
                    onChange={handleInputChange}
                    style={inputStyle}
                    placeholder="请输入您的电话"
                  />
                </div>
                <div style={inputGroupStyle}>
                  <label htmlFor="subject" style={labelStyle}>主题 *</label>
                  <select
                    id="subject"
                    name="subject"
                    value={formData.subject}
                    onChange={handleInputChange}
                    required
                    style={selectStyle}
                  >
                    <option value="">请选择主题</option>
                    <option value="product">产品咨询</option>
                    <option value="technical">技术支持</option>
                    <option value="cooperation">商务合作</option>
                    <option value="complaint">投诉建议</option>
                    <option value="other">其他</option>
                  </select>
                </div>
              </div>

              <div style={inputGroupStyle}>
                <label htmlFor="message" style={labelStyle}>留言内容 *</label>
                <textarea
                  id="message"
                  name="message"
                  value={formData.message}
                  onChange={handleInputChange}
                  required
                  style={textareaStyle}
                  placeholder="请详细描述您的问题或需求..."
                  rows="6"
                />
              </div>

              <button type="submit" style={submitButtonStyle}>
                发送消息
              </button>
            </form>
          </div>
        </div>

        {/* 地图位置 */}
        <section style={mapSectionStyle}>
          <h2 style={sectionTitleStyle}>我们的位置</h2>
          <div style={mapPlaceholderStyle}>
            <div style={mapContentStyle}>
              <div style={mapIconStyle}>🗺️</div>
              <h3 style={mapTitleStyle}>地图位置</h3>
              <p style={mapDescStyle}>
                上海市杨浦区尚浦中心A座401
              </p>
              <p style={mapTipStyle}>
                点击查看详细地图位置
              </p>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

// 样式定义
const containerStyle = {
  minHeight: '100vh',
  backgroundColor: '#f8f9fa',
};

const contentStyle = {
  maxWidth: '1200px',
  margin: '0 auto',
  padding: '2rem 1rem',
};

const headerStyle = {
  textAlign: 'center',
  marginBottom: '4rem',
  padding: '3rem 0',
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  borderRadius: '12px',
  color: 'white',
};

const titleStyle = {
  fontSize: '3rem',
  fontWeight: '700',
  marginBottom: '1rem',
};

const subtitleStyle = {
  fontSize: '1.2rem',
  opacity: 0.9,
  fontWeight: '300',
};

const mainContentStyle = {
  display: 'grid',
  gridTemplateColumns: '1fr 1fr',
  gap: '3rem',
  marginBottom: '4rem',
  '@media (max-width: 768px)': {
    gridTemplateColumns: '1fr',
  }
};

const infoSectionStyle = {
  backgroundColor: 'white',
  padding: '2.5rem',
  borderRadius: '12px',
  boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)',
};

const formSectionStyle = {
  backgroundColor: 'white',
  padding: '2.5rem',
  borderRadius: '12px',
  boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)',
};

const sectionTitleStyle = {
  fontSize: '1.8rem',
  fontWeight: '600',
  color: '#2d3748',
  marginBottom: '2rem',
  borderBottom: '3px solid #ff6a00',
  paddingBottom: '0.5rem',
  display: 'inline-block',
};

const contactListStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '2rem',
  marginBottom: '3rem',
};

const contactItemStyle = {
  display: 'flex',
  alignItems: 'flex-start',
  gap: '1rem',
  padding: '1.5rem',
  backgroundColor: '#f7fafc',
  borderRadius: '8px',
  transition: 'transform 0.3s ease',
  ':hover': {
    transform: 'translateY(-2px)',
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
  }
};

const contactIconStyle = {
  fontSize: '2rem',
  flexShrink: 0,
  marginTop: '0.25rem',
};

const contactInfoStyle = {
  flex: 1,
};

const contactTitleStyle = {
  fontSize: '1.1rem',
  fontWeight: '600',
  color: '#2d3748',
  marginBottom: '0.5rem',
};

const contactDetailStyle = {
  color: '#718096',
  fontSize: '1rem',
  marginBottom: '0.25rem',
  lineHeight: '1.4',
};

const socialSectionStyle = {
  borderTop: '1px solid #e2e8f0',
  paddingTop: '2rem',
};

const socialTitleStyle = {
  fontSize: '1.2rem',
  fontWeight: '600',
  color: '#2d3748',
  marginBottom: '1rem',
};

const socialLinksStyle = {
  display: 'flex',
  gap: '1rem',
  flexWrap: 'wrap',
};

const socialLinkStyle = {
  backgroundColor: '#ff6a00',
  color: 'white',
  padding: '0.5rem 1rem',
  borderRadius: '20px',
  textDecoration: 'none',
  fontSize: '0.9rem',
  fontWeight: '500',
  transition: 'all 0.3s ease',
  ':hover': {
    backgroundColor: '#e55a00',
    transform: 'translateY(-2px)',
  }
};

const formStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '1.5rem',
};

const formRowStyle = {
  display: 'grid',
  gridTemplateColumns: '1fr 1fr',
  gap: '1rem',
  '@media (max-width: 480px)': {
    gridTemplateColumns: '1fr',
  }
};

const inputGroupStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '0.5rem',
};

const labelStyle = {
  fontWeight: '600',
  color: '#2d3748',
  fontSize: '0.9rem',
};

const inputStyle = {
  padding: '0.75rem',
  border: '1px solid #ddd',
  borderRadius: '6px',
  fontSize: '1rem',
  transition: 'border-color 0.3s',
  outline: 'none',
  ':focus': {
    borderColor: '#ff6a00',
    boxShadow: '0 0 0 3px rgba(255, 106, 0, 0.1)',
  }
};

const selectStyle = {
  padding: '0.75rem',
  border: '1px solid #ddd',
  borderRadius: '6px',
  fontSize: '1rem',
  backgroundColor: 'white',
  cursor: 'pointer',
  outline: 'none',
  ':focus': {
    borderColor: '#ff6a00',
    boxShadow: '0 0 0 3px rgba(255, 106, 0, 0.1)',
  }
};

const textareaStyle = {
  padding: '0.75rem',
  border: '1px solid #ddd',
  borderRadius: '6px',
  fontSize: '1rem',
  resize: 'vertical',
  minHeight: '120px',
  outline: 'none',
  fontFamily: 'inherit',
  ':focus': {
    borderColor: '#ff6a00',
    boxShadow: '0 0 0 3px rgba(255, 106, 0, 0.1)',
  }
};

const submitButtonStyle = {
  backgroundColor: '#ff6a00',
  color: 'white',
  border: 'none',
  padding: '1rem 2rem',
  fontSize: '1.1rem',
  fontWeight: '600',
  borderRadius: '6px',
  cursor: 'pointer',
  transition: 'all 0.3s ease',
  marginTop: '1rem',
  ':hover': {
    backgroundColor: '#e55a00',
    transform: 'translateY(-2px)',
    boxShadow: '0 4px 12px rgba(255, 106, 0, 0.3)',
  }
};

const mapSectionStyle = {
  backgroundColor: 'white',
  padding: '2.5rem',
  borderRadius: '12px',
  boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)',
};

const mapPlaceholderStyle = {
  backgroundColor: '#f7fafc',
  borderRadius: '8px',
  height: '300px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  border: '2px dashed #cbd5e0',
};

const mapContentStyle = {
  textAlign: 'center',
  color: '#718096',
};

const mapIconStyle = {
  fontSize: '4rem',
  marginBottom: '1rem',
};

const mapTitleStyle = {
  fontSize: '1.5rem',
  fontWeight: '600',
  marginBottom: '0.5rem',
  color: '#4a5568',
};

const mapDescStyle = {
  fontSize: '1rem',
  marginBottom: '0.5rem',
};

const mapTipStyle = {
  fontSize: '0.9rem',
  fontStyle: 'italic',
};

export default ContactUs;