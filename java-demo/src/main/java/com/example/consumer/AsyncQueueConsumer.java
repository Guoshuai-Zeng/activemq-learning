package com.example.consumer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * 异步队列消费者
 * 演示使用消息监听器异步接收消息
 */
public class AsyncQueueConsumer {

    private static final String QUEUE_NAME = "ORDER_QUEUE";

    public static void main(String[] args) {
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

            // 创建消息消费者
            consumer = ActiveMQUtil.createConsumer(session, queue);

            // 设置消息监听器
            consumer.setMessageListener(new OrderMessageListener());

            System.out.println("===== 异步消费者已启动 =====");
            System.out.println("队列: " + QUEUE_NAME);
            System.out.println("模式: 异步监听");
            System.out.println("按 Ctrl+C 退出...\n");

            // 保持运行
            Thread.sleep(Long.MAX_VALUE);

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            ActiveMQUtil.close(consumer, session, connection);
        }
    }

    /**
     * 订单消息监听器
     */
    private static class OrderMessageListener implements MessageListener {

        private int count = 0;

        @Override
        public void onMessage(Message message) {
            try {
                count++;

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String content = textMessage.getText();
                    int orderId = message.getIntProperty("orderId");
                    String orderType = message.getStringProperty("orderType");

                    System.out.printf("[监听器] 收到消息 #%d: %s (订单ID: %d, 类型: %s)%n",
                        count, content, orderId, orderType);
                }
            } catch (JMSException e) {
                System.err.println("处理消息时出错: " + e.getMessage());
            }
        }
    }
}
