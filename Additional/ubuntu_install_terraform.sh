# 获取最新版本号并下载
TERRAFORM_VERSION=$(curl -s https://api.github.com/repos/hashicorp/terraform/releases/latest | grep 'tag_name' | cut -d\" -f4 | sed 's/v//')

# 下载Terraform
wget https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip

# 安装unzip工具（如果未安装）
sudo apt update
sudo apt install unzip

# 解压到系统目录
sudo unzip terraform_${TERRAFORM_VERSION}_linux_amd64.zip -d /usr/local/bin/

# 验证安装
terraform version
