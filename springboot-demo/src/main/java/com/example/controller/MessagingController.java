package com.example.controller;

import com.example.listener.QueueMessageListener;
import com.example.listener.TopicMessageListener;
import com.example.service.MessageProducerService;
import com.example.service.SerializableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息控制器
 * 提供 REST API 接口
 */
@RestController
@RequestMapping("/api/messaging")
public class MessagingController {

    @Autowired
    private MessageProducerService messageProducerService;

    @Autowired
    private QueueMessageListener queueMessageListener;

    @Autowired
    private TopicMessageListener topicMessageListener;

    /**
     * 发送队列消息
     */
    @PostMapping("/queue/send")
    public ResponseEntity<Map<String, Object>> sendQueueMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "消息不能为空"));
        }

        messageProducerService.sendQueueMessage(message);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "消息已发送到队列",
            "content", message
        ));
    }

    /**
     * 发送带属性的队列消息
     */
    @PostMapping("/queue/send-with-props")
    public ResponseEntity<Map<String, Object>> sendQueueMessageWithProps(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) request.get("properties");

        if (content == null || content.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "内容不能为空"));
        }

        messageProducerService.sendQueueMessageWithProperties(content, properties);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "带属性的消息已发送",
            "content", content
        ));
    }

    /**
     * 发送对象消息
     */
    @PostMapping("/queue/send-object")
    public ResponseEntity<Map<String, Object>> sendObjectMessage(@RequestBody Map<String, Object> request) {
        Long id = request.get("id") != null ? Long.valueOf(request.get("id").toString()) : System.currentTimeMillis();
        String name = (String) request.get("name");
        String description = (String) request.get("description");

        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "名称不能为空"));
        }

        SerializableObject object = new SerializableObject(id, name, description);
        messageProducerService.sendObjectMessage(object);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "对象消息已发送",
            "object", object
        ));
    }

    /**
     * 批量发送队列消息
     */
    @PostMapping("/queue/batch")
    public ResponseEntity<Map<String, Object>> sendBatchMessages(@RequestParam(defaultValue = "10") int count) {
        messageProducerService.sendBatchQueueMessages(count);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", count + " 条消息已发送",
            "count", count
        ));
    }

    /**
     * 发布主题消息
     */
    @PostMapping("/topic/publish")
    public ResponseEntity<Map<String, Object>> publishTopicMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String type = request.getOrDefault("type", "DEFAULT");

        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "消息不能为空"));
        }

        messageProducerService.publishTopicMessage(message, type);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "消息已发布到主题",
            "content", message,
            "type", type
        ));
    }

    /**
     * 批量发布主题消息
     */
    @PostMapping("/topic/batch")
    public ResponseEntity<Map<String, Object>> publishBatchMessages(@RequestParam(defaultValue = "5") int count) {
        messageProducerService.publishBatchTopicMessages(count);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", count + " 条消息已发布",
            "count", count
        ));
    }

    /**
     * 发送并接收消息
     */
    @PostMapping("/queue/send-receive")
    public ResponseEntity<Map<String, Object>> sendAndReceive(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "消息不能为空"));
        }

        String response = messageProducerService.sendAndReceive(message);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "request", message,
            "response", response
        ));
    }

    /**
     * 获取统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("queueListenerMessageCount", queueMessageListener.getMessageCount());
        stats.put("topicListenerMessageCount", topicMessageListener.getMessageCount());

        return ResponseEntity.ok(stats);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "ActiveMQ Messaging Service"
        ));
    }
}
