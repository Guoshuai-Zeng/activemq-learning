package com.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 队列消息监听器 (XML 配置方式)
 */
public class QueueMessageListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(QueueMessageListener.class);

    private int messageCount = 0;

    @Override
    public void onMessage(Message message) {
        try {
            messageCount++;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();

                logger.info("[XML监听器] 收到消息 #{}: {}", messageCount, text);

                // 获取消息属性
                if (message.propertyExists("messageId")) {
                    int messageId = message.getIntProperty("messageId");
                    String batch = message.getStringProperty("batch");
                    logger.info("消息属性 - ID: {}, Batch: {}", messageId, batch);
                }

                if (message.propertyExists("processed")) {
                    int processed = message.getIntProperty("processed");
                    String processorType = message.getStringProperty("processorType");
                    logger.info("处理器 - processed: {}, type: {}", processed, processorType);
                }

            } else {
                logger.warn("[XML监听器] 收到非文本消息: {}", message.getClass().getName());
            }

        } catch (JMSException e) {
            logger.error("处理消息时出错", e);
        }
    }

    public int getMessageCount() {
        return messageCount;
    }
}
