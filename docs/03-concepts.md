# ActiveMQ 核心概念

## JMS 基础

Java Message Service (JMS) 是 Java 平台上关于面向消息中间件 (MOM) 的 API，用于在两个或多个客户端之间发送消息。

### JMS 核心组件

```
┌─────────┐         ┌─────────┐         ┌─────────┐
│ Producer│────────>│Provider │────────>│Consumer │
│ 生产者   │  消息   │ActiveMQ │  消息   │消费者   │
└─────────┘         └─────────┘         └─────────┘
```

| 组件 | 说明 |
|------|------|
| **ConnectionFactory** | 连接工厂，用于创建连接 |
| **Connection** | 与 ActiveMQ 服务器的物理连接 |
| **Session** | 会话，单线程上下文，用于创建消息生产者和消费者 |
| **Destination** | 目的地，消息的发送/接收目标 |
| **MessageProducer** | 消息生产者，用于发送消息 |
| **MessageConsumer** | 消息消费者，用于接收消息 |
| **Message** | 消息对象，包含数据和头信息 |

## 消息模型

### 1. 点对点模型 (Point-to-Point, P2P)

基于消息队列 (Queue) 的模型：

```
┌─────────┐         ┌──────┐         ┌─────────┐
│Producer1│────────>│ Queue│────────>│Consumer1│
│生产者    │         │队列  │         └─────────┘
└─────────┘         └──────┘         ┌─────────┐
                    (点对点)          │Consumer2│
┌─────────┐         └──────┘         │消费者   │
│Producer2│────────────────────────>└─────────┘
│生产者    │                           (负载均衡)
└─────────┘
```

**特点：**
- 每个消息只有一个消费者
- 消息发送后立即从队列中移除
- 消费者必须主动监听才能接收消息
- 消费者之间是竞争关系（负载均衡）

**适用场景：**
- 任务分发
- 订单处理
- 数据处理流水线

### 2. 发布/订阅模型 (Publish/Subscribe, Pub/Sub)

基于主题 (Topic) 的模型：

```
┌─────────┐         ┌──────┐         ┌─────────┐
│Publisher│────────>│Topic │────────>│Subscriber│
│发布者    │         │主题  │  广播   │1 订阅者  │
└─────────┘         └──────┘         └─────────┘
                    (发布/订阅)
                                     ┌─────────┐
                                     │Subscriber│
                                     │2 订阅者  │
                                     └─────────┘
```

**特点：**
- 每个消息可以有多个消费者
- 消息发送后保留在主题中，直到被所有订阅者接收（非持久订阅）
- 消费者之间是订阅关系
- 支持持久订阅

**适用场景：**
- 实时通知
- 新闻广播
- 日志收集

## Destination（目的地）

### Queue（队列）

点对点模型中的目的地：

```java
// 创建队列
Queue queue = session.createQueue("ORDER_QUEUE");
// 或使用 String 标识
Destination destination = session.createQueue("ORDER_QUEUE");
```

队列命名规范：
- 大写字母和下划线
- 语义明确：`ORDER_QUEUE`, `USER_REGISTER_QUEUE`, `PAYMENT_QUEUE`

### Topic（主题）

发布/订阅模型中的目的地：

```java
// 创建主题
Topic topic = session.createTopic("NEWS_TOPIC");
// 或使用 String 标识
Destination destination = session.createTopic("NEWS_TOPIC");
```

主题命名规范：
- 大写字母和下划线
- 语义明确：`NEWS_TOPIC`, `NOTIFICATION_TOPIC`, `LOG_TOPIC`

## Message（消息）

### 消息类型

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| TextMessage | 文本消息 | 简单字符串数据 |
| ObjectMessage | 对象消息 | Java 对象（需实现 Serializable） |
| BytesMessage | 字节消息 | 二进制数据、文件 |
| StreamMessage | 流消息 | 按顺序读写的基本数据流 |
| MapMessage | 键值对消息 | 名字-值对集合 |

### 消息结构

