import React from 'react';

const TermsOfService = () => {
  return (
    <div style={containerStyle}>
      <div style={contentStyle}>
        {/* 页面头部 */}
        <div style={headerStyle}>
          <h1 style={titleStyle}>服务条款</h1>
          <p style={subtitleStyle}>最后更新日期：2025年11月2日</p>
        </div>

        {/* 服务条款内容 */}
        <div style={termsContentStyle}>
          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>接受条款</h2>
            <p style={paragraphStyle}>
              欢迎使用我们的服务。通过访问或使用我们的网站、应用程序、软件、产品和服务（统称为"服务"），
              您同意遵守本服务条款（"条款"）以及我们可能不时发布的任何附加条款和条件。
            </p>
            <p style={paragraphStyle}>
              如果您不同意这些条款，请勿使用我们的服务。我们保留随时修改这些条款的权利，
              修改后的条款将在网站上公布后立即生效。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>服务描述</h2>
            <p style={paragraphStyle}>
              我们提供包括但不限于以下服务：[具体服务描述，如：在线平台、软件工具、数字内容等]。
              我们保留随时修改、暂停或终止任何服务的权利，恕不另行通知。
            </p>
            <p style={paragraphStyle}>
              您理解并同意，我们提供的服务可能包含广告或其他商业内容，这些内容是我们服务的重要组成部分。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>用户账户</h2>
            <h3 style={subsectionTitleStyle}>账户注册</h3>
            <ul style={listStyle}>
              <li style={listItemStyle}>
                您可能需要注册账户才能使用某些服务功能
              </li>
              <li style={listItemStyle}>
                您必须提供真实、准确、完整和最新的注册信息
              </li>
          <li style={listItemStyle}>
                您必须年满18周岁或达到您所在司法管辖区的法定成年年龄
              </li>
            </ul>

            <h3 style={subsectionTitleStyle}>账户安全</h3>
            <ul style={listStyle}>
              <li style={listItemStyle}>
                您有责任维护账户信息的机密性
              </li>
              <li style={listItemStyle}>
                您对在您账户下发生的所有活动承担全部责任
              </li>
              <li style={listItemStyle}>
                如发现任何未经授权的账户使用，请立即通知我们
              </li>
            </ul>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>用户义务</h2>
            <p style={paragraphStyle}>在使用我们的服务时，您同意不会：</p>
            <ul style={listStyle}>
              <li style={listItemStyle}>违反任何适用的法律、法规或规章</li>
              <li style={listItemStyle}>侵犯他人的知识产权、隐私权或其他权利</li>
              <li style={listItemStyle}>发布、传输任何非法、有害、威胁、辱骂、骚扰、诽谤的内容</li>
              <li style={listItemStyle}>传播病毒、恶意代码或其他有害技术</li>
              <li style={listItemStyle}>干扰或破坏服务的正常运行</li>
              <li style={listItemStyle}>未经授权访问其他用户的账户</li>
              <li style={listItemStyle}>进行任何欺诈性活动</li>
            </ul>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>知识产权</h2>
            <p style={paragraphStyle}>
              我们服务中的所有内容，包括但不限于文本、图形、徽标、按钮图标、图像、音频剪辑、数字下载、
              数据编译和软件，均归我们或我们的内容提供商所有，并受国际版权法的保护。
            </p>
            <p style={paragraphStyle}>
              未经我们明确书面许可，您不得复制、修改、分发、传输、展示、执行、复制、发布、许可、
              创建衍生作品或转让任何通过服务获得的信息、软件、产品或服务。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>用户内容</h2>
            <p style={paragraphStyle}>
              您保留向服务提交、发布或显示的任何内容的所有权。但是，通过提交、发布或显示内容，
              您授予我们全球范围内、免版税、非独占的许可，以使用、复制、修改、改编、发布、
              翻译和分发此类内容。
            </p>
            <p style={paragraphStyle}>
              您声明并保证您拥有或获得了提交内容的所有必要权利，且内容不侵犯任何第三方的权利。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>服务变更和终止</h2>
            <p style={paragraphStyle}>
              我们保留随时修改或终止服务（或其任何部分）的权利，恕不另行通知。
              对于服务的任何修改、价格变更、暂停或终止，我们不对您或任何第三方承担责任。
            </p>
            <p style={paragraphStyle}>
              如果您违反这些条款，我们可能暂停或终止您的账户，恕不另行通知。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>免责声明</h2>
            <p style={paragraphStyle}>
              服务按"原样"和"可用"的基础提供。在适用法律允许的最大范围内，
              我们明确否认所有明示或暗示的保证，包括但不限于对适销性、特定用途适用性、
              所有权和不侵权的暗示保证。
            </p>
            <p style={paragraphStyle}>
              我们不保证服务将不间断、及时、安全或无错误。您从服务或通过服务获得的
              任何建议或信息，无论是口头还是书面，都不构成未在本条款中明确规定的任何保证。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>责任限制</h2>
            <p style={paragraphStyle}>
              在适用法律允许的最大范围内，我们不对任何间接、偶然、特殊、后果性或惩罚性损害承担责任，
              包括但不限于利润损失、数据损失或其他无形损失，无论我们是否已被告知此类损害的可能性。
            </p>
            <p style={paragraphStyle}>
              我们的总责任，对于因使用服务而引起的或与之相关的任何索赔，不得超过您在过去12个月内
              为使用服务而向我们支付的金额（如有）。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>赔偿</h2>
            <p style={paragraphStyle}>
              您同意赔偿、辩护并使我们免受因以下原因引起的或与之相关的任何及所有索赔、损害、义务、
              损失、责任、成本或债务（包括合理的律师费）：
            </p>
            <ul style={listStyle}>
              <li style={listItemStyle}>您使用或滥用服务</li>
              <li style={listItemStyle}>您违反这些条款</li>
              <li style={listItemStyle}>您侵犯任何第三方的权利</li>
              <li style={listItemStyle}>您提交的任何内容</li>
            </ul>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>第三方链接</h2>
            <p style={paragraphStyle}>
              服务可能包含指向第三方网站或资源的链接。您承认并同意，我们不对这些外部网站或资源的
              内容、产品、服务或可用性负责，也不认可它们。
            </p>
            <p style={paragraphStyle}>
              您承认并同意，对于因使用或依赖任何此类内容、商品或服务而引起的或与之相关的任何损害或损失，
              我们不承担任何责任。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>管辖法律</h2>
            <p style={paragraphStyle}>
              这些条款应受中华人民共和国法律管辖并据其解释，不考虑法律冲突原则。
            </p>
            <p style={paragraphStyle}>
              因这些条款引起的或与之相关的任何争议应提交有管辖权的中华人民共和国法院解决。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>一般条款</h2>
            <h3 style={subsectionTitleStyle}>可分割性</h3>
            <p style={paragraphStyle}>
              如果这些条款的任何条款被认定为无效或不可执行，其余条款仍保持完全有效。
            </p>

            <h3 style={subsectionTitleStyle}>弃权</h3>
            <p style={paragraphStyle}>
              我们未能执行这些条款的任何条款不应被解释为放弃该条款或任何其他条款。
            </p>

            <h3 style={subsectionTitleStyle}>完整协议</h3>
            <p style={paragraphStyle}>
              这些条款构成您与我们之间关于服务使用的完整协议，并取代所有先前或同期的通信和提案，
              无论是口头还是书面。
            </p>
          </section>

          <section style={sectionStyle}>
            <h2 style={sectionTitleStyle}>联系我们</h2>
            <p style={paragraphStyle}>
              如果您对这些服务条款有任何疑问，请通过以下方式与我们联系：
            </p>
            <div style={contactInfoStyle}>
              <p style={contactItemStyle}>
                <strong>邮箱：</strong>legal@example.com
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
              请仔细阅读这些服务条款。通过使用我们的服务，您同意遵守这些条款和条件。
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

// 样式定义 - 与隐私政策页面保持一致
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

const termsContentStyle = {
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

export default TermsOfService;