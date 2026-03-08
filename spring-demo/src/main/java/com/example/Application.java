package com.example;

import com.example.producer.SerializableObject;
import com.example.producer.SpringQueueProducer;
import com.example.producer.SpringTopicPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Spring ActiveMQ 应用入口
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  ActiveMQ Spring 整合示例");
        System.out.println("========================================\n");

        // 加载 Spring 配置
        System.out.println("加载 Spring 容器...");
        ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("spring-context.xml");
        context.start();

        // 获取 Bean
        SpringQueueProducer queueProducer = context.getBean("springQueueProducer", SpringQueueProducer.class);
        SpringTopicPublisher topicPublisher = context.getBean("springTopicPublisher", SpringTopicPublisher.class);

        System.out.println("Spring 容器启动成功！\n");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            System.out.print("请选择操作 (0-退出): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    // 发送简单消息
                    System.out.print("请输入消息内容: ");
                    String message = scanner.nextLine();
                    queueProducer.sendTextMessage(message);
                    break;

                case "2":
                    // 发送带属性的消息
                    System.out.print("请输入消息内容: ");
                    String content = scanner.nextLine();
                    Map<String, Object> props = new HashMap<>();
                    props.put("userId", System.currentTimeMillis() % 1000);
                    props.put("userName", "User-" + (int)(Math.random() * 100));
                    props.put("timestamp", System.currentTimeMillis());
                    queueProducer.sendTextMessageWithProperties(content, props);
                    break;

                case "3":
                    // 发送对象消息
                    SerializableObject obj = new SerializableObject(
                        System.currentTimeMillis(),
                        "Object-" + (int)(Math.random() * 100),
                        "这是一个测试对象"
                    );
                    queueProducer.sendObjectMessage(obj);
                    break;

                case "4":
                    // 批量发送
                    queueProducer.sendBatch(10);
                    break;

                case "5":
                    // 发布主题消息
                    System.out.print("请输入主题消息内容: ");
                    String topicMsg = scanner.nextLine();
                    topicPublisher.publishTextMessage(topicMsg);
                    break;

                case "6":
                    // 发布各种消息
                    topicPublisher.publishVariousMessages();
                    break;

                case "7":
                    // 批量发布
                    topicPublisher.publishBatch(5);
                    break;

                case "0":
                    // 退出
                    System.out.println("\n正在关闭 Spring 容器...");
                    context.close();
                    System.out.println("再见！");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("无效的选择，请重新输入！");
            }

            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("========== 菜单 ==========");
        System.out.println("1. 发送队列消息 (简单)");
        System.out.println("2. 发送队列消息 (带属性)");
        System.out.println("3. 发送对象消息");
        System.out.println("4. 批量发送队列消息");
        System.out.println("5. 发布主题消息");
        System.out.println("6. 发布各种主题消息");
        System.out.println("7. 批量发布主题消息");
        System.out.println("0. 退出");
        System.out.println("==========================");
    }
}