```
┌─────────────────────────────────────┐
│              Header (消息头)          │
│  - JMSMessageID: 消息唯一标识          │
│  - JMSTimestamp: 时间戳               │
│  - JMSPriority: 优先级 (0-9)          │
│  - JMSCorrelationID: 关联ID           │
│  - JMSReplyTo: 回复目的地              │
├─────────────────────────────────────┤
│            Properties (属性)          │
│  - 自定义键值对                        │
│  - 应用自定义信息                       │
├─────────────────────────────────────┤
│              Body (消息体)             │
│  - 实际消息内容                         │
└─────────────────────────────────────┘
```

### 消息头 (Headers)

```java
// 设置消息头
message.setJMSMessageID("MSG-001");
message.setJMSPriority(9);  // 0-9，9最高
message.setJMSTimestamp(System.currentTimeMillis());

// 读取消息头
String messageId = message.getJMSMessageID();
int priority = message.getJMSPriority();
```

### 消息属性 (Properties)

```java
// 设置自定义属性
message.setStringProperty("orderType", "NEW");
message.setIntProperty("orderId", 1001);
message.setBooleanProperty("urgent", true);

// 读取属性
String orderType = message.getStringProperty("orderType");
int orderId = message.getIntProperty("orderId");
boolean urgent = message.getBooleanProperty("urgent");
```

### 消息优先级

```java
message.setJMSPriority(9);  // 0-9，9最高优先级
```

- 0-4：普通优先级
- 5-9：高优先级
- 消费者优先级高的消息

## 消息确认模式

### AUTO_ACKNOWLEDGE（自动确认）

```java
Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
```

- 消息被消费后自动确认
- 最简单的模式
- 可能丢失消息（处理后崩溃）

### CLIENT_ACKNOWLEDGE（客户端确认）

```java
Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
```

- 需要手动调用 `message.acknowledge()`
- 更可靠
- 可以批量确认

```java
Message message = consumer.receive();
// 处理消息...
message.acknowledge();  // 手动确认
```

### DUPS_OK_ACKNOWLEDGE（允许重复）

```java
Session session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
```

- 允许重复消费
- 性能更好
- 需要处理幂等性

### SESSION_TRANSACTED（事务）

```java
Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
```

- 事务性消息
- 需要手动提交

```java
// 发送消息
producer.send(message);
session.commit();  // 提交事务
```

## 消息接收方式

### 同步接收

```java
// 阻塞接收
Message message = consumer.receive();

// 带超时接收
Message message = consumer.receive(1000);  // 1秒超时

// 立即返回
Message message = consumer.receiveNoWait();
```

### 异步接收

```java
consumer.setMessageListener(new MessageListener() {
    @Override
    public void onMessage(Message message) {
        // 处理消息
        TextMessage textMessage = (TextMessage) message;
        System.out.println("收到消息: " + textMessage.getText());
    }
});
```

## 事务

### 本地事务

```java
// 创建事务会话
Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

try {
    // 发送多条消息
    producer.send(message1);
    producer.send(message2);
    producer.send(message3);

    // 提交事务
    session.commit();
} catch (Exception e) {
    // 回滚事务
    session.rollback();
}
```

### XA 分布式事务

支持与其他资源（如数据库）的分布式事务。

## 持久化与非持久化

### 持久化消息

```java
producer.setDeliveryMode(DeliveryMode.PERSISTENT);
producer.send(message);
```

- 消息持久化到磁盘
- ActiveMQ 重启后消息仍然存在
- 性能略低

### 非持久化消息

```java
producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
producer.send(message);
```

- 消息仅存储在内存中
- ActiveMQ 重启后消息丢失
- 性能更高

## TTL (Time To Live)

```java
// 设置消息过期时间（毫秒）
producer.setTimeToLive(60000);  // 60秒后过期
producer.send(message);
```

## 消息选择器

使用 SQL-like 语法过滤消息：

```java
// 发送时设置属性
message.setStringProperty("priority", "high");

// 消费时使用选择器
String selector = "priority = 'high'";
MessageConsumer consumer = session.createConsumer(queue, selector);
```

支持的运算符：
- 比较运算符：`=, >, <, >=, <=, <>`
- 逻辑运算符：`AND, OR, NOT`
- NULL 检查：`IS NULL, IS NOT NULL`
- LIKE 模式：`LIKE`
- IN 检查：`IN`

## 下一步

- [快速开始示例](./04-quick-start.md)
- [Java 代码示例](../java-demo/)
