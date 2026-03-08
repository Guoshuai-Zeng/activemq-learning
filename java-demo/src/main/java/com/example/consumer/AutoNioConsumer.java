package com.example.consumer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Auto NIO 测试消费者
 * 使用 auto+nio 传输协议
 */
public class AutoNioConsumer {

    private static final String BROKER_URL = "auto+nio://localhost:61618";
    private static final String QUEUE_NAME = "AUTO_NIO_TEST_QUEUE";

    public static void main(String[] args) {
        // 创建连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            // 创建连接
            connection = ActiveMQUtil.createConnection(factory);
            System.out.println("连接 ActiveMQ 成功");
            System.out.println("使用传输: " + BROKER_URL);

            // 创建会话
            session = ActiveMQUtil.createSession(connection);

            // 创建队列
            Queue queue = ActiveMQUtil.createQueue(session, QUEUE_NAME);

            // 创建消息消费者
            consumer = ActiveMQUtil.createConsumer(session, queue);
            System.out.println("创建消费者成功，监听队列: " + QUEUE_NAME);
            System.out.println("等待消息...\n");

            int messageCount = 0;
            long startTime = System.currentTimeMillis();

            // 接收消息
            while (true) {
                // 阻塞接收，10秒超时
                Message message = consumer.receive(10000);

                // 超时退出
                if (message == null) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    System.out.printf("\n%d秒内没有收到消息，退出消费%n", elapsed / 1000);
                    System.out.printf("总共收到 %d 条消息%n", messageCount);
                    break;
                }

                messageCount++;

                // 处理文本消息
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String content = textMessage.getText();
                    int messageId = message.getIntProperty("messageId");
                    String transportType = message.getStringProperty("transportType");
                    long timestamp = message.getLongProperty("timestamp");

                    System.out.printf("[%d] 收到消息: %s%n", messageCount, content);
                    System.out.printf("    消息ID: %d, 传输类型: %s, 时间戳: %d%n",
                        messageId, transportType, timestamp);
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            ActiveMQUtil.close(consumer, session, connection);
            System.out.println("连接已关闭");
        }
    }
}
