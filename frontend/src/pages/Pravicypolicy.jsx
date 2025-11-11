import React from 'react';

const PrivacyPolicy = () => {
  return (
    <div style={containerStyle}>
      <div style={contentStyle}>
        {/* 页面头部 */}
        <div style={headerStyle}>
          <h1 style={titleStyle}>隐私政策</h1>
          <p style={subtitleStyle}>最后更新日期：2025年11月2日</p>
        </div>

        {/* 隐私政策内容 */}
        <div style={policyContentStyle}>
          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>引言</h2>
            <p style={paragraphStyle}>
              欢迎使用我们的服务。我们深知个人信息对您的重要性，并致力于保护您的隐私。
              本隐私政策旨在说明我们如何收集、使用、存储和保护您的个人信息。
            </p>
            <p style={paragraphStyle}>
              请您在使用我们的服务前，仔细阅读并理解本隐私政策。一旦您开始使用我们的服务，
              即表示您同意我们按照本政策的规定处理您的个人信息。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>信息收集</h2>
            <h3 style={subsectionTitleStyle}>我们收集的信息类型</h3>
            <ul style={listStyle}>
              <li style={listItemStyle}>
                <strong>个人基本信息：</strong>包括您的姓名、电话号码、电子邮箱地址等
              </li>
              <li style={listItemStyle}>
                <strong>账户信息：</strong>包括用户名、密码、账户设置等
              </li>
              <li style={listItemStyle}>
                <strong>交易信息：</strong>包括订单信息、支付信息、收货地址等
              </li>
              <li style={listItemStyle}>
                <strong>设备信息：</strong>包括设备型号、操作系统、IP地址、浏览器类型等
              </li>
              <li style={listItemStyle}>
                <strong>使用信息：</strong>包括浏览记录、搜索记录、点击记录等
              </li>
            </ul>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>信息使用</h2>
            <p style={paragraphStyle}>我们可能将收集的信息用于以下目的：</p>
            <ul style={listStyle}>
              <li style={listItemStyle}>提供、维护和改进我们的服务</li>
              <li style={listItemStyle}>处理您的交易和订单</li>
              <li style={listItemStyle}>发送重要的服务通知</li>
              <li style={listItemStyle}>个性化您的用户体验</li>
              <li style={listItemStyle}>进行数据分析和市场研究</li>
              <li style={listItemStyle}>确保服务的安全性和完整性</li>
              <li style={listItemStyle}>遵守法律法规要求</li>
            </ul>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>信息共享</h2>
            <p style={paragraphStyle}>
              我们不会将您的个人信息出售给第三方。但在以下情况下，我们可能会共享您的信息：
            </p>
            <ul style={listStyle}>
              <li style={listItemStyle}>
                <strong>服务提供商：</strong>与为我们提供服务的第三方共享必要信息
              </li>
              <li style={listItemStyle}>
                <strong>法律要求：</strong>为遵守法律法规、法院命令或政府要求
              </li>
              <li style={listItemStyle}>
                <strong>保护权利：</strong>为保护我们、用户或公众的权利、财产和安全
              </li>
              <li style={listItemStyle}>
                <strong>商业交易：</strong>在公司合并、收购或资产出售等情况下
              </li>
            </ul>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>Cookie和类似技术</h2>
            <p style={paragraphStyle}>
              我们使用Cookie和类似技术来收集信息、记住您的偏好、提供个性化体验以及分析服务使用情况。
              您可以通过浏览器设置拒绝或管理Cookie，但这可能会影响某些服务功能的正常使用。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>数据安全</h2>
            <p style={paragraphStyle}>
              我们采取合理的安全措施来保护您的个人信息，防止未经授权的访问、使用或披露。
              这些措施包括物理安全措施、电子安全措施和管理安全措施。
            </p>
            <p style={paragraphStyle}>
              尽管我们尽力保护您的个人信息，但没有任何安全措施是完美无缺的，
              我们无法保证信息的绝对安全。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>数据保留</h2>
            <p style={paragraphStyle}>
              我们仅在实现本政策所述目的所需的期限内保留您的个人信息，
              除非法律要求或允许更长的保留期限。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>您的权利</h2>
            <p style={paragraphStyle}>根据适用法律，您可能拥有以下权利：</p>
            <ul style={listStyle}>
              <li style={listItemStyle}>访问和获取您的个人信息的副本</li>
              <li style={listItemStyle}>更正不准确的个人信息</li>
              <li style={listItemStyle}>删除您的个人信息</li>
              <li style={listItemStyle}>限制或反对我们处理您的个人信息</li>
              <li style={listItemStyle}>数据可移植性</li>
              <li style={listItemStyle}>撤回同意</li>
            </ul>
            <p style={paragraphStyle}>
              如要行使上述权利，请通过本政策末尾提供的联系方式与我们联系。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>未成年人隐私</h2>
            <p style={paragraphStyle}>
              我们的服务不面向未成年人。我们不会故意收集未成年人的个人信息。
              如果您是未成年人的父母或监护人，并认为我们可能收集了未成年人的信息，
              请立即与我们联系。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>国际数据传输</h2>
            <p style={paragraphStyle}>
              您的个人信息可能被传输到您所在国家/地区之外的服务器上进行处理。
              我们会确保这些传输符合适用的数据保护法律。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>政策更新</h2>
            <p style={paragraphStyle}>
              我们可能会不时更新本隐私政策。更新后的政策将在网站上发布，
              并更新"最后更新日期"。我们建议您定期查看本政策以了解任何变更。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>联系我们</h2>
            <p style={paragraphStyle}>
              如果您对本隐私政策有任何疑问、意见或建议，请通过以下方式与我们联系：
            </p>
            <div style={contactInfoStyle}>
              <p style={contactItemStyle}>
                <strong>邮箱：</strong>privacy@example.com
              </p>
              <p style={contactItemStyle}>
                <strong>电话：</strong>400-123-4567
              </p>
              <p style={contactItemStyle}>
                <strong>地址：</strong>北京市朝阳区科技园区创新大厦A座
              </p>
            </div>
          </section>

          <div style={footerNoteStyle}>
            <p>
              感谢您花时间阅读我们的隐私政策。我们承诺保护您的隐私并提供安全可靠的服务。
            </p>
          </div>
        </div>
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
  maxWidth: '900px',
  margin: '0 auto',
  padding: '2rem 1rem',
};

const headerStyle = {
  textAlign: 'center',
  marginBottom: '3rem',
  padding: '3rem 0',
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  borderRadius: '12px',
  color: 'white',
};

const titleStyle = {
  fontSize: '2.5rem',
  fontWeight: '700',
  marginBottom: '1rem',
};

const subtitleStyle = {
  fontSize: '1.1rem',
  opacity: 0.9,
  fontWeight: '300',
};

const policyContentStyle = {
  backgroundColor: 'white',
  padding: '3rem',
  borderRadius: '12px',
  boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)',
  lineHeight: '1.8',
};

