package com.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 主题消息监听器 (注解方式)
 */
@Component
public class AnnotationTopicListener {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationTopicListener.class);

    private int messageCount = 0;

    /**
     * 监听主题
     * subscription 指定订阅名称，用于持久订阅
     */
    @JmsListener(
        destination = "SPRING_TOPIC",
        containerFactory = "jmsListenerContainerFactory",
        subscription = "annotation-durable-subscription"
    )
    public void onTopicMessage(String message) {
        messageCount++;
        logger.info("[注解主题监听器 #{}] 收到主题消息: {}", messageCount, message);
    }

    /**
     * 使用消息选择器
     * 只接收 level = 'WARN' 的消息
     */
    @JmsListener(
        destination = "SPRING_TOPIC",
        containerFactory = "jmsListenerContainerFactory",
        selector = "level = 'WARN'",
        subscription = "warn-subscription"
    )
    public void onWarnMessage(String message) {
        messageCount++;
        logger.warn("[警告监听器 #{}] 收到警告消息: {}", messageCount, message);
    }

    public int getMessageCount() {
        return messageCount;
    }
}
