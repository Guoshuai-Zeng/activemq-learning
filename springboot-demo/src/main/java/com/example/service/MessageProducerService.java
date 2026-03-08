package com.example.service;

import jakarta.jms.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息生产者服务
 */
@Service
public class MessageProducerService {

    private final JmsTemplate queueJmsTemplate;
    private final JmsTemplate topicJmsTemplate;
    private final Queue defaultQueue;
    private final Topic defaultTopic;

    public MessageProducerService(
            @Qualifier("queueJmsTemplate") JmsTemplate queueJmsTemplate,
            @Qualifier("topicJmsTemplate") JmsTemplate topicJmsTemplate,
            Queue defaultQueue,
            Topic defaultTopic) {
        this.queueJmsTemplate = queueJmsTemplate;
        this.topicJmsTemplate = topicJmsTemplate;
        this.defaultQueue = defaultQueue;
        this.defaultTopic = defaultTopic;
    }

    /**
     * 发送队列消息（文本）
     */
    public void sendQueueMessage(String message) {
        System.out.println("发送队列消息: " + message);
        queueJmsTemplate.send(defaultQueue, session -> {
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setLongProperty("timestamp", System.currentTimeMillis());
            return textMessage;
        });
    }

    /**
     * 发送队列消息（带属性）
     */
    public void sendQueueMessageWithProperties(String content, Map<String, Object> properties) {
        System.out.println("发送带属性的队列消息: " + content);
        queueJmsTemplate.send(defaultQueue, session -> {
            TextMessage textMessage = session.createTextMessage(content);

            if (properties != null) {
                properties.forEach((key, value) -> {
                    try {
                        setProperty(textMessage, key, value);
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            return textMessage;
        });
    }

    /**
     * 发送对象消息
     */
    public void sendObjectMessage(SerializableObject object) {
        System.out.println("发送对象消息: " + object);
        queueJmsTemplate.send(defaultQueue, session -> session.createObjectMessage(object));
    }

    /**
     * 发布主题消息
     */
    public void publishTopicMessage(String message, String type) {
        System.out.println("发布主题消息 [" + type + "]: " + message);
        topicJmsTemplate.send(defaultTopic, session -> {
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setStringProperty("type", type);
            textMessage.setLongProperty("timestamp", System.currentTimeMillis());
            return textMessage;
        });
    }

    /**
     * 批量发送队列消息
     */
    public void sendBatchQueueMessages(int count) {
        System.out.println("开始批量发送 " + count + " 条队列消息...");
        for (int i = 1; i <= count; i++) {
            Map<String, Object> props = new HashMap<>();
            props.put("messageId", i);
            props.put("batch", "batch-" + (i % 3 + 1));
            sendQueueMessageWithProperties("批量消息 " + i, props);
        }
        System.out.println("批量发送完成");
    }

    /**
     * 批量发布主题消息
     */
    public void publishBatchTopicMessages(int count) {
        System.out.println("开始批量发布 " + count + " 条主题消息...");
        String[] types = {"SYSTEM", "USER", "ORDER", "PAYMENT", "NOTIFICATION"};

        for (int i = 1; i <= count; i++) {
            String type = types[i % types.length];
            publishTopicMessage("批量主题消息 " + i, type);
        }
        System.out.println("批量发布完成");
    }

    /**
     * 使用 convertAndSend 发送消息
     */
    public void convertAndSend(String message) {
        System.out.println("使用 convertAndSend 发送: " + message);
        queueJmsTemplate.convertAndSend(defaultQueue, message);
    }

    /**
     * 使用 sendAndReceive 发送并接收
     */
    public String sendAndReceive(String message) {
        System.out.println("发送并接收消息: " + message);
        return queueJmsTemplate.sendAndReceive(defaultQueue, session -> {
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setJMSCorrelationID("CORR-" + System.currentTimeMillis());
            return textMessage;
        });
    }

    private void setProperty(TextMessage message, String key, Object value) throws JMSException {
        if (value instanceof String) {
            message.setStringProperty(key, (String) value);
        } else if (value instanceof Integer) {
            message.setIntProperty(key, (Integer) value);
        } else if (value instanceof Long) {
            message.setLongProperty(key, (Long) value);
        } else if (value instanceof Boolean) {
            message.setBooleanProperty(key, (Boolean) value);
        } else if (value instanceof Double) {
            message.setDoubleProperty(key, (Double) value);
        }
    }
}
