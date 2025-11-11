import React from 'react';
import { Link } from 'react-router-dom';

const AboutUs = () => {
  return (
    <div style={containerStyle}>
      <div style={contentStyle}>
        {/* 页面头部 */}
        <div style={headerStyle}>
          <h1 style={titleStyle}>关于我们</h1>
          <p style={subtitleStyle}>连接世界，创造价值</p>
        </div>

        {/* 公司简介 */}
        <section style={sectionStyle}>
          <div style={sectionHeaderStyle}>
            <h2 style={sectionTitleStyle}>公司简介</h2>
            <div style={dividerStyle}></div>
          </div>
          <div style={textContentStyle}>
            <p style={paragraphStyle}>
              我们是一家专注水果蔬菜线上销售领域的创新科技公司，致力于为用户提供优质的商品和便捷的购物体验。
              自成立以来，我们始终坚持以用户为中心，通过技术创新和服务升级，不断推动行业发展。
            </p>
            <p style={paragraphStyle}>
              我们的团队由来自知名互联网公司的资深专家组成，拥有丰富的技术研发和运营管理经验。
              我们相信，通过科技的力量可以让生活变得更美好。
            </p>
          </div>
        </section>

        {/* 我们的使命 */}
        <section style={sectionStyle}>
          <div style={sectionHeaderStyle}>
            <h2 style={sectionTitleStyle}>我们的使命</h2>
            <div style={dividerStyle}></div>
          </div>
          <div style={missionGridStyle}>
            <div style={missionCardStyle}>
              <div style={missionIconStyle}>🚀</div>
              <h3 style={missionTitleStyle}>技术创新</h3>
              <p style={missionDescStyle}>
                通过持续的技术创新，为用户提供更智能、更便捷的购物体验
              </p>
            </div>
            <div style={missionCardStyle}>
              <div style={missionIconStyle}>❤️</div>
              <h3 style={missionTitleStyle}>用户至上</h3>
              <p style={missionDescStyle}>
                始终将用户需求放在首位，用心服务每一位用户
              </p>
            </div>
            <div style={missionCardStyle}>
              <div style={missionIconStyle}>🌍</div>
              <h3 style={missionTitleStyle}>社会责任</h3>
              <p style={missionDescStyle}>
                积极履行社会责任，推动行业健康发展
              </p>
            </div>
          </div>
        </section>

        {/* 核心价值 */}
        <section style={sectionStyle}>
          <div style={sectionHeaderStyle}>
            <h2 style={sectionTitleStyle}>核心价值</h2>
            <div style={dividerStyle}></div>
          </div>
          <div style={valuesListStyle}>
            <div style={valueItemStyle}>
              <span style={valueDotStyle}></span>
              <span style={valueTextStyle}>诚信经营 - 我们坚持诚实守信，为用户提供真实可靠的服务</span>
            </div>
            <div style={valueItemStyle}>
              <span style={valueDotStyle}></span>
              <span style={valueTextStyle}>追求卓越 - 我们不断追求更高的标准，力求做到最好</span>
            </div>
            <div style={valueItemStyle}>
              <span style={valueDotStyle}></span>
              <span style={valueTextStyle}>开放合作 - 我们拥抱变化，乐于与各方合作伙伴共同成长</span>
            </div>
            <div style={valueItemStyle}>
              <span style={valueDotStyle}></span>
              <span style={valueTextStyle}>创新驱动 - 我们鼓励创新思维，用技术创造更多可能</span>
            </div>
          </div>
        </section>

        {/* 发展历程 */}
        <section style={sectionStyle}>
          <div style={sectionHeaderStyle}>
            <h2 style={sectionTitleStyle}>发展历程</h2>
            <div style={dividerStyle}></div>
          </div>
          <div style={timelineStyle}>
            <div style={timelineItemStyle}>
              <div style={timelineYearStyle}>2023</div>
              <div style={timelineContentStyle}>
                <h4 style={timelineTitleStyle}>公司成立</h4>
                <p style={timelineDescStyle}>正式成立，开启电商新征程</p>
              </div>
            </div>
            <div style={timelineItemStyle}>
              <div style={timelineYearStyle}>2024</div>
              <div style={timelineContentStyle}>
                <h4 style={timelineTitleStyle}>产品上线</h4>
                <p style={timelineDescStyle}>首个版本正式上线，获得用户认可</p>
              </div>
            </div>
            <div style={timelineItemStyle}>
              <div style={timelineYearStyle}>2025</div>
              <div style={timelineContentStyle}>
                <h4 style={timelineTitleStyle}>快速发展</h4>
                <p style={timelineDescStyle}>用户规模突破百万，服务持续升级</p>
              </div>
            </div>
          </div>
        </section>

        {/* 加入我们 */}
        <section style={joinSectionStyle}>
          <div style={joinContentStyle}>
            <h2 style={joinTitleStyle}>加入我们</h2>
            <p style={joinDescStyle}>
              我们正在寻找有激情、有才华的伙伴加入我们的团队，共同创造更美好的未来。
            </p>
            <div style={buttonGroupStyle}>
              <button style={joinButtonStyle}>
                查看职位机会
              </button>
              <Link to="/contactus" style={contactButtonStyle}>
                联系我们
              </Link>
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
  background: 'linear-gradient(45deg, #fff, #f0f0f0)',
  WebkitBackgroundClip: 'text',
  WebkitTextFillColor: 'transparent',
};

const subtitleStyle = {
  fontSize: '1.2rem',
  opacity: 0.9,
  fontWeight: '300',
};

const sectionStyle = {
  marginBottom: '4rem',
  backgroundColor: 'white',
  padding: '2.5rem',
  borderRadius: '12px',
  boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)',
};

