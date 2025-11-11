
sudo apt update
sudo apt install wget unzip

# 下载 Gradle 8.5
wget https://services.gradle.org/distributions/gradle-8.5-bin.zip

# 创建安装目录
sudo mkdir /opt/gradle
sudo unzip -d /opt/gradle gradle-8.5-bin.zip

# ln -s /opt/gradle/gradle-8.5/bin/gradle /usr/local/bin/gradle
# gradle -version

------------------------------------------------------------
Gradle 8.5
------------------------------------------------------------

Build time:   2023-11-29 14:08:57 UTC
Revision:     28aca86a7180baa17117e0e5ba01d8ea9feca598

Kotlin:       1.9.20
Groovy:       3.0.17
Ant:          Apache Ant(TM) version 1.10.13 compiled on January 4 2023
JVM:          17.0.16 (Eclipse Adoptium 17.0.16+8)
OS:           Linux 5.10.244-240.965.amzn2.x86_64 amd64

# 

