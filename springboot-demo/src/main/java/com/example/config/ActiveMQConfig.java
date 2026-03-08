package com.example.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * ActiveMQ 配置类
 */
@Configuration
@EnableJms
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String username;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("${activemq.queue.default}")
    private String defaultQueue;

    @Value("${activemq.queue.order}")
    private String orderQueue;

    @Value("${activemq.topic.default}")
    private String defaultTopic;

    @Value("${activemq.topic.notification}")
    private String notificationTopic;

    /**
     * ActiveMQ 连接工厂
     */
    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        factory.setUserName(username);
        factory.setPassword(password);
        factory.setTrustAllPackages(true);
        return factory;
    }

    /**
     * Pooled 连接工厂
     * 使用连接池可以提高性能
     */
    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory pooledFactory = new PooledConnectionFactory();
        pooledFactory.setConnectionFactory(activeMQConnectionFactory());
        pooledFactory.setMaxConnections(10);
        pooledFactory.setMaximumActiveSessionPerConnection(500);
        pooledFactory.setIdleTimeout(30000);
        return pooledFactory;
    }

    /**
     * 缓存连接工厂
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(pooledConnectionFactory());
    }

    /**
     * JMS 监听器容器工厂（Queue）
     */
    @Bean(name = "queueListenerFactory")
    public JmsListenerContainerFactory<?> queueListenerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(false);  // Queue 模式
        factory.setConcurrency("3-10");  // 并发数量
        factory.setSessionTransacted(false);
        factory.setSessionAcknowledgeMode(1);  // CLIENT_ACKNOWLEDGE
        return factory;
    }

    /**
     * JMS 监听器容器工厂（Topic）
     */
    @Bean(name = "topicListenerFactory")
    public JmsListenerContainerFactory<?> topicListenerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true);  // Topic 模式
        factory.setConcurrency("1-5");
        factory.setSubscriptionDurable(true);  // 持久订阅
        factory.setClientId("springboot-topic-client");
        return factory;
    }

    /**
     * JMS 模板（Queue）
     */
    @Bean(name = "queueJmsTemplate")
    public JmsTemplate queueJmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.setDefaultDestinationName(defaultQueue);
        jmsTemplate.setDeliveryMode(2);  // PERSISTENT
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setTimeToLive(3600000);  // 1小时
        return jmsTemplate;
    }

    /**
     * JMS 模板（Topic）
     */
    @Bean(name = "topicJmsTemplate")
    public JmsTemplate topicJmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.setDefaultDestinationName(defaultTopic);
        jmsTemplate.setDeliveryMode(1);  // NON_PERSISTENT
        jmsTemplate.setExplicitQosEnabled(true);
        return jmsTemplate;
    }

    /**
     * 默认队列
     */
    @Bean
    public Queue defaultQueue() {
        return new ActiveMQQueue(defaultQueue);
    }

    /**
     * 订单队列
     */
    @Bean
    public Queue orderQueue() {
        return new ActiveMQQueue(orderQueue);
    }

    /**
     * 默认主题
     */
    @Bean
    public Topic defaultTopic() {
        return new ActiveMQTopic(defaultTopic);
    }

    /**
     * 通知主题
     */
    @Bean
    public Topic notificationTopic() {
        return new ActiveMQTopic(notificationTopic);
    }
}
