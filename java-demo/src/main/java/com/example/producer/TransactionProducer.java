package com.example.producer;

import com.example.common.ActiveMQUtil;

import javax.jms.*;

/**
 * 事务消息生产者
 * 演示使用事务发送消息
 */
public class TransactionProducer {

    private static final String QUEUE_NAME = "TRANSACTION_QUEUE";

    public static void main(String[] args) {
        ConnectionFactory factory = ActiveMQUtil.createConnectionFactory();

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            // 创建连接
            connection = ActiveMQUtil.createConnection(factory);

            // 创建事务会话
            session = ActiveMQUtil.createSession(connection, true, Session.SESSION_TRANSACTED);

            // 创建队列
            Queue queue = ActiveMQUtil.createQueue(session, QUEUE_NAME);

            // 创建消息生产者
            producer = ActiveMQUtil.createProducer(session, queue);

            System.out.println("===== 开始发送事务消息 =====");

            try {
                // 第一批消息 - 将被提交
                System.out.println("--- 批次 1: 正常提交 ---");
                for (int i = 1; i <= 3; i++) {
                    TextMessage message = session.createTextMessage();
                    message.setText("事务消息-批次1-" + i);
                    message.setIntProperty("batch", 1);
                    producer.send(message);
                    System.out.println("发送: " + message.getText());
                }
                // 提交事务
                session.commit();
                System.out.println("批次1 事务已提交\n");

                // 模拟延迟
                Thread.sleep(1000);

                // 第二批消息 - 将被回滚
                System.out.println("--- 批次 2: 将被回滚 ---");
                for (int i = 1; i <= 3; i++) {
                    TextMessage message = session.createTextMessage();
                    message.setText("事务消息-批次2-" + i);
                    message.setIntProperty("batch", 2);
                    producer.send(message);
                    System.out.println("发送: " + message.getText());
                }
                // 模拟错误，回滚事务
                session.rollback();
                System.out.println("批次2 事务已回滚\n");

                // 第三批消息 - 正常提交
                System.out.println("--- 批次 3: 正常提交 ---");
                for (int i = 1; i <= 3; i++) {
                    TextMessage message = session.createTextMessage();
                    message.setText("事务消息-批次3-" + i);
                    message.setIntProperty("batch", 3);
                    producer.send(message);
                    System.out.println("发送: " + message.getText());
                }
                // 提交事务
                session.commit();
                System.out.println("批次3 事务已提交\n");

                System.out.println("===== 事务消息发送完成 =====");
                System.out.println("消费者应该只收到批次1和批次3的消息");

            } catch (Exception e) {
                System.err.println("发生异常，回滚事务");
                session.rollback();
                throw e;
            }

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            ActiveMQUtil.close(producer, session, connection);
        }
    }
}
