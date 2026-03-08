package com.example.consumer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * Auto NIO 异步消费者
 * 使用消息监听器异步接收消息
 */
public class AsyncAutoNioConsumer {

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

            // 设置消息监听器
            consumer.setMessageListener(new MessageListener() {
                private int count = 0;

                @Override
                public void onMessage(Message message) {
                    try {
                        count++;

                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            String content = textMessage.getText();
                            int messageId = message.getIntProperty("messageId");
                            String transportType = message.getStringProperty("transportType");
                            long timestamp = message.getLongProperty("timestamp");

                            System.out.printf("[异步监听器 #%d] 收到消息: %s%n",
                                count, content);
                            System.out.printf("    消息ID: %d, 传输类型: %s%n",
                                messageId, transportType);
                        }
                    } catch (JMSException e) {
                        System.err.println("处理消息时出错: " + e.getMessage());
                    }
                }
            });

            // 保持运行
            System.out.println("异步消费者已启动，按 Ctrl+C 退出...");
            Thread.sleep(Long.MAX_VALUE);

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            ActiveMQUtil.close(consumer, session, connection);
        }
    }
}
