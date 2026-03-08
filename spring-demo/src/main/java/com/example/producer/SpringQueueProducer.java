package com.example.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring 队列消息生产者
 */
@Component("springQueueProducer")
public class SpringQueueProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("springQueue")
    private Queue springQueue;

    /**
     * 发送简单文本消息
     */
    public void sendTextMessage(String message) {
        System.out.println("发送消息: " + message);
        jmsTemplate.send(springQueue, session -> {
            TextMessage textMessage = session.createTextMessage(message);
            return textMessage;
        });
    }

    /**
     * 发送带属性的消息
     */
    public void sendTextMessageWithProperties(String content, Map<String, Object> properties) {
        System.out.println("发送消息: " + content);
        jmsTemplate.send(springQueue, session -> {
            TextMessage textMessage = session.createTextMessage(content);

            // 设置属性
            if (properties != null) {
                properties.forEach((key, value) -> {
                    try {
                        if (value instanceof String) {
                            textMessage.setStringProperty(key, (String) value);
                        } else if (value instanceof Integer) {
                            textMessage.setIntProperty(key, (Integer) value);
                        } else if (value instanceof Long) {
                            textMessage.setLongProperty(key, (Long) value);
                        } else if (value instanceof Boolean) {
                            textMessage.setBooleanProperty(key, (Boolean) value);
                        } else if (value instanceof Double) {
                            textMessage.setDoubleProperty(key, (Double) value);
                        }
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
    public void sendObjectMessage(SerializableObject obj) {
        System.out.println("发送对象消息: " + obj);
        jmsTemplate.send(springQueue, session -> session.createObjectMessage(obj));
    }

    /**
     * 批量发送消息
     */
    public void sendBatch(int count) {
        System.out.println("开始批量发送 " + count + " 条消息...");
        for (int i = 1; i <= count; i++) {
            Map<String, Object> props = new HashMap<>();
            props.put("messageId", i);
            props.put("batch", "batch-" + (i % 3 + 1));
            props.put("timestamp", System.currentTimeMillis());

            sendTextMessageWithProperties("批量消息 " + i, props);
        }
        System.out.println("批量发送完成");
    }

    /**
     * 使用默认目的地发送消息
     */
    public void sendToDefault(String message) {
        System.out.println("发送到默认目的地: " + message);
        jmsTemplate.send(session -> {
            TextMessage textMessage = session.createTextMessage(message);
            return textMessage;
        });
    }

    /**
     * 使用 convertAndSend 发送消息
     */
    public void convertAndSend(String message) {
        System.out.println("使用 convertAndSend 发送: " + message);
        jmsTemplate.convertAndSend(springQueue, message);
    }

    /**
     * 使用 post-processor 发送消息
     */
    public void sendWithPostProcessor(String message) {
        System.out.println("发送带 Post-Processor 的消息: " + message);
        jmsTemplate.convertAndSend(springQueue, message, msg -> {
            msg.setIntProperty("processed", 1);
            msg.setStringProperty("processorType", "POST_PROCESSOR");
            return msg;
        });
    }
}
