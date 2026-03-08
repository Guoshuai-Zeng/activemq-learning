package com.example.consumer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * 持久主题订阅者
 * 演示持久订阅，可以在离线期间保留消息
 */
public class DurableTopicSubscriber {

    private static final String TOPIC_NAME = "NOTIFICATION_TOPIC";
    private static final String CLIENT_ID = "durable-subscriber-1";
    private static final String SUBSCRIPTION_NAME = "my-durable-subscription";

    public static void main(String[] args) {
        ConnectionFactory factory = ActiveMQUtil.createConnectionFactory();

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            // 创建连接
            connection = ActiveMQUtil.createConnection(factory);

            // 必须设置客户端ID
            connection.setClientID(CLIENT_ID);
            connection.start();

            // 创建会话
            session = ActiveMQUtil.createSession(connection);

            // 创建主题
            Topic topic = ActiveMQUtil.createTopic(session, TOPIC_NAME);

            // 创建持久订阅者
            consumer = ActiveMQUtil.createDurableSubscriber(connection, session, topic, SUBSCRIPTION_NAME);

            // 设置消息监听器
            consumer.setMessageListener(new DurableNotificationListener());

            System.out.println("===== 持久订阅者已启动 =====");
            System.out.println("主题: " + TOPIC_NAME);
            System.out.println("客户端ID: " + CLIENT_ID);
            System.out.println("订阅名称: " + SUBSCRIPTION_NAME);
            System.out.println("模式: 持久订阅");
            System.out.println("特点: 即使订阅者离线，消息也会被保存");
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
     * 持久通知消息监听器
     */
    private static class DurableNotificationListener implements MessageListener {

        private int count = 0;

        @Override
        public void onMessage(Message message) {
            try {
                count++;

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String content = textMessage.getText();
                    String type = message.getStringProperty("type");
                    long timestamp = message.getLongProperty("timestamp");

                    System.out.printf("[持久订阅 #%d] [%s] %s (时间: %d)%n",
                        count, type, content, timestamp);
                }
            } catch (JMSException e) {
                System.err.println("处理消息时出错: " + e.getMessage());
            }
        }
    }
}
