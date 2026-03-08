# ActiveMQ SpringBoot Demo

## 概述

本目录包含使用 SpringBoot 整合 ActiveMQ 的示例代码，提供了 REST API 和 Web 界面。

## 项目结构

```
springboot-demo/
├── pom.xml
├── src/main/
│   ├── java/com/example/
│   │   ├── ActiveMqSpringBootApplication.java  # 应用入口
│   │   ├── config/
│   │   │   └── ActiveMQConfig.java            # ActiveMQ 配置
│   │   ├── service/
│   │   │   ├── MessageProducerService.java  # 消息生产者服务
│   │   │   └── SerializableObject.java        # 可序列化对象
│   │   ├── listener/
│   │   │   ├── QueueMessageListener.java     # 队列监听器
│   │   │   └── TopicMessageListener.java     # 主题监听器
│   │   └── controller/
│   │       ├── MessagingController.java      # 消息 REST API
│   │       └── WebController.java            # Web 页面控制器
│   └── resources/
│       ├── application.yml                    # YAML 配置
│       └── application.properties             # Properties 配置
```

## 快速开始

### 1. 启动 ActiveMQ

```bash
# Docker 方式
docker start activemq

# 或本地方式
activemq start
```

### 2. 编译项目

```bash
cd springboot-demo
mvn clean compile
```

### 3. 运行应用

```bash
mvn spring-boot:run
```

应用启动后，访问：
- 首页: http://localhost:8080
- 健康检查: http://localhost:8080/api/messaging/health
- 统计信息: http://localhost:8080/api/messaging/stats

## REST API

### 队列消息

#### 发送队列消息

```bash
curl -X POST http://localhost:8080/api/messaging/queue/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello SpringBoot ActiveMQ"}'
```

#### 发送带属性的消息

```bash
curl -X POST http://localhost:8080/api/messaging/queue/send-with-props \
  -H "Content-Type: application/json" \
  -d '{
    "content": "订单消息",
    "properties": {
      "userId": 123,
      "userName": "张三",
      "timestamp": 1234567890
    }
  }'
```

#### 发送对象消息

```bash
curl -X POST http://localhost:8080/api/messaging/queue/send-object \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "测试对象",
    "description": "这是一个测试对象"
  }'
```

#### 批量发送消息

```bash
curl -X POST http://localhost:8080/api/messaging/queue/batch?count=10
```

### 主题消息

#### 发布主题消息

```bash
curl -X POST http://localhost:8080/api/messaging/topic/publish \
  -H "Content-Type: application/json" \
  -d '{
    "message": "系统通知",
    "type": "SYSTEM"
  }'
```

#### 批量发布消息

```bash
curl -X POST http://localhost:8080/api/messaging/topic/batch?count=5
```

### 其他接口

#### 发送并接收

```bash
curl -X POST http://localhost:8080/api/messaging/queue/send-receive \
  -H "Content-Type: application/json" \
  -d '{"message": "测试消息"}'
```

#### 获取统计信息

```bash
curl http://localhost:8080/api/messaging/stats
```

#### 健康检查

```bash
curl http://localhost:8080/api/messaging/health
```

## 配置说明

### application.yml

```yaml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    pool:
      enabled: true
      max-connections: 10
    packages:
      trust-all: true
  jms:
    pub-sub-domain: false
    listener:
      acknowledge-mode: client
      concurrency: 3-10
```

### ActiveMQConfig.java

自定义配置包括：
- 连接工厂
- 连接池
- JMS 模板
- 监听器容器
- 消息目的地

## 监听器配置

### 队列监听器

```java
@JmsListener(
    destination = "BOOT_QUEUE",
    containerFactory = "queueListenerFactory"
)
public void onMessage(String message) {
    // 处理消息
}
```

### 主题监听器

```java
@JmsListener(
    destination = "BOOT_TOPIC",
    containerFactory = "topicListenerFactory",
    subscription = "default-subscription"
)
public void onTopicMessage(String message) {
    // 处理消息
}
```

### 消息选择器

```java
@JmsListener(
    destination = "BOOT_TOPIC",
    containerFactory = "topicListenerFactory",
    selector = "type = 'SYSTEM'",
    subscription = "system-subscription"
)
public void onSystemMessage(String message) {
    // 只接收 type = 'SYSTEM' 的消息
}
```

## Web 控制台

ActiveMQ Web 控制台：
- URL: http://localhost:8161
- 用户名: admin
- 密码: admin

在控制台可以查看：
- 队列消息状态
- 主题订阅者
- 连接信息
- 消息浏览和删除

## 常见问题

### 连接失败

检查 ActiveMQ 是否启动：

```bash
docker ps | grep activemq
# 或
netstat -an | grep 61616
```

### 消息未消费

检查监听器配置和目的地名称是否正确。

### 持久订阅不生效

确保：
1. clientId 设置唯一
2. subscriptionName 设置唯一
3. 监听器容器设置 subscriptionDurable = true
