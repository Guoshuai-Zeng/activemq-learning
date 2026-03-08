# ActiveMQ 安装与配置

## 系统要求

- Java JDK 8 或更高版本
- 至少 512MB 可用内存
- 100MB 可用磁盘空间

## 安装方式

### 方式一：使用二进制包安装（推荐）

#### 1. 下载 ActiveMQ

访问 ActiveMQ 官方下载页面：
http://activemq.apache.org/download-archives

下载最新稳定版本（推荐 5.18.x）

```bash
# 使用 wget 下载
wget https://archive.apache.org/dist/activemq/5.18.3/apache-activemq-5.18.3-bin.tar.gz

# 或使用 curl 下载
curl -O https://archive.apache.org/dist/activemq/5.18.3/apache-activemq-5.18.3-bin.tar.gz
```

#### 2. 解压安装

```bash
# 解压到 /opt 目录（Linux）
sudo tar -xzf apache-activemq-5.18.3-bin.tar.gz -C /opt

# 或解压到 /usr/local 目录（macOS/Linux）
sudo tar -xzf apache-activemq-5.18.3-bin.tar.gz -C /usr/local

# 创建符号链接（可选）
sudo ln -s /usr/local/apache-activemq-5.18.3 /usr/local/activemq
```

#### 3. 配置环境变量

```bash
# 编辑 ~/.bashrc 或 ~/.zshrc
export ACTIVEMQ_HOME=/usr/local/activemq
export PATH=$PATH:$ACTIVEMQ_HOME/bin

# 重新加载配置
source ~/.bashrc  # 或 source ~/.zshrc
```

### 方式二：使用 Docker 安装（推荐开发环境）

```bash
# 拉取官方镜像
docker pull apache/activemq:5.18.3

# 运行容器
docker run -d \
  --name activemq \
  -p 61616:61616 \      # JMS 端口
  -p 8161:8161 \        # Web 管理控制台端口
  -e ACTIVEMQ_ADMIN_LOGIN=admin \
  -e ACTIVEMQ_ADMIN_PASSWORD=admin123 \
  apache/activemq:5.18.3
```

### 方式三：使用 Homebrew 安装（macOS）

```bash
# 安装
brew install activemq

# 启动服务
brew services start activemq

# 停止服务
brew services stop activemq
```

## 启动 ActiveMQ

### 命令行启动

```bash
# 进入 ActiveMQ 安装目录
cd /usr/local/activemq

# 启动（前台运行）
./bin/activemq start

# 启动（后台运行）
./bin/activemq start

# 查看状态
./bin/activemq status

# 停止
./bin/activemq stop

# 重启
./bin/activemq restart
```

### Windows 系统启动

```cmd
# 进入 ActiveMQ 安装目录
cd C:\apache-activemq-5.18.3\bin

# 启动
activemq.bat start
```

## 验证安装

### 1. 检查进程

```bash
# Linux/Mac
ps aux | grep activemq

# 或使用 jps
jps -l | grep activemq
```

### 2. 检查端口

```bash
# 检查 JMS 端口 (61616)
netstat -an | grep 61616

# 检查 Web 管理控制台端口 (8161)
netstat -an | grep 8161
```

### 3. 访问 Web 管理控制台

打开浏览器访问：`http://localhost:8161`

默认登录凭证：
- 用户名：`admin`
- 密码：`admin`

控制台主要功能：
- **Queues** - 查看和管理消息队列
- **Topics** - 查看和管理主题订阅
- **Subscribers** - 查看订阅者状态
- **Connections** - 查看客户端连接
- **Network** - 查看网络拓扑

## 配置文件

### activemq.xml

位置：`$ACTIVEMQ_HOME/conf/activemq.xml`

```xml
<beans xmlns="http://www.springframework.org/schema/beans">
  <broker brokerName="localhost" dataDirectory="${activemq.data}">

    <!-- 访问控制 -->
    <managementContext>
      <managementContext createConnector="false"/>
    </managementContext>

    <!-- 网络连接器 -->
    <networkConnectors>
    </networkConnectors>

    <!-- 持久化适配器 -->
    <persistenceAdapter>
      <kahaDB directory="${activemq.data}/kahadb"/>
    </persistenceAdapter>

    <!-- 传输连接器 -->
    <transportConnectors>
      <transportConnector name="openwire" uri="tcp://0.0.0.0:61616"/>
      <transportConnector name="amqp" uri="amqp://0.0.0.0:5672"/>
      <transportConnector name="stomp" uri="stomp://0.0.0.0:61613"/>
      <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883"/>
    </transportConnectors>

  </broker>
</beans>
```

### jetty.xml

位置：`$ACTIVEMQ_HOME/conf/jetty.xml`

配置 Web 管理控制台端口和访问控制。

### login.config

位置：`$ACTIVEMQ_HOME/conf/login.config`

配置用户认证信息。

## 常见问题

### 1. 端口被占用

```bash
# 查找占用 61616 端口的进程
lsof -i :61616

# 修改端口配置
# 编辑 activemq.xml 中的 transportConnector uri
```

### 2. 权限不足

```bash
# 添加执行权限
chmod +x $ACTIVEMQ_HOME/bin/activemq
```

### 3. Java 版本不兼容

```bash
# 检查 Java 版本
java -version

# ActiveMQ 5.18.x 需要 Java 8+
```

### 4. 内存不足

```bash
# 修改 bin/env 脚本中的 JVM 内存参数
ACTIVEMQ_OPTS_MEMORY="-Xms512M -Xmx512M"
```

## 生产环境建议

1. **启用持久化** - 使用 KahaDB 或 JDBC 持久化
2. **配置集群** - 使用主从复制提高可用性
3. **监控告警** - 配置 JMX 监控
4. **安全加固** - 配置 SSL/TLS 认证
5. **日志管理** - 配置日志轮转

## 下一步

- [了解核心概念](./03-concepts.md)
- [快速开始示例](./04-quick-start.md)
