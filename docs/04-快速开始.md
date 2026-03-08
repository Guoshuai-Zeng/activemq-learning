# ActiveMQ 快速开始

## 前置准备

1. 已安装并启动 ActiveMQ（参见 [安装指南](./02-installation.md)）
2. 已安装 JDK 8+
3. 已安装 Maven 3.6+

## 点对点模型示例

### 发送消息

```java
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class QueueProducer {
    public static void main(String[] args) {
        // 1. 创建连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory(
            "tcp://localhost:61616"
        );

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            // 2. 创建连接
            connection = factory.createConnection();
            connection.start();

            // 3. 创建会话
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 4. 创建队列
            Destination destination = session.createQueue("HELLO_QUEUE");

            // 5. 创建消息生产者
            producer = session.createProducer(destination);

            // 6. 创建并发送消息
            for (int i = 1; i <= 10; i++) {
                TextMessage message = session.createTextMessage();
                message.setText("Hello ActiveMQ! 消息 " + i);
                message.setIntProperty("messageNumber", i);

                producer.send(message);
                System.out.println("发送消息: " + message.getText());
            }

            System.out.println("所有消息发送完成！");

        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            // 7. 关闭资源
            try {
                if (producer != null) producer.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 接收消息

```java
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class QueueConsumer {
    public static void main(String[] args) {
        // 1. 创建连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory(
            "tcp://localhost:61616"
        );

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            // 2. 创建连接
            connection = factory.createConnection();
            connection.start();

            // 3. 创建会话
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 4. 创建队列
            Destination destination = session.createQueue("HELLO_QUEUE");

            // 5. 创建消息消费者
            consumer = session.createConsumer(destination);

            // 6. 接收消息
            while (true) {
                // 阻塞接收，10秒超时
                Message message = consumer.receive(10000);

                if (message == null) {
                    System.out.println("10秒内没有收到消息，退出...");
                    break;
                }

                // 处理文本消息
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    int messageNumber = message.getIntProperty("messageNumber");

                    System.out.println("收到消息: " + text);
                    System.out.println("消息编号: " + messageNumber);
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            // 7. 关闭资源
            try {
                if (consumer != null) consumer.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
```

## 发布/订阅模型示例

### 发布消息

```java
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TopicPublisher {
    public static void main(String[] args) {
        ConnectionFactory factory = new ActiveMQConnectionFactory(
            "tcp://localhost:61616"
        );

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 创建主题
            Topic topic = session.createTopic("NEWS_TOPIC");

            producer = session.createProducer(topic);

            // 发布消息
            for (int i = 1; i <= 5; i++) {
                TextMessage message = session.createTextMessage();
                message.setText("新闻更新 - 第" + i + "期");
                message.setStringProperty("category", "technology");

                producer.send(message);
                System.out.println("发布消息: " + message.getText());

                Thread.sleep(1000);  // 间隔1秒
            }

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (producer != null) producer.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 订阅消息（非持久）

```java
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TopicSubscriber {
    public static void main(String[] args) {
        ConnectionFactory factory = new ActiveMQConnectionFactory(
            "tcp://localhost:61616"
        );

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic("NEWS_TOPIC");

            consumer = session.createConsumer(topic);

            // 使用消息监听器异步接收
            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String category = message.getStringProperty("category");

                        System.out.println("收到消息: " + textMessage.getText());
                        System.out.println("分类: " + category);
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });

            // 保持运行
            System.out.println("订阅者已启动，等待消息...");
            System.out.println("按 Ctrl+C 退出...");
            Thread.sleep(Long.MAX_VALUE);

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (consumer != null) consumer.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
```

## 持久订阅示例

```java
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class DurableTopicSubscriber {
    public static void main(String[] args) {
        // 设置客户端ID，必须唯一
        String clientId = "durable-subscriber-1";

        ConnectionFactory factory = new ActiveMQConnectionFactory(
            "tcp://localhost:61616"
        );

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            connection = factory.createConnection();
            connection.setClientID(clientId);  // 必须设置
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic("NEWS_TOPIC");

            // 创建持久订阅
            consumer = session.createDurableSubscriber(topic, "my-durable-subscription");

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("持久订阅收到消息: " + textMessage.getText());
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("持久订阅已启动...");
            Thread.sleep(Long.MAX_VALUE);

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (consumer != null) consumer.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
```

## 运行示例

### 1. 启动 ActiveMQ

```bash
# Docker 方式
docker start activemq

# 或本地安装方式
activemq start
```

### 2. 编译运行

创建 `pom.xml`：

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>activemq-demo</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-client</artifactId>
            <version>5.18.3</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.9</version>
        </dependency>
    </dependencies>
</project>
```

编译运行：

```bash
# 编译
mvn clean compile

# 运行生产者
mvn exec:java -Dexec.mainClass="QueueProducer"

# 运行消费者（新终端）
mvn exec:java -Dexec.mainClass="QueueConsumer"
```

## 使用 Web 控制台查看消息

1. 访问 `http://localhost:8161`
2. 登录（默认 admin/admin）
3. 点击 **Queues** 查看 HELLO_QUEUE 的消息
4. 点击 **Topics** 查看 NEWS_TOPIC 的订阅者

## 调试技巧

### 1. 查看日志

```bash
# Docker 方式
docker logs activemq

# 本地方式
tail -f $ACTIVEMQ_HOME/data/activemq.log
```

### 2. 使用 JMX 监控

```bash
# 启动 JMX
jconsole
```

连接到 `localhost:1099` 查看 ActiveMQ 的 MBean。

## 下一步

- [Java 代码示例](../java-demo/)
- [Spring 整合示例](../spring-demo/)
- [SpringBoot 整合示例](../springboot-demo/)
