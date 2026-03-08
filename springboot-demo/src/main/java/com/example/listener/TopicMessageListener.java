package com.example.listener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 主题消息监听器
 */
@Component
public class TopicMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(TopicMessageListener.class);

    private int messageCount = 0;

    /**
     * 监听默认主题
     * subscription 指定订阅名称，用于持久订阅
     * containerFactory 指定使用 Topic 监听器容器
     */
    @JmsListener(
        destination = "BOOT_TOPIC",
        containerFactory = "topicListenerFactory",
        subscription = "default-subscription"
    )
    public void onTopicMessage(String message,
                               @Header(JmsHeaders.TYPE) String type,
                               @Header(JmsHeaders.TIMESTAMP) long timestamp) {
        messageCount++;
        logger.info("[主题监听器 #{}] [TYPE:{}] 收到消息: {} (时间: {})",
            messageCount, type, message, timestamp);
    }

    /**
     * 监听通知主题
     */
    @JmsListener(
        destination = "NOTIFICATION_TOPIC",
        containerFactory = "topicListenerFactory",
        subscription = "notification-subscription"
    )
    public void onNotificationMessage(@Payload String message) {
        messageCount++;
        logger.info("[通知监听器 #{}] 收到通知: {}", messageCount, message);
    }

    /**
     * 使用消息选择器 - 只接收 SYSTEM 类型的消息
     */
    @JmsListener(
        destination = "BOOT_TOPIC",
        containerFactory = "topicListenerFactory",
        selector = "type = 'SYSTEM'",
        subscription = "system-subscription"
    )
    public void onSystemMessage(String message) {
        messageCount++;
        logger.info("[系统监听器 #{}] 收到系统消息: {}", messageCount, message);
    }

    /**
     * 手动确认消息
     */
    @JmsListener(
        destination = "BOOT_QUEUE",
        containerFactory = "queueListenerFactory"
    )
    public void onManualAcknowledge(Message message) throws JMSException {
        messageCount++;

        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();

            logger.info("[手动确认监听器 #{}] 收到消息: {}", messageCount, text);

            // 处理消息...

            // 手动确认消息
            message.acknowledge();
            logger.info("消息已确认");
        }
    }

    public int getMessageCount() {
        return messageCount;
    }
}
