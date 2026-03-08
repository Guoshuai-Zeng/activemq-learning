package com.example.common;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * ActiveMQ 工具类
 * 提供常用的连接、会话等资源创建方法
 */
public class ActiveMQUtil {

    private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "admin";

    /**
     * 创建默认的连接工厂
     */
    public static ConnectionFactory createConnectionFactory() {
        return new ActiveMQConnectionFactory(DEFAULT_BROKER_URL);
    }

    /**
     * 创建带认证的连接工厂
     */
    public static ConnectionFactory createConnectionFactory(String brokerUrl, String user, String password) {
        return new ActiveMQConnectionFactory(user, password, brokerUrl);
    }

    /**
     * 创建连接
     */
    public static Connection createConnection(ConnectionFactory factory) throws JMSException {
        Connection connection = factory.createConnection();
        connection.start();
        return connection;
    }

    /**
     * 创建会话（非事务，自动确认）
     */
    public static Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * 创建会话（可配置）
     */
    public static Session createSession(Connection connection, boolean transacted, int acknowledgeMode) throws JMSException {
        return connection.createSession(transacted, acknowledgeMode);
    }

    /**
     * 创建队列
     */
    public static Queue createQueue(Session session, String queueName) throws JMSException {
        return session.createQueue(queueName);
    }

    /**
     * 创建主题
     */
    public static Topic createTopic(Session session, String topicName) throws JMSException {
        return session.createTopic(topicName);
    }

    /**
     * 创建消息生产者
     */
    public static MessageProducer createProducer(Session session, Destination destination) throws JMSException {
        return session.createProducer(destination);
    }

    /**
     * 创建持久化消息生产者
     */
    public static MessageProducer createPersistentProducer(Session session, Destination destination) throws JMSException {
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        return producer;
    }

    /**
     * 创建消息消费者
     */
    public static MessageConsumer createConsumer(Session session, Destination destination) throws JMSException {
        return session.createConsumer(destination);
    }

    /**
     * 创建带选择器的消息消费者
     */
    public static MessageConsumer createConsumer(Session session, Destination destination, String selector) throws JMSException {
        return session.createConsumer(destination, selector);
    }

    /**
     * 创建持久订阅者
     */
    public static MessageConsumer createDurableSubscriber(Connection connection, Session session,
                                                          Topic topic, String subscriptionName) throws JMSException {
        return session.createDurableSubscriber(topic, subscriptionName);
    }

    /**
     * 关闭资源
     */
    public static void close(MessageProducer producer, Session session, Connection connection) {
        closeQuietly(producer);
        closeQuietly(session);
        closeQuietly(connection);
    }

    /**
     * 关闭资源（包含消费者）
     */
    public static void close(MessageConsumer consumer, Session session, Connection connection) {
        closeQuietly(consumer);
        closeQuietly(session);
        closeQuietly(connection);
    }

    private static void closeQuietly(Object resource) {
        if (resource == null) {
            return;
        }

        try {
            if (resource instanceof Connection) {
                ((Connection) resource).close();
            } else if (resource instanceof Session) {
                ((Session) resource).close();
            } else if (resource instanceof MessageConsumer) {
                ((MessageConsumer) resource).close();
            } else if (resource instanceof MessageProducer) {
                ((MessageProducer) resource).close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
