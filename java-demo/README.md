# ActiveMQ Java Demo

## 概述

本目录包含使用原生 Java API 操作 ActiveMQ 的示例代码。

## 项目结构

```
java-demo/
├── pom.xml
└── src/main/java/com/example/
    ├── common/
    │   └── ActiveMQUtil.java       # ActiveMQ 工具类
    ├── producer/
    │   ├── QueueProducer.java      # 队列消息生产者
    │   ├── TopicPublisher.java     # 主题消息发布者
    │   └── TransactionProducer.java # 事务消息生产者
    └── consumer/
        ├── QueueConsumer.java      # 队列消息消费者（同步）
        ├── AsyncQueueConsumer.java # 队列消息消费者（异步）
        ├── TopicSubscriber.java    # 主题订阅者
        ├── DurableTopicSubscriber.java # 持久主题订阅者
        └── SelectorConsumer.java    # 消息选择器消费者
```

## 运行前准备

确保 ActiveMQ 已启动：

```bash
# Docker 方式
docker start activemq

# 或检查本地安装的 ActiveMQ
./activemq status
```

## 运行示例

### 方式一：使用 Maven exec 插件

```bash
# 进入项目目录
cd java-demo

# 运行队列生产者
mvn exec:java -Dexec.mainClass="com.example.producer.QueueProducer"

# 运行队列消费者（新终端）
mvn exec:java -Dexec.mainClass="com.example.consumer.QueueConsumer"

# 运行主题发布者
mvn exec:java -Dexec.mainClass="com.example.producer.TopicPublisher"

# 运行主题订阅者（新终端）
mvn exec:java -Dexec.mainClass="com.example.consumer.TopicSubscriber"

# 运行持久订阅者
mvn exec:java -Dexec.mainClass="com.example.consumer.DurableTopicSubscriber"

# 运行消息选择器消费者
mvn exec:java -Dexec.mainClass="com.example.consumer.SelectorConsumer"
```

### 方式二：编译后运行

```bash
# 编译项目
mvn clean compile

# 运行类（使用 java 命令）
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) com.example.producer.QueueProducer
```

## 示例说明

### 1. QueueProducer & QueueConsumer

演示点对点模型（Queue）：
- 生产者发送 10 条订单消息
- 消费者接收并处理消息
- 支持消息属性、优先级设置

### 2. TopicPublisher & TopicSubscriber

演示发布/订阅模型（Topic）：
- 发布者发送不同类型的通知消息
- 订阅者异步接收消息
- 注意：订阅者必须在发布者启动前运行，否则消息将丢失

### 3. DurableTopicSubscriber

演示持久订阅：
- 订阅者离线期间的消息会被保存
- 重新上线后可以接收离线期间的消息
- 需要设置唯一的 clientId 和 subscriptionName

### 4. TransactionProducer

演示事务消息：
- 发送三批消息，中间一批会回滚
- 消费者只能收到提交的消息

### 5. SelectorConsumer

演示消息选择器：
- 使用 SQL-like 语法过滤消息
- 只接收符合条件的消息

## Web 控制台

启动示例后，可以通过 ActiveMQ Web 控制台查看消息：

访问：http://localhost:8161

登录：admin / admin

查看：
- **Queues** - 查看队列消息
- **Topics** - 查看主题订阅
- **Subscribers** - 查看订阅者状态
