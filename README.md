# ActiveMQ 学习指南

本项目包含了 ActiveMQ 的完整学习资料，从基础概念到实战代码示例。

## 目录结构

```
activemq-learning/
├── README.md                    # 项目介绍
├── docker-compose.yml           # Docker Compose 配置
├── .gitignore                   # Git 忽略配置
├── docs/                        # 学习文档
│   ├── 01-简介.md      # ActiveMQ 简介
│   ├── 02-安装与配置.md      # 安装与配置
│   ├── 03-核心概念.md          # 核心概念
│   └── 04-快速开始.md       # 快速开始
├── java-demo/                   # Java 原生使用示例
│   ├── pom.xml                 # Maven 配置
│   ├── README.md               # 使用说明
│   └── src/main/java/com/example/
│       ├── common/
│       │   └── ActiveMQUtil.java
│       ├── producer/
│       │   ├── QueueProducer.java
│       │   ├── TopicPublisher.java
│       │   └── TransactionProducer.java
│       └── consumer/
│           ├── QueueConsumer.java
│           ├── AsyncQueueConsumer.java
│           ├── TopicSubscriber.java
│           ├── DurableTopicSubscriber.java
│           └── SelectorConsumer.java
├── spring-demo/                 # Spring 整合示例
│   ├── pom.xml                 # Maven 配置
│   ├── README.md               # 使用说明
│   ├── src/main/java/com/example/
│   │   ├── Application.java
│   │   ├── producer/
│   │   │   ├── SpringQueueProducer.java
│   │   │   ├── SpringTopicPublisher.java
│   │   │   └── SerializableObject.java
│   │   └── listener/
│   │       ├── QueueMessageListener.java
│   │       ├── TopicMessageListener.java
│   │       ├── AnnotationQueueListener.java
│   │       └── AnnotationTopicListener.java
│   └── src/main/resources/
│       └── spring-context.xml
└── springboot-demo/             # SpringBoot 整合示例
    ├── pom.xml                 # Maven 配置
    ├── README.md               # 使用说明
    ├── src/main/java/com/example/
    │   ├── ActiveMqSpringBootApplication.java
    │   ├── config/
    │   │   └── ActiveMQConfig.java
    │   ├── service/
    │   │   ├── MessageProducerService.java
    │   │   └── SerializableObject.java
    │   ├── listener/
    │   │   ├── QueueMessageListener.java
    │   │   └── TopicMessageListener.java
    │   └── controller/
    │       ├── MessagingController.java
    │       └── WebController.java
    └── src/main/resources/
        ├── application.yml
        └── application.properties
```

## 学习路线

1. 阅读 `docs/01-简介.md` - 了解 ActiveMQ 是什么
2. 阅读 `docs/02-安装与配置.md` - 安装 ActiveMQ
3. 阅读 `docs/03-核心概念.md` - 理解核心概念（队列、主题等）
4. 阅读 `docs/04-快速开始.md` - 快速开始指南
5. 运行 `java-demo/` - 学习原生 Java API 使用
6. 运行 `spring-demo/` - 学习 Spring 整合
7. 运行 `springboot-demo/` - 学习 SpringBoot 整合

## 环境要求

- JDK 8+ (SpringBoot 示例需要 JDK 17)
- Maven 3.6+
- Docker（可选）
- IntelliJ IDEA 或其他 IDE

## 快速开始

### 1. 启动 ActiveMQ

使用 Docker Compose（推荐）：

```bash
docker-compose up -d
```

或手动启动本地安装的 ActiveMQ：

```bash
activemq start
```

### 2. 运行 Java 示例

```bash
cd java-demo
mvn clean compile

# 运行生产者
mvn exec:java -Dexec.mainClass="com.example.producer.QueueProducer"

# 运行消费者（新终端）
mvn exec:java -Dexec.mainClass="com.example.consumer.QueueConsumer"
```

### 3. 运行 Spring 示例

```bash
cd spring-demo
mvn clean compile

# 运行应用
mvn exec:java -Dexec.mainClass="com.example.Application"
```

### 4. 运行 SpringBoot 示例

```bash
cd springboot-demo
mvn spring-boot:run

# 访问应用
open http://localhost:8080
```

## Web 管理控制台

ActiveMQ Web 控制台地址：`http://localhost:8161`

默认登录凭证：
- 用户名：`admin`
- 密码：`admin`

## 示例说明

### Java 原生示例 (java-demo/)

- `QueueProducer` / `QueueConsumer` - 点对点模型示例
- `TopicPublisher` / `TopicSubscriber` - 发布订阅模型示例
- `DurableTopicSubscriber` - 持久订阅示例
- `TransactionProducer` - 事务消息示例
- `SelectorConsumer` - 消息选择器示例

### Spring 整合示例 (spring-demo/)

- XML 配置和注解配置两种方式
- `JmsTemplate` 发送消息
- `@JmsListener` 注解监听消息
- 支持消息属性、对象消息

### SpringBoot 整合示例 (springboot-demo/)

- 自动配置简化开发
- REST API 接口
- 支持连接池配置
- 持久订阅和消息选择器

## 常见问题

### ActiveMQ 无法启动

检查端口 61616（消息端口）和 8161（管理控制台）是否被占用

### 消息消费失败

确保消费者在消息发送前已经启动，或者使用持久订阅

### Docker 启动失败

检查 Docker 服务是否运行，端口是否被占用

## 参考资料

- [ActiveMQ 官方文档](http://activemq.apache.org/)
- [Spring JMS 文档](https://docs.spring.io/spring-framework/reference/integration/jms.html)
- [SpringBoot ActiveMQ 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/messaging.html#messaging.jms)
