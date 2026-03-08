package com.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 队列消息监听器 (注解方式)
 */
@Component
public class AnnotationQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationQueueListener.class);

    private int messageCount = 0;

    /**
     * 监听队列
     * containerFactory 指定监听器容器工厂
     */
    @JmsListener(destination = "SPRING_QUEUE", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message) {
        messageCount++;
        logger.info("[注解监听器] 收到队列消息 #{}: {}", messageCount, message);
    }

    /**
     * 监听订单队列
     */
    @JmsListener(destination = "ORDER_QUEUE", containerFactory = "jmsListenerContainerFactory")
    public void onOrderMessage(String message) {
        messageCount++;
        logger.info("[注解监听器] 收到订单消息 #{}: {}", messageCount, message);
    }

    public int getMessageCount() {
        return messageCount;
    }
}
