package com.example.listener;

import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 队列消息监听器
 */
@Component
public class QueueMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(QueueMessageListener.class);

    private int messageCount = 0;

    /**
     * 监听默认队列
     * containerFactory 指定使用 Queue 监听器容器
     */
    @JmsListener(
        destination = "BOOT_QUEUE",
        containerFactory = "queueListenerFactory"
    )
    public void onMessage(String message) {
        messageCount++;
        logger.info("[队列监听器] 收到消息 #{}: {}", messageCount, message);
    }

    /**
     * 监听订单队列
     */
    @JmsListener(
        destination = "ORDER_QUEUE",
        containerFactory = "queueListenerFactory"
    )
    public void onOrderMessage(String message) {
        messageCount++;
        logger.info("[订单监听器] 收到订单消息 #{}: {}", messageCount, message);
    }

    /**
     * 接收对象消息
     */
    @JmsListener(
        destination = "BOOT_QUEUE",
        containerFactory = "queueListenerFactory",
        selector = "type = 'OBJECT'"
    )
    public void onObjectMessage(SerializableObject object) {
        messageCount++;
        logger.info("[对象监听器] 收到对象消息 #{}: {}", messageCount, object);
    }

    public int getMessageCount() {
        return messageCount;
    }
}
