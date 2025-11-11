# 下载Node.js 18二进制文件
cd /tmp
wget https://nodejs.org/dist/v18.20.4/node-v18.20.4-linux-x64.tar.xz

# 解压文件
tar -xf node-v18.20.4-linux-x64.tar.xz

# 移动到系统目录
sudo mv node-v18.20.4-linux-x64 /usr/local/node

# 创建符号链接
sudo ln -sf /usr/local/node/bin/node /usr/local/bin/node
sudo ln -sf /usr/local/node/bin/npm /usr/local/bin/npm
sudo ln -sf /usr/local/node/bin/npx /usr/local/bin/npx

# 验证安装
node --version
npm --version