const sectionHeaderStyle = {
  marginBottom: '2rem',
  textAlign: 'center',
};

const sectionTitleStyle = {
  fontSize: '2rem',
  fontWeight: '600',
  color: '#2d3748',
  marginBottom: '1rem',
};

const dividerStyle = {
  width: '60px',
  height: '4px',
  backgroundColor: '#ff6a00',
  margin: '0 auto',
  borderRadius: '2px',
};

const textContentStyle = {
  lineHeight: '1.8',
  color: '#4a5568',
};

const paragraphStyle = {
  marginBottom: '1.5rem',
  fontSize: '1.1rem',
  textAlign: 'justify',
};

const missionGridStyle = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
  gap: '2rem',
  marginTop: '2rem',
};

const missionCardStyle = {
  textAlign: 'center',
  padding: '2rem 1rem',
  backgroundColor: '#f7fafc',
  borderRadius: '8px',
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  cursor: 'pointer',
  ':hover': {
    transform: 'translateY(-5px)',
    boxShadow: '0 10px 25px rgba(0, 0, 0, 0.1)',
  }
};

const missionIconStyle = {
  fontSize: '3rem',
  marginBottom: '1rem',
};

const missionTitleStyle = {
  fontSize: '1.3rem',
  fontWeight: '600',
  color: '#2d3748',
  marginBottom: '1rem',
};

const missionDescStyle = {
  color: '#718096',
  lineHeight: '1.6',
};

const valuesListStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '1.5rem',
  maxWidth: '600px',
  margin: '0 auto',
};

const valueItemStyle = {
  display: 'flex',
  alignItems: 'flex-start',
  gap: '1rem',
  padding: '1rem',
  backgroundColor: '#f7fafc',
  borderRadius: '8px',
};

const valueDotStyle = {
  width: '8px',
  height: '8px',
  backgroundColor: '#ff6a00',
  borderRadius: '50%',
  marginTop: '0.5rem',
  flexShrink: 0,
};

const valueTextStyle = {
  color: '#4a5568',
  fontSize: '1.1rem',
  lineHeight: '1.6',
};

const timelineStyle = {
  maxWidth: '600px',
  margin: '0 auto',
  position: 'relative',
};

const timelineItemStyle = {
  display: 'flex',
  alignItems: 'flex-start',
  gap: '2rem',
  marginBottom: '3rem',
  position: 'relative',
};

const timelineYearStyle = {
  backgroundColor: '#ff6a00',
  color: 'white',
  padding: '0.5rem 1rem',
  borderRadius: '20px',
  fontWeight: '600',
  fontSize: '0.9rem',
  flexShrink: 0,
  minWidth: '80px',
  textAlign: 'center',
};

const timelineContentStyle = {
  flex: 1,
  padding: '1rem',
  backgroundColor: '#f7fafc',
  borderRadius: '8px',
};

const timelineTitleStyle = {
  fontSize: '1.2rem',
  fontWeight: '600',
  color: '#2d3748',
  marginBottom: '0.5rem',
};

const timelineDescStyle = {
  color: '#718096',
  lineHeight: '1.6',
};

const joinSectionStyle = {
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  padding: '4rem 2rem',
  borderRadius: '12px',
  textAlign: 'center',
  color: 'white',
  marginTop: '2rem',
};

const joinContentStyle = {
  maxWidth: '600px',
  margin: '0 auto',
};

const joinTitleStyle = {
  fontSize: '2.5rem',
  fontWeight: '700',
  marginBottom: '1.5rem',
};

const joinDescStyle = {
  fontSize: '1.2rem',
  marginBottom: '2rem',
  opacity: 0.9,
  lineHeight: '1.6',
};

const buttonGroupStyle = {
  display: 'flex',
  gap: '1rem',
  justifyContent: 'center',
  flexWrap: 'wrap',
};

const joinButtonStyle = {
  backgroundColor: 'white',
  color: '#667eea',
  border: 'none',
  padding: '1rem 2rem',
  fontSize: '1.1rem',
  fontWeight: '600',
  borderRadius: '25px',
  cursor: 'pointer',
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  textDecoration: 'none',
  display: 'inline-block',
  ':hover': {
    transform: 'translateY(-2px)',
    boxShadow: '0 8px 25px rgba(255, 255, 255, 0.3)',
  }
};

const contactButtonStyle = {
  backgroundColor: 'transparent',
  color: 'white',
  border: '2px solid white',
  padding: '1rem 2rem',
  fontSize: '1.1rem',
  fontWeight: '600',
  borderRadius: '25px',
  cursor: 'pointer',
  transition: 'all 0.3s ease',
  textDecoration: 'none',
  display: 'inline-block',
  ':hover': {
    backgroundColor: 'white',
    color: '#667eea',
    transform: 'translateY(-2px)',
  }
};

export default AboutUs;