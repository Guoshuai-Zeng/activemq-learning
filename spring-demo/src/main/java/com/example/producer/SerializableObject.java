package com.example.producer;

import java.io.Serializable;

/**
 * 可序列化对象
 * 用于演示对象消息
 */
public class SerializableObject implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Long createTime;

    public SerializableObject() {
    }

    public SerializableObject(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createTime = System.currentTimeMillis();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SerializableObject{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", createTime=" + createTime +
            '}';
    }
}
