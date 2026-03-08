package com.example.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring 主题消息发布者
 */
@Component("springTopicPublisher")
public class SpringTopicPublisher {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("springTopic")
    private Topic springTopic;

    /**
     * 发布简单文本消息
     */
    public void publishTextMessage(String message) {
        System.out.println("发布主题消息: " + message);
        jmsTemplate.send(springTopic, session -> {
            TextMessage textMessage = session.createTextMessage(message);
            return textMessage;
        });
    }

    /**
     * 发布带属性的消息
     */
    public void publishWithProperties(String content, String type, String level) {
        System.out.println("发布主题消息: " + content);
        jmsTemplate.send(springTopic, session -> {
            TextMessage textMessage = session.createTextMessage(content);

            textMessage.setStringProperty("type", type);
            textMessage.setStringProperty("level", level);
            textMessage.setLongProperty("timestamp", System.currentTimeMillis());

            return textMessage;
        });
    }

    /**
     * 发布不同类型的消息
     */
    public void publishVariousMessages() {
        System.out.println("\n===== 开始发布各种主题消息 =====");

        publishWithProperties("系统启动完成", "SYSTEM", "INFO");
        pause(500);

        publishWithProperties("用户登录成功", "USER", "INFO");
        pause(500);

        publishWithProperties("订单创建成功", "ORDER", "INFO");
        pause(500);

        publishWithProperties("支付成功", "PAYMENT", "INFO");
        pause(500);

        publishWithProperties("警告：系统负载较高", "SYSTEM", "WARN");
        pause(500);

        publishWithProperties("订单取消", "ORDER", "INFO");
        pause(500);

        System.out.println("===== 主题消息发布完成 =====\n");
    }

    /**
     * 使用 convertAndSend 发布
     */
    public void convertAndPublish(String message) {
        System.out.println("使用 convertAndPublish 发布: " + message);
        jmsTemplate.convertAndSend(springTopic, message);
    }

    /**
     * 批量发布消息
     */
    public void publishBatch(int count) {
        System.out.println("开始批量发布 " + count + " 条消息...");
        String[] types = {"SYSTEM", "USER", "ORDER", "PAYMENT", "NOTIFICATION"};

        for (int i = 1; i <= count; i++) {
            String type = types[i % types.length];
            publishWithProperties("批量消息 " + i, type, "INFO");
        }
        System.out.println("批量发布完成");
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