const sectionStyle = {
  marginBottom: '3rem',
  paddingBottom: '2rem',
  borderBottom: '1px solid #e2e8f0',
  ':last-child': {
    borderBottom: 'none',
    marginBottom: 0,
    paddingBottom: 0,
  }
};

const sectionTitleStyle = {
  fontSize: '1.5rem',
  fontWeight: '600',
  color: '#2d3748',
  marginBottom: '1.5rem',
  paddingBottom: '0.5rem',
  borderBottom: '2px solid #ff6a00',
  display: 'inline-block',
};

const subsectionTitleStyle = {
  fontSize: '1.2rem',
  fontWeight: '600',
  color: '#4a5568',
  marginBottom: '1rem',
  marginTop: '1.5rem',
};

const paragraphStyle = {
  marginBottom: '1.5rem',
  color: '#4a5568',
  fontSize: '1rem',
  textAlign: 'justify',
  lineHeight: '1.8',
};

const listStyle = {
  margin: '1rem 0',
  paddingLeft: '1.5rem',
  color: '#4a5568',
};

const listItemStyle = {
  marginBottom: '0.75rem',
  lineHeight: '1.6',
  paddingLeft: '0.5rem',
};

const contactInfoStyle = {
  backgroundColor: '#f7fafc',
  padding: '1.5rem',
  borderRadius: '8px',
  marginTop: '1rem',
};

const contactItemStyle = {
  marginBottom: '0.75rem',
  color: '#4a5568',
  fontSize: '1rem',
  lineHeight: '1.6',
};

const footerNoteStyle = {
  marginTop: '3rem',
  padding: '2rem',
  backgroundColor: '#fff8f0',
  border: '1px solid #ffddcc',
  borderRadius: '8px',
  textAlign: 'center',
  color: '#c05621',
  fontSize: '1rem',
  lineHeight: '1.6',
};

export default PrivacyPolicy;