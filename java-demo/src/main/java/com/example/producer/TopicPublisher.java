package com.example.producer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * 主题消息发布者
 * 演示发布/订阅模型的消息发布
 */
public class TopicPublisher {

    private static final String TOPIC_NAME = "NOTIFICATION_TOPIC";

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

            // 创建主题
            Topic topic = ActiveMQUtil.createTopic(session, TOPIC_NAME);

            // 创建消息生产者
            producer = ActiveMQUtil.createProducer(session, topic);
            // 设置为非持久化消息
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            System.out.println("===== 开始发布主题消息 =====");
            System.out.println("确保有订阅者正在运行，否则消息将丢失！");

            // 发布不同类型的消息
            publishMessage(session, producer, "SYSTEM", "系统启动完成");
            Thread.sleep(1000);

            publishMessage(session, producer, "USER", "用户 user001 登录");
            Thread.sleep(1000);

            publishMessage(session, producer, "ORDER", "订单 #10001 已创建");
            Thread.sleep(1000);

            publishMessage(session, producer, "PAYMENT", "支付成功，订单 #10001");
            Thread.sleep(1000);

            publishMessage(session, producer, "SYSTEM", "系统正在执行定时任务");
            Thread.sleep(1000);

            System.out.println("===== 消息发布完成 =====");

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            ActiveMQUtil.close(producer, session, connection);
        }
    }

    /**
     * 发布消息
     */
    private static void publishMessage(Session session, MessageProducer producer,
                                       String type, String content) throws JMSException {
        TextMessage message = session.createTextMessage();
        message.setText(content);

        // 设置消息属性
        message.setStringProperty("type", type);
        message.setStringProperty("level", "INFO");
        message.setLongProperty("timestamp", System.currentTimeMillis());

        // 发布消息
        producer.send(message);

        System.out.printf("发布消息: [%s] %s%n", type, content);
    }
}
