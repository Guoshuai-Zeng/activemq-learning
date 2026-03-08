package com.example.consumer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * 主题订阅者
 * 演示发布/订阅模型的消息订阅（非持久）
 */
public class TopicSubscriber {

    private static final String TOPIC_NAME = "NOTIFICATION_TOPIC";

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

            // 创建主题
            Topic topic = ActiveMQUtil.createTopic(session, TOPIC_NAME);

            // 创建消息消费者
            consumer = ActiveMQUtil.createConsumer(session, topic);

            // 设置消息监听器
            consumer.setMessageListener(new NotificationListener());

            System.out.println("===== 主题订阅者已启动 =====");
            System.out.println("主题: " + TOPIC_NAME);
            System.out.println("模式: 非持久订阅");
            System.out.println("注意: 订阅者必须在发布者发布消息前启动，否则消息将丢失");
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
     * 通知消息监听器
     */
    private static class NotificationListener implements MessageListener {

        private int count = 0;

        @Override
        public void onMessage(Message message) {
            try {
                count++;

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String content = textMessage.getText();
                    String type = message.getStringProperty("type");
                    String level = message.getStringProperty("level");

                    System.out.printf("[订阅者 #%d] [%s:%s] %s%n",
                        count, type, level, content);
                }
            } catch (JMSException e) {
                System.err.println("处理消息时出错: " + e.getMessage());
            }
        }
    }
}
