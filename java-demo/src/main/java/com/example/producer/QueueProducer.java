package com.example.producer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * 队列消息生产者
 * 演示点对点模型的消息发送
 */
public class QueueProducer {

    private static final String QUEUE_NAME = "ORDER_QUEUE";

    public static void main(String[] args) {
        // 创建连接工厂
        ConnectionFactory factory = ActiveMQUtil.createConnectionFactory();

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            // 创建连接
            connection = ActiveMQUtil.createConnection(factory);

            // 创建会话
            session = ActiveMQUtil.createSession(connection);

            // 创建队列
            Queue queue = ActiveMQUtil.createQueue(session, QUEUE_NAME);

            // 创建消息生产者
            producer = ActiveMQUtil.createProducer(session, queue);

            System.out.println("===== 开始发送队列消息 =====");

            // 发送多条消息
            for (int i = 1; i <= 10; i++) {
                // 创建文本消息
                TextMessage message = session.createTextMessage();
                message.setText("订单消息 #" + i);

                // 设置消息头
                message.setJMSPriority(i % 5 + 5); // 优先级 5-9

                // 设置消息属性
                message.setStringProperty("orderType", i % 2 == 0 ? "NORMAL" : "PRIORITY");
                message.setIntProperty("orderId", 10000 + i);
                message.setLongProperty("timestamp", System.currentTimeMillis());

                // 发送消息
                producer.send(message);

                System.out.printf("发送消息: %s | 订单ID: %d | 类型: %s | 优先级: %d%n",
                    message.getText(),
                    message.getIntProperty("orderId"),
                    message.getStringProperty("orderType"),
                    message.getJMSPriority());

                // 稍作延迟
                Thread.sleep(200);
            }

            System.out.println("===== 消息发送完成 =====");

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            ActiveMQUtil.close(producer, session, connection);
        }
    }
}
