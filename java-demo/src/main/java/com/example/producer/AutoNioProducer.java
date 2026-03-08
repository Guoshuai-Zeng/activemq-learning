package com.example.producer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * Auto NIO 测试生产者
 * 使用 auto+nio 传输协议
 */
public class AutoNioProducer {

    private static final String BROKER_URL = "auto+nio://localhost:61618";
    private static final String QUEUE_NAME = "AUTO_NIO_TEST_QUEUE";

    public static void main(String[] args) {
        // 创建连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            // 创建连接
            connection = ActiveMQUtil.createConnection(factory);
            System.out.println("连接 ActiveMQ 成功");
            System.out.println("使用传输: " + BROKER_URL);

            // 创建会话
            session = ActiveMQUtil.createSession(connection);

            // 创建队列
            Queue queue = ActiveMQUtil.createQueue(session, QUEUE_NAME);

            // 创建消息生产者
            producer = ActiveMQUtil.createProducer(session, queue);
            System.out.println("创建生产者成功，目标队列: " + QUEUE_NAME);

            System.out.println("\n===== 开始发送消息 =====");

            // 发送多条消息
            for (int i = 1; i <= 10; i++) {
                TextMessage message = session.createTextMessage();
                message.setText("Auto NIO 测试消息 #" + i);

                // 设置消息属性
                message.setIntProperty("messageId", i);
                message.setStringProperty("transportType", "AUTO+NIO");
                message.setLongProperty("timestamp", System.currentTimeMillis());

                // 发送消息
                producer.send(message);

                System.out.printf("[%d] 发送消息: %s%n", i, message.getText());

                // 稍作延迟
                Thread.sleep(100);
            }

            System.out.println("\n===== 消息发送完成 =====");
            System.out.println("总共发送 10 条消息到队列: " + QUEUE_NAME);

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            ActiveMQUtil.close(producer, session, connection);
            System.out.println("连接已关闭");
        }
    }
}
