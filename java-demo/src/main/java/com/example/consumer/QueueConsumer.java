package com.example.consumer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * 队列消息消费者
 * 演示点对点模型的消息接收（同步方式）
 */
public class QueueConsumer {

    private static final String QUEUE_NAME = "ORDER_QUEUE";

    public static void main(String[] args) {
        ConnectionFactory factory = ActiveMQUtil.createConnectionFactory();

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            // 创建连接
            connection = ActiveMQUtil.createConnection(factory);

            // 创建会话（客户端确认模式）
            session = ActiveMQUtil.createSession(connection, false, Session.CLIENT_ACKNOWLEDGE);

            // 创建队列
            Queue queue = ActiveMQUtil.createQueue(session, QUEUE_NAME);

            // 创建消息消费者
            consumer = ActiveMQUtil.createConsumer(session, queue);

            System.out.println("===== 开始消费队列消息 =====");
            System.out.println("队列: " + QUEUE_NAME);
            System.out.println("确认模式: CLIENT_ACKNOWLEDGE");
            System.out.println("等待消息...\n");

            int messageCount = 0;
            long startTime = System.currentTimeMillis();

            while (true) {
                // 同步接收消息，10秒超时
                Message message = consumer.receive(10000);

                // 超时退出
                if (message == null) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    System.out.printf("\n%d秒内没有收到消息，退出消费\n", elapsed / 1000);
                    System.out.printf("总共收到消息: %d 条\n", messageCount);
                    break;
                }

                messageCount++;

                // 处理文本消息
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String content = textMessage.getText();
                    int orderId = message.getIntProperty("orderId");
                    String orderType = message.getStringProperty("orderType");
                    int priority = message.getJMSPriority();

                    System.out.printf("[%d] 收到消息: %s | 订单ID: %d | 类型: %s | 优先级: %d%n",
                        messageCount, content, orderId, orderType, priority);

                    // 模拟处理时间
                    Thread.sleep(100);

                    // 手动确认消息
                    message.acknowledge();
                }
            }

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            ActiveMQUtil.close(consumer, session, connection);
        }
    }
}
