# ActiveMQ 简介

## 什么是 ActiveMQ

Apache ActiveMQ 是一个开源的消息中间件，实现了 Java Message Service (JMS) 规范。它支持多种消息协议和语言，是构建分布式系统和异步通信的重要工具。

## ActiveMQ 的特点

### 1. 多协议支持
- **JMS 1.1/2.0** - Java 消息服务标准
- **AMQP** - 高级消息队列协议
- **MQTT** - 轻量级消息协议
- **STOMP** - 简单文本协议
- **OpenWire** - ActiveMQ 自有协议

### 2. 消息模型
- **点对点 (Point-to-Point, P2P)** - 基于 Queue 的模型
- **发布/订阅 (Publish/Subscribe, Pub/Sub)** - 基于 Topic 的模型

### 3. 高可靠性
- 消息持久化
- 消息确认机制
- 事务支持
- 主从复制

### 4. 高性能
- 异步消息处理
- 消息分组和批处理
- 连接池
- NIO 传输

### 5. 集群支持
- 网络连接
- 主从复制
- 负载均衡

## 应用场景

### 1. 异步处理
```
用户提交订单 -> ActiveMQ -> 订单处理系统
              -> 库存系统
              -> 通知系统
```

### 2. 系统解耦
```
系统A -> ActiveMQ -> 系统B
                    -> 系统C
                    -> 系统D
```

### 3. 流量削峰
```
大量请求 -> ActiveMQ（缓冲）-> 慢速消费者
```

### 4. 广播通知
```
消息发布 -> ActiveMQ -> 订阅者A
                     -> 订阅者B
                     -> 订阅者C
```

## ActiveMQ vs 其他消息中间件

| 特性 | ActiveMQ | RabbitMQ | Kafka | RocketMQ |
|------|----------|----------|-------|----------|
| 协议 | JMS/AMQP/STOMP/MQTT | AMQP | 自定义协议 | 自定义协议 |
| 吞吐量 | 中等 | 高 | 极高 | 高 |
| 延迟 | 低 | 低 | 低 | 低 |
| 持久化 | 支持 | 支持 | 支持 | 支持 |
| 消息回溯 | 支持 | 支持 | 支持 | 支持 |
| 易用性 | 高 | 中 | 中 | 中 |
| 适用场景 | 企业应用 | 微服务 | 大数据/日志 | 电商/金融 |

## ActiveMQ 版本

### ActiveMQ 5.x (Classic)
- 成熟稳定
- JMS 1.1 实现
- 适合传统企业应用

### ActiveMQ Artemis 2.x
- 下一代 ActiveMQ
- JMS 2.0 实现
- 更高性能
- 支持 AMQP 1.0

**本项目基于 ActiveMQ 5.18.x 进行讲解**

## 下一步

- [安装 ActiveMQ](./02-installation.md)
- [了解核心概念](./03-concepts.md)
