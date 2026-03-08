package com.example.consumer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * 消息选择器消费者
 * 演示使用消息选择器过滤消息
 */
public class SelectorConsumer {

    private static final String QUEUE_NAME = "ORDER_QUEUE";

    public static void main(String[] args) {
        // 使用消息选择器，只接收 orderType = "PRIORITY" 的消息
        String selector = "orderType = 'PRIORITY' AND JMSPriority >= 7";

        ConnectionFactory factory = ActiveMQUtil.createConnectionFactory();

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            // 创建连接
            connection = ActiveMQUtil.createConnection(factory);

            // 创建会话
            session = ActiveMQUtil.createSession(connection);

            // 创建队列
            Queue queue = ActiveMQUtil.createQueue(session, QUEUE_NAME);

            // 创建带选择器的消息消费者
            consumer = ActiveMQUtil.createConsumer(session, queue, selector);

            System.out.println("===== 消息选择器消费者已启动 =====");
            System.out.println("队列: " + QUEUE_NAME);
            System.out.println("选择器: " + selector);
            System.out.println("只接收: orderType='PRIORITY' 且 优先级>=7 的消息");
            System.out.println("\n等待匹配的消息...\n");

            int messageCount = 0;
            long startTime = System.currentTimeMillis();

            while (true) {
                // 接收消息，10秒超时
                Message message = consumer.receive(10000);

                if (message == null) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    System.out.printf("\n%d秒内没有收到匹配消息，退出消费\n", elapsed / 1000);
                    System.out.printf("总共收到匹配消息: %d 条\n", messageCount);
                    break;
                }

                messageCount++;

                // 处理消息
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String content = textMessage.getText();
                    int orderId = message.getIntProperty("orderId");
                    String orderType = message.getStringProperty("orderType");
                    int priority = message.getJMSPriority();

                    System.out.printf("[%d] 收到匹配消息: %s | 订单ID: %d | 类型: %s | 优先级: %d%n",
                        messageCount, content, orderId, orderType, priority);
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            ActiveMQUtil.close(consumer, session, connection);
        }
    }
}
