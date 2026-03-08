# ActiveMQ Spring Demo

## 概述

本目录包含使用 Spring 框架整合 ActiveMQ 的示例代码。

## 项目结构

```
spring-demo/
├── pom.xml
├── src/main/
│   ├── java/com/example/
│   │   ├── Application.java           # 应用入口
│   │   ├── producer/
│   │   │   ├── SpringQueueProducer.java    # 队列消息生产者
│   │   │   ├── SpringTopicPublisher.java   # 主题消息发布者
│   │   │   └── SerializableObject.java     # 可序列化对象
│   │   └── listener/
│   │       ├── QueueMessageListener.java       # 队列监听器（XML配置）
│   │       ├── TopicMessageListener.java       # 主题监听器（XML配置）
│   │       ├── AnnotationQueueListener.java    # 队列监听器（注解配置）
│   │       └── AnnotationTopicListener.java    # 主题监听器（注解配置）
│   └── resources/
│       └── spring-context.xml          # Spring 配置文件
```

## 核心配置

### Spring 配置文件 (spring-context.xml)

配置内容包括：
- ActiveMQ 连接工厂
- 连接池
- JMS 模板
- 监听器容器
- 消息目的地（Queue/Topic）

## 运行示例

### 编译项目

```bash
cd spring-demo
mvn clean compile
```

### 运行应用

```bash
mvn exec:java -Dexec.mainClass="com.example.Application"
```

### 使用 Maven 插件运行

需要在 pom.xml 中添加 exec 插件配置：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <mainClass>com.example.Application</mainClass>
    </configuration>
</plugin>
```

然后运行：

```bash
mvn exec:java
```

## 功能说明

### 发送队列消息

```
选择: 1
请输入消息内容: Hello Spring ActiveMQ
```

### 发送带属性的消息

```
选择: 2
请输入消息内容: 订单消息
```

### 发送对象消息

```
选择: 3
```

### 批量发送消息

```
选择: 4
```

### 发布主题消息

```
选择: 5
请输入主题消息内容: 系统通知
```

### 发布各种消息

```
选择: 6
```

### 批量发布消息

```
选择: 7
```

## 监听器配置

### XML 配置方式

在 `spring-context.xml` 中配置：

```xml
<bean id="queueListenerContainer"
      class="org.springframework.jms.listener.DefaultMessageListenerContainer">
    <property name="connectionFactory" ref="connectionFactory"/>
    <property name="destination" ref="springQueue"/>
    <property name="messageListener" ref="queueMessageListener"/>
    <property name="concurrency" value="3"/>
</bean>
```

### 注解配置方式

```java
@Component
public class AnnotationQueueListener {

    @JmsListener(
        destination = "SPRING_QUEUE",
        containerFactory = "jmsListenerContainerFactory"
    )
    public void onMessage(String message) {
        // 处理消息
    }
}
```

## 常用配置

### JmsTemplate 配置

| 属性 | 说明 | 默认值 |
|------|------|--------|
| connectionFactory | 连接工厂 | - |
| defaultDestination | 默认目的地 | - |
| deliveryMode | 投递模式 | PERSISTENT |
| sessionTransacted | 会话事务 | false |
| timeToLive | 消息存活时间 | 0 (永久) |

### 监听器容器配置

| 属性 | 说明 | 默认值 |
|------|------|--------|
| connectionFactory | 连接工厂 | - |
| destination | 目的地 | - |
| concurrency | 并发数 | 1-1 |
| acknowledgeMode | 确认模式 | AUTO |
| sessionTransacted | 会话事务 | false |
