package com.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 主题消息监听器 (XML 配置方式)
 */
public class TopicMessageListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(TopicMessageListener.class);

    private int messageCount = 0;

    @Override
    public void onMessage(Message message) {
        try {
            messageCount++;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();

                // 获取消息属性
                String type = message.getStringProperty("type");
                String level = message.getStringProperty("level");
                long timestamp = message.getLongProperty("timestamp");

                logger.info("[XML主题监听器 #{}] [{}:{}] {} (时间: {})",
                    messageCount, type, level, text, timestamp);

            } else {
                logger.warn("[XML主题监听器] 收到非文本消息: {}", message.getClass().getName());
            }

        } catch (JMSException e) {
            logger.error("处理主题消息时出错", e);
        }
    }

    public int getMessageCount() {
        return messageCount;
    }
}
