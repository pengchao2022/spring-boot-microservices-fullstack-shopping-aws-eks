class MockSmsService {
  constructor() {
    this.sentCodes = new Map(); // 存储发送的验证码
  }

  // 生成随机6位验证码
  generateCode() {
    return Math.random().toString().slice(2, 8);
  }

  // 模拟发送验证码
  async sendVerificationCode(phoneNumber) {
    try {
      const code = this.generateCode();
      
      // 存储验证码，设置5分钟过期
      this.sentCodes.set(phoneNumber, {
        code: code,
        expires: Date.now() + 5 * 60 * 1000 // 5分钟
      });

      // 开发环境：在控制台显示验证码
      if (process.env.NODE_ENV === 'development') {
        console.log('📱 短信验证码模拟发送 📱');
        console.log('=================================');
        console.log(`接收手机: ${phoneNumber}`);
        console.log(`验证码: ${code}`);
        console.log(`有效期: 5分钟`);
        console.log('=================================');
        console.log('提示: 在登录页面输入以上验证码即可');
      }

      // 模拟网络延迟
      await new Promise(resolve => setTimeout(resolve, 1000));

      return {
        success: true,
        message: '验证码发送成功',
        code: code // 开发环境下返回验证码，便于测试
      };

    } catch (error) {
      console.error('模拟短信发送失败:', error);
      return {
        success: false,
        error: '验证码发送失败，请重试'
      };
    }
  }

  // 验证验证码
  verifyCode(phoneNumber, inputCode) {
    const record = this.sentCodes.get(phoneNumber);
    
    if (!record) {
      return { success: false, error: '验证码已过期，请重新获取' };
    }

    if (Date.now() > record.expires) {
      this.sentCodes.delete(phoneNumber);
      return { success: false, error: '验证码已过期，请重新获取' };
    }

    if (record.code === inputCode) {
      // 验证成功后删除记录
      this.sentCodes.delete(phoneNumber);
      return { success: true, message: '验证成功' };
    } else {
      return { success: false, error: '验证码错误' };
    }
  }

  // 开发环境特殊验证：接受任意6位数字
  devVerifyCode(inputCode) {
    if (process.env.NODE_ENV === 'development') {
      // 检查是否是6位数字
      if (/^\d{6}$/.test(inputCode)) {
        return { success: true, message: '开发模式验证通过' };
      } else {
        return { success: false, error: '验证码必须是6位数字' };
      }
    }
    return { success: false, error: '非开发环境' };
  }
}

// 创建单例实例
export default new MockSmsService();